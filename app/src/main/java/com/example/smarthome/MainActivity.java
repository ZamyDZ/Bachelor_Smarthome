package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Adapter.RoomAdapter;
import com.example.smarthome.model.Room;
import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, CreateRoomFragment.OnFragmentInteractionListener {
    //Firebase Authentication & FireStore & Currentuser
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    //Recyclerview of the rooms
    private RecyclerView roomRecyclerview;
    private RoomAdapter roomAdapter;
    private RecyclerView.LayoutManager roomLayoutManager;

    //Textfields to display the data of the current logged in user
    private TextView Username_Main, Email_Main;

    //ImageviewButtons
    private ImageView UserLogout_Button, Refresh_Button;

    //Floating action button
    private FloatingActionButton floatButton;

    //Handler to change the Textview of the UI
    private Handler mainHandler;

    //ArrayList of Rooms that the user has
    private static ArrayList<Room> roomList;

    //Tag used to display logs
    private static final String TAG = "MainActivity";

    //UID of The current User
    private String currentUID, roomID;

    //Countdownlatch for the getRooms thread
    private CountDownLatch roomLatch = new CountDownLatch(1);

    //Declaration of the Create_Room Fragment Container
    private FrameLayout CreateRoomFramelayout;

    /**
     * Creates the View of the Mainactivity and all UI compontents
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //List of Rooms
        roomList = new ArrayList<>();

        //Reference to Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Components to display the user data
        Username_Main = findViewById(R.id.Username_Main);
        Email_Main = findViewById(R.id.Email_Main);

        //Floating action button
        floatButton = findViewById(R.id.Floating_Button_Main);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateRoomFragment();
            }
        });

        //UserLogout button
        UserLogout_Button = findViewById(R.id.Toolbar_User_Logout);
        UserLogout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutMenu(v);
            }
        });

        //Refresh-button
        Refresh_Button = findViewById(R.id.Toolbar_Refresh);
        Refresh_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
            }
        });

        /**
         * FRAGMENT CONTAINER
         */
        //Create room Container Framelayout
        CreateRoomFramelayout = findViewById(R.id.CreateRoomContainer);


        //Thread to get the current user
        Thread uiThread = new Thread(() -> {
            if (!currentUser.equals(null)) {
                getUserData(currentUser);
            }else {
                Log.w(TAG, "User is not logged in");
            }
        });

        //Thread to get the users "rooms" collection this waits until user data is arrived
        Thread getRoomsThread = new Thread(() -> {
            try {
                roomLatch.await();
                getRoomsOfUser();
                createRecyclerview();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        //starting both threads
        uiThread.start();
        getRoomsThread.start();
        try {
            uiThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Refreshing the user roomslist with the RefreshButton
     */
    private void showList(){
        getRoomsOfUser();
    }

    /**
     * Creates an Instance of the Frgament and opens it
     */
    private void openCreateRoomFragment(){
        CreateRoomFragment createRoomFragment = CreateRoomFragment.newInstance();
        FragmentManager createRoomFragmentManager = getSupportFragmentManager();
        FragmentTransaction roomtransaction = createRoomFragmentManager.beginTransaction();
        roomtransaction.addToBackStack(null);
        roomtransaction.add(R.id.CreateRoomContainer, createRoomFragment, "CREATEROOM FRAGMENT").commit();
    }

    /**
     * Runs everytime the Mainactivity gets opened again after the creation
     * includes a Thread that updates the UI components to show the users data
     */
    @Override
    protected void onStart() {
        super.onStart();
        try {
        }catch (Exception e){

        }
    }

    /**
     * Opens a popup if the user clicks the userlogo
     * @param view
     */
    private void logoutMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.logout_main);
        popupMenu.show();
    }

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
     * @param currentUser user that is currently logged in
     */
    private void getUserData(FirebaseUser currentUser) {
        String userEmail = currentUser.getEmail();
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //finding the first user in the database that has the same email as the current user
                queryDocumentSnapshots.getDocuments()
                        .stream()
                        .filter(val -> val.toObject(User.class).getEmail().equals(userEmail))
                        .limit(1)
                        .forEach(entry -> {
                            //get the user UID so later the rooms can be loaded
                            currentUID = entry.getId();
                            //Creating the user out of the Documentsnapshot
                            User user = new User(entry.toObject(User.class).getEmail(), entry.toObject(User.class).getUsername(), entry.toObject(User.class).getPassword());
                            Log.w(TAG, "Found user: " + user.toString());
                            String id = entry.getId();
                            //updating the UI so the Username and Email can be displayed
                            updateUI(user, id);
                            //countdown the latch so the rooms can be loaded
                            roomLatch.countDown();
                        });
            }
        });
    }

    /**
     * Creates the recyclerview where all the rooms are displayed in cards
     */
    private void createRecyclerview(){
        roomRecyclerview = findViewById(R.id.RecyclerViewMain);
        roomRecyclerview.setHasFixedSize(true);
        roomLayoutManager = new LinearLayoutManager(this);
        roomAdapter = new RoomAdapter(roomList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomRecyclerview.setLayoutManager(roomLayoutManager);
                roomRecyclerview.setAdapter(roomAdapter);
            }
        });

        //TODO CHANGE HERE STUFF TESTING
        roomAdapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //get object of the select room
                Room selected = roomList.get(position);

                //open Dashboard
                Intent dashboard = new Intent(MainActivity.this, DashboardActivity.class);

                CountDownLatch waitLatch = new CountDownLatch(1);

                Thread first = new Thread(() -> {
                    //get room ID
                    db.collection("users")
                            .document(currentUID)
                            .collection("rooms")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    queryDocumentSnapshots.getDocuments()
                                            .stream()
                                            .forEach(entry -> {
                                                if(selected.equals(entry.toObject(Room.class))){
                                                    roomID = entry.getId();
                                                    Log.w(TAG, "202 FOUND ROOM ID: " + roomID);
                                                    waitLatch.countDown();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                });

                Thread second = new Thread(() -> {
                    try {
                        waitLatch.await();
                        //transfer the room object
                        dashboard.putExtra("Room", selected);
                        //transfer user id
                        dashboard.putExtra("userID", currentUID);
                        //transfer room id
                        dashboard.putExtra("roomID", roomID);

                        MainActivity.this.startActivity(dashboard);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });

                first.start();
                second.start();

                try {
                    first.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * this function resets the actual roomslist if it is not empty,
     * then making a query to get all rooms of the current user.
     */
    private void getRoomsOfUser(){
        //reset roomList if the list conains more then zero elements
        if(roomList.size() > 0){
            roomList.clear();
        }
        //getting the "rooms" collection of the user and adding each to the roomslist
        db.collection("users")
                .document(currentUID)
                .collection("rooms")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        queryDocumentSnapshots
                                .forEach(room -> {
                                    roomList.add(room.toObject(Room.class));
                                });
                        //updating the current roomList
                        roomAdapter.updateRoomList(roomList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "ERROR: Couldn't load rooms.");
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
            }
        });
    }

    /**
     * this function gets called if the user hits the user-icon and then hits logout
     * user will get logged out and directed to the Login-Activity
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemLogout:
                userLogout();
                return true;
            default:
                return false;
        }
    }

    /**
     * Handeling onBackpressed when the fragment is open
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }

}
