package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {
    //Firebase Authentication & Database
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Textfields to display the data of the current logged in user
    private TextView Username_Main, Email_Main, Password_Main, Firebase_ID;

    //Log-out button
    private Button Logout_Button;

    //Handler to change the Textview of the UI
    private Handler mainHandler;

    //Tag used to display logs
    private static final String TAG = "MainActivity";


    /**
     * Creates the Mainactivity and all UI compontents
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Components to display the user data
        Username_Main = findViewById(R.id.Username_Main);
        Email_Main = findViewById(R.id.Email_Main);
        Password_Main = findViewById(R.id.Password_Main);
        Firebase_ID = findViewById(R.id.FirebaseID);

        //Log-out button
        Logout_Button = findViewById(R.id.LogoutButton_Main);
        Logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogout();
            }
        });

        //Reference to Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Runs everytime the Mainactivity gets opened again after the creation
     * includes a Thread that updates the UI components to show the users data
     */
    @Override
    protected void onStart() {
        super.onStart();
        try {
            UIThread uiThread = new UIThread();
            uiThread.start();
        }catch (Exception e){

        }
    }

    //TODO Optional: block the usage of backpressed()

    /**
     * Logs out the current user and directs him to the Login & Registeractivity
     */
    public void userLogout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    /**
     * Searches the current user int the Firebase collection to make a new Object
     * which will be used to update the UI
     * @param currentUser
     */
    private void getUserData(FirebaseUser currentUser) {
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.getDocuments().stream()
                        .forEach(entry -> {
                            if (entry.toObject(User.class).getEmail().equals(currentUser.getEmail())){
                                //User user = entry.toObject(User.class);
                                User user = new User(entry.toObject(User.class).getEmail(), entry.toObject(User.class).getUsername(), entry.toObject(User.class).getPassword());
                                Log.w(TAG, "Found user: " + user.toString());
                                String id = entry.getId();
                                updateUI(user, id);
                            }
                        });
            }
        });
    }

    /**
     * Displays the Data of the current user that is logged in
     * @param user Object of the current user
     * @param id Document ID of the User that is located in the "users" collection
     */
    private void updateUI(User user, String id){
        mainHandler = new Handler();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Username_Main.setText(user.getUsername());
                Email_Main.setText(user.getEmail());
                Password_Main.setText(user.getPassword());
                Firebase_ID.setText(id);
            }
        });
    }

    /**
     * Creates a new thread so the User Data can be displayed without the app crashes
     */
    class UIThread extends Thread {
        @Override
        public void run() {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                getUserData(currentUser);
            }else {
                Log.w(TAG, "User is not logged in");
            }
        }
    }
}
