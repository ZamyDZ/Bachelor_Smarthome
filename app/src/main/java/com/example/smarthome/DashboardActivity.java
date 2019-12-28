package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Adapter.DeviceAdapter;
import com.example.smarthome.Adapter.RoomAdapter;
import com.example.smarthome.model.Device;
import com.example.smarthome.model.DeviceType;
import com.example.smarthome.model.Room;
import com.example.smarthome.model.Toaster;
import com.example.smarthome.model.Washingmachine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class DashboardActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, CreateDeviceFragment.OnFragmentInteractionListener, ManageDeviceFragment.OnFragmentInteractionListener{
    //Firebase Authentication & FireStore & Currentuser
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    //Recyclerview & Adapter
    private RecyclerView deviceRecyclerview;
    private DeviceAdapter deviceAdapter;
    private RecyclerView.LayoutManager deviceLayoutManager;

    //CountDownlatch for the getDegices thread
    private CountDownLatch deviceLatch = new CountDownLatch(1), fragmentLatch = new CountDownLatch(1);

    //List of devices
    private ArrayList<Object> deviceList;

    //Texview
    private TextView roomName, roomCategory;

    //Imageview buttons logout and refresh
    private ImageView userLogoutDashboard, refreshDashboard;

    //Floating action button
    private FloatingActionButton floatButton;

    //Tag used to display logs
    private static final String TAG = "DashboardActivity";

    //ID of the current User and Room
    private String userID, roomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //List of devices
        deviceList = new ArrayList<>();

        //Reference to Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //retrieve object and user ID from main
        Room currentRoom = (Room) getIntent().getSerializableExtra("Room");
        userID = (String) getIntent().getSerializableExtra("userID");
        roomID = (String) getIntent().getSerializableExtra("roomID");

        //Refresh Button
        refreshDashboard = findViewById(R.id.Toolbar_Refresh);
        refreshDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevicesOfRoom();
            }
        });

        //Textviews
        roomName = findViewById(R.id.RoomName_Dashboard);
        roomCategory = findViewById(R.id.RoomCategory_Dashboard);

        roomName.setText(currentRoom.getName());
        roomCategory.setText("Cat.:" + currentRoom.getRoomCategory().toString());

        //Logout the user
        userLogoutDashboard = findViewById(R.id.Toolbar_User_Logout);
        userLogoutDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutMenu(v);
            }
        });

        //button to open the "create device fragment"
        floatButton = findViewById(R.id.Floating_Button_Dashboard);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateDeviceFragment();
            }
        });

        //get the ID of the current room that is selected
        //Thread getRoomIDThread = new Thread(() -> {
        //    getRoomID(userID, currentRoom);
        //});

        //Thread getDataThread = new Thread(() -> {
        //    try {
        //        //deviceLatch.await();
