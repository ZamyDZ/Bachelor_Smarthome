package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginRegisterActivity extends AppCompatActivity implements RegisterFragment.OnFragmentInteractionListener{
    //Logo
    private ImageView smartHomeLogo;
    //Login & Register-Button
    private Button LoginButton, RegisterButton;
    //EditTexts of the LoginRegisterActivity & RegisterFragment
    private EditText EmailInput, PasswordInput;
    //Declaration of the Register Fragement Container
    private FrameLayout RegisterFramelayout;

    //Firebase Authentication an reference to the database
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Creating the Login & Registeractivity with Buttons, Textviews & the Registerfragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        //Logo
        smartHomeLogo = findViewById(R.id.SmartHomeLogo);
        smartHomeLogo.setImageResource(R.drawable.smarhomelogo);

        //Login button
        LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        //Register button
        RegisterButton = findViewById(R.id.RegisterButton);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterFragment();
            }
        });

        //Email Input Login
        EmailInput = findViewById(R.id.Emailinput);
        //Password Input Login
        PasswordInput = findViewById(R.id.Passwordinput);

        /**
         * FRAGMENT Container
         */
        //Register Container Framelayout
        RegisterFramelayout = findViewById(R.id.RegisterFragment_Container);

        //Firebase Authentication & Database
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new Fragment and directs the user to it
     */
    private void openRegisterFragment(){
        RegisterFragment registerFragment = RegisterFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.RegisterFragment_Container, registerFragment, "REGISTER FRAGMENT").commit();
    }

    /**
     * Opens the MainActivity
     */
    private void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Everytime the activtiy starts:
     * check on every start of the Login&Registeractivity if the user already logged in
     */
    @Override
    protected void onStart() {
        super.onStart();
        userLogInStatus();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }

    /**
     * Takes the Input of the Email& -Passwordinput and makes a request to Firebase Authentication
     * so the User can be logged in.
     */
    private void LoginUser(){
        mAuth.signInWithEmailAndPassword(EmailInput.getText().toString(), PasswordInput.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(LoginRegisterActivity.this, "Login was successful", Toast.LENGTH_SHORT).show();
                openMainActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginRegisterActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * checks if the user is already logged in
     * if this is true he get directed to the mainactivity
     */
    private void userLogInStatus(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            openMainActivity();
        }
    }

}
