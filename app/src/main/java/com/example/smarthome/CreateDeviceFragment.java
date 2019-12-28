package com.example.smarthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smarthome.model.Toaster;
import com.example.smarthome.model.Washingmachine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateDeviceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateDeviceFragment extends Fragment {
    //Firebase Authentication
    private FirebaseAuth mAuth;
    //Firebase FireStore
    private FirebaseFirestore db;
    //Spinner
    private Spinner deviceSpinner;
    //Edittext device name
    private EditText deviceNameEdittext;
    //create Device button
    private Button createDeviceButton;
    //ID of the user and the room
    private String userID, roomID;

    //Tag for logs
    private static final String TAG = "DashboardFRAGMENT";

    //Device type list
    private List<String> deviceTyp = Arrays.asList("Washingmachine", "Toaster");

    private OnFragmentInteractionListener mListener;

    public CreateDeviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CreateDeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateDeviceFragment newInstance() {
        CreateDeviceFragment fragment = new CreateDeviceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //get user and room ID from dashboard
        userID = getArguments().getString("userID");
        roomID = getArguments().getString("roomID");
        //Log.w(TAG, "303 USER ID: " + userID + " ||| ROOM ID: "+ roomID);

        //View
        View view = inflater.inflate(R.layout.fragment_create_device, container, false);

        //Spinner
        deviceSpinner = view.findViewById(R.id.DeviceType_Fragment);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, deviceTyp);
        deviceSpinner.setAdapter(adapter);

        //Editext device name
        deviceNameEdittext = view.findViewById(R.id.CreateDeviceName);

        //Button
        createDeviceButton = view.findViewById(R.id.CreateDeviceButtonFragment);
        createDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDevice();
            }
        });

        return view;
    }

    /**
     *
     */
    private void createDevice(){
        //get the input-text of the user
        String deviceName = deviceNameEdittext.getText().toString();
        //create a device
        Object device = null;
        if(deviceSpinner.getSelectedItem().toString().equals("Washingmachine")){
            device = new Washingmachine(deviceName);
        }
        if(deviceSpinner.getSelectedItem().toString().equals("Toaster")){
            device = new Toaster(deviceName);
        }
        Log.w(TAG, "101: name = " + device.toString());
        //
        db.collection("users")
                .document(userID)
                .collection("rooms")
                .document(roomID)
                .collection("devices")
                .add(device);
        Toast.makeText(getActivity(), device.getClass().getName()+" Device created!", Toast.LENGTH_SHORT ).show();

        //closing fragment after its created and added it to the collection
        getActivity().onBackPressed();
    }

    // TODO: Rename method, update argument and hook method into UI event
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
