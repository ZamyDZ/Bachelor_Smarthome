package com.example.smarthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.model.Device;
import com.example.smarthome.model.DeviceType;
import com.example.smarthome.model.Room;
import com.example.smarthome.model.Toaster;
import com.example.smarthome.model.Washingmachine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageDeviceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageDeviceFragment extends Fragment {
    //Firebase Authentication & FireStore & Currentuser
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    //Cloud functions
    private FirebaseFunctions mFunctions;

    //ID of the current user, room and device
    private String userID, roomID, deviceID;

    //curren device Object
    private Device selectedDevice;

    //Linear layouts of washingmachine and Toaster
    private LinearLayout washingLayout , toasterLayout;

    //ImageButtons
    private ImageButton washingButton, toasterButton;

    //Textviews
    private TextView deviceType, deviceName, washingSatuts, toasterStatus, washingTemp;

    private OnFragmentInteractionListener mListener;

    public ManageDeviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ManageDeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageDeviceFragment newInstance() {
        ManageDeviceFragment fragment = new ManageDeviceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * getting all the data that is passed from the bashboardactivity
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Reference to Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Cloud Functions
        mFunctions = FirebaseFunctions.getInstance();

        //get transferred data
        userID = (String) getArguments().getSerializable("userID");
        roomID = (String) getArguments().getSerializable("roomID");
        deviceID = (String) getArguments().getSerializable("deviceID");
        selectedDevice = (Device) getArguments().getSerializable("deviceObject");
    }

    /**
     * Creates the fragment UI for the first time with all its components to create a new room
     * @param inflater Component which creates the view based on layout XML file
     * @param container The view that this fragment is inserted
     * @param savedInstanceState Parameters
     * @return The View of the fragment UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_device, container, false);

        //Layouts
        washingLayout = view.findViewById(R.id.ManageDeviceWashingLayout);
        toasterLayout = view.findViewById(R.id.ManageDeviceToasterLayout);

        //ImageViewButtons
        washingButton = view.findViewById(R.id.ManageDeviceWashingButton);
        toasterButton = view.findViewById(R.id.ManageDeviceToasterButton);

        washingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Data that gets transferred
                Map<String, Object> data = new HashMap<>();
                data.put("userID", userID);
                data.put("roomID", roomID);
                data.put("deviceID", deviceID);

                //starting Cloud Function for the Washingmachine
                mFunctions.getHttpsCallable("updateWashingMachine").call(data);
            }
        });
        toasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Data that gets transferred
                Map<String, Object> data = new HashMap<>();
                data.put("userID", userID);
                data.put("roomID", roomID);
                data.put("deviceID", deviceID);

                //starting Cloud Function for the Toaster
                mFunctions.getHttpsCallable("startToaster").call(data);
            }
        });

        //Textviews
        deviceType = view.findViewById(R.id.ManageDeviceType);
        deviceName = view.findViewById(R.id.ManageDeviceName);

        washingSatuts = view.findViewById(R.id.ManageDeviceWashingStatus);
        washingTemp = view.findViewById(R.id.ManageDeviceWashingTemp);

        toasterStatus = view.findViewById(R.id.ManageDeviceToasterStatus);

        //set information about current device
        if(selectedDevice.getDeviceType().equals(DeviceType.Washingmachine)){
            deviceType.setText(DeviceType.Washingmachine.toString());
            Washingmachine washDevice = (Washingmachine) selectedDevice;
            deviceName.setText(washDevice.getName());
            washingLayout.setVisibility(View.VISIBLE);

            //Power status
            if(!washDevice.isPowerStatus()){
                washingButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.red));
                washingSatuts.setText("INACTIVE");
            }else {
                washingButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.green));
                washingSatuts.setText("ACTIVE");
            }

            washingTemp.setText(String.valueOf(washDevice.getTemperature()));

        }
        if(selectedDevice.getDeviceType().equals(DeviceType.Toaster)){
            deviceType.setText(DeviceType.Toaster.toString());
            Toaster toasterDevice = (Toaster) selectedDevice;
            deviceName.setText(toasterDevice.getDeviceName());
            toasterLayout.setVisibility(View.VISIBLE);

            //Power status
            if(!toasterDevice.isPowerStatus()){
                toasterButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.red));
                toasterStatus.setText("INACTIVE");
            }else {
                toasterButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.green));
                toasterStatus.setText("ACTIVE");
            }
        }
        return view;
    }

    /**
     * Automatically listen to any change of the current device that is selected and change them
     * this is performed with the Snapshot-listener that listen to the actual document in the
     * databse
     */
    @Override
    public void onStart() {
        super.onStart();

        db.collection("users")
                .document(userID)
                .collection("rooms")
                .document(roomID)
                .collection("devices")
                .document(deviceID)
                .addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            return;
                        }
                        //if the device is a Washing-machine
                        if(selectedDevice.getDeviceType().equals(DeviceType.Washingmachine)){
                            selectedDevice = documentSnapshot.toObject(Washingmachine.class);

                            Washingmachine washDevice = documentSnapshot.toObject(Washingmachine.class);
                            //Power status
                            if(!washDevice.isPowerStatus()){
                                washingButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.red));
                                washingSatuts.setText("INACTIVE");
                            }else {
                                washingButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.green));
                                washingSatuts.setText("ACTIVE");
                            }

                            //actual temperature of the device
                            washingTemp.setText(String.valueOf(washDevice.getTemperature()));
                        }

                        //if the Device is a Toaster
                        if(selectedDevice.getDeviceType().equals(DeviceType.Toaster)){
                            selectedDevice = documentSnapshot.toObject(Toaster.class);

                            Toaster toasterDevice = documentSnapshot.toObject(Toaster.class);
                            //Power status
                            if(!toasterDevice.isPowerStatus()){
                                toasterButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.red));
                                toasterStatus.setText("INACTIVE");
                            }else {
                                toasterButton.setColorFilter(ContextCompat.getColor(washingButton.getContext(), R.color.green));
                                toasterStatus.setText("ACTIVE");
                            }
                        }
                    }
                });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