//
        //        getDevicesOfRoom();
        //        createDeviceRecyclerview();
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //});

        //getDevicesOfRoom();
        createDeviceRecyclerview();

        //start Threads
        //getRoomIDThread.start();
        //getDataThread.start();
        //try {
        //    getRoomIDThread.join();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }

    /**
     * automatically gets new data if a document got changed in the database
     * using snapshot that listens to any changes in the collection "devices"
     */
    @Override
    protected void onStart() {
        super.onStart();
        db.collection("users")
                .document(userID)
                .collection("rooms")
                .document(roomID)
                .collection("devices")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            return;
                        }
                        deviceList.clear();
                        queryDocumentSnapshots.forEach(device -> {
                            Device getType = device.toObject(Device.class);

                            if(getType.getDeviceType().equals(DeviceType.Washingmachine)){
                                Washingmachine washingDevice = device.toObject(Washingmachine.class);
                                deviceList.add(washingDevice);
                            }
                            if(getType.getDeviceType().equals(DeviceType.Toaster)){
                                Toaster toasterDevice = device.toObject(Toaster.class);
                                deviceList.add(toasterDevice);
                            }
                        });
                        deviceAdapter.updateDeviceList(deviceList);
                    }
                });
    }

    /**
     * gets the hole collection of "devices" that a room has
     */
    private void getDevicesOfRoom(){
        //reset roomList if the list conains more then zero elements
        if(deviceList.size() > 0){
            deviceList.clear();
        }
        //getting the "device" collection of the user and adding each tot the deviceList
        db.collection("users")
                .document(userID)
                .collection("rooms")
                .document(roomID)
                .collection("devices")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //iterating through the devices in a room
                        queryDocumentSnapshots
                                .forEach(device -> {
                                    Device getType = device.toObject(Device.class);

                                    if(getType.getDeviceType().equals(DeviceType.Washingmachine)){
                                        Washingmachine washingDevice = device.toObject(Washingmachine.class);
                                        deviceList.add(washingDevice);
                                    }
                                    if(getType.getDeviceType().equals(DeviceType.Toaster)){
                                        Toaster toasterDevice = device.toObject(Toaster.class);
                                        deviceList.add(toasterDevice);
                                    }
                                });
                        deviceAdapter.updateDeviceList(deviceList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "ERROR: Couldn't load devices.");
            }
        });
    }

    //creates the recyclerview with its components
    private void createDeviceRecyclerview(){
        deviceRecyclerview = findViewById(R.id.RecyclerViewDashboard);
        deviceRecyclerview.setHasFixedSize(true);
        deviceLayoutManager = new LinearLayoutManager(this);
        deviceAdapter = new DeviceAdapter(deviceList);

        //this functions allows to run on the main ui thread so changes can be made
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceRecyclerview.setLayoutManager(deviceLayoutManager);
                deviceRecyclerview.setAdapter(deviceAdapter);
            }
        });

        /**
         * Opening the "Manage device fragment"
         * data gets passed but before the current device ID of the selected device needs to
         * be retrieved from the database, if this is finished the fragment will open.
         * Data about the device will be stored in a bundle and passed to the fragment
         */
        deviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onDeviceClick(int position) {
                ManageDeviceFragment manageDeviceFragment = ManageDeviceFragment.newInstance();

                Object deviceClass = deviceList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("roomID", roomID);


                Thread collectData = new Thread(() -> {
                    //if the selected Device is a Washingmachine
                    if(deviceClass.getClass().equals(Washingmachine.class)){
                        Washingmachine selectedDevice = (Washingmachine) deviceList.get(position);

                        db.collection("users")
                                .document(userID)
                                .collection("rooms")
                                .document(roomID)
                                .collection("devices")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        queryDocumentSnapshots.getDocuments()
                                                .stream()
                                                .forEach(entry -> {
                                                    if(selectedDevice.equals(entry.toObject(Washingmachine.class))){
                                                        bundle.putString("deviceID", entry.getId());
                                                        bundle.putSerializable("deviceObject", (Serializable) deviceClass);
                                                        fragmentLatch.countDown();
                                                        //reset latch
                                                        fragmentLatch = new CountDownLatch(1);
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "COULDN'T FIND DEVICE!");
                            }
                        });
                    }

                    //if the selected Device is a Toaster
                    if(deviceClass.getClass().equals(Toaster.class)){
                        Toaster selectedDevice = (Toaster) deviceList.get(position);

                        db.collection("users")
                                .document(userID)
                                .collection("rooms")
                                .document(roomID)
                                .collection("devices")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        queryDocumentSnapshots.getDocuments()
                                                .stream()
                                                .forEach(entry -> {
                                                    if(selectedDevice.equals(entry.toObject(Toaster.class))){
                                                        bundle.putString("deviceID", entry.getId());
                                                        bundle.putSerializable("deviceObject", (Serializable) deviceClass);

                                                        fragmentLatch.countDown();
                                                        //reset latch
                                                        fragmentLatch = new CountDownLatch(1);
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "COULDN'T FIND DEVICE!");
                            }
                        });
                    }
                });

                //opening the fragment
                Thread startFragment = new Thread(() -> {
                    try {
                        fragmentLatch.await();
                        manageDeviceFragment.setArguments(bundle);

                        FragmentManager manageDeviceFragmentManager = getSupportFragmentManager();
                        FragmentTransaction manageDeviceTransaction = manageDeviceFragmentManager.beginTransaction();

                        manageDeviceTransaction.addToBackStack(null);
                        manageDeviceTransaction.add(R.id.ManageDeviceContainer, manageDeviceFragment,"CREATE MANAGE DEVICE FRAGMENT").commit();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });

                //starting the both Threads
                collectData.start();
                startFragment.start();
            }
        });
    }

    /**
     *Opens the "Create device" fragment
     * Id of the user and room will be transferred so the device will be stored in the right place
     */
    private void openCreateDeviceFragment(){
        CreateDeviceFragment createDeviceFragment = CreateDeviceFragment.newInstance();

        //transfer Data to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putString("roomID", roomID);
        createDeviceFragment.setArguments(bundle);

        FragmentManager createDeviceFragmentManager = getSupportFragmentManager();
        FragmentTransaction deviceTransaction = createDeviceFragmentManager.beginTransaction();
        deviceTransaction.addToBackStack(null);
        deviceTransaction.add(R.id.CreateDeviceContainer, createDeviceFragment, "CREATE DEVICE FRAGMENT").commit();
    }

    /**
     * TODO delete not getting used anymore
     * @param id
     * @param room
     */
    private void getRoomID(String id, Room room){
        Thread getIDThread = new Thread(() -> {
            db.collection("users")
                    .document(id)
                    .collection("rooms")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            queryDocumentSnapshots.getDocuments()
                                    .stream()
                                    .forEach(entry -> {
                                        if(room.equals(entry.toObject(Room.class))){
                                            roomID = entry.getId();
                                            Log.w(TAG, "202 FOUND ROOM ID: " + roomID);
                                        }
                                    });
                            deviceLatch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        });

        //starting thread
        getIDThread.start();
        try {
            getIDThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Opens a popup if the user clicks the user-logo so he can log out
     * @param view
     */
    private void logoutMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.logout_main);
        popupMenu.show();
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
     * Logs out the current user and directs him to the Login & Registeractivity
     */
    public void userLogout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }
}
