package com.example.smarthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smarthome.model.Category;
import com.example.smarthome.model.Room;
import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.stream.Stream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRoomFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRoomFragment extends Fragment {
    //Firebase Authentication
    private FirebaseAuth mAuth;
    //Firebase FireStore
    private FirebaseFirestore db;
    //Spinner of the Enum: Category
    private Spinner enumSpinner;
    //Editext room name
    private EditText createRoomName;
    //Create room button
    private Button createRoomButton;
    //Fragment Listener
    private OnFragmentInteractionListener mListener;

    public CreateRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CreateRoomFragment.
     */
    public static CreateRoomFragment newInstance() {
        CreateRoomFragment fragment = new CreateRoomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializing Firebase Authentication & FireStore
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates the fragment UI for the first time with all its components to create a new room
     * @param inflater Component which creates the view based on layout XML file
     * @param container The view that this fragment is inserted
     * @param savedInstanceState Parameters
     * @return The View of the fragment UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View
        View view = inflater.inflate(R.layout.fragment_create_room, container, false);
        //Spinner
        enumSpinner = view.findViewById(R.id.RoomCategory_Fragment);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.categoryList, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enumSpinner.setAdapter(adapter);
        //enumSpinner.setOnItemSelectedListener(getActivity());

        //Room name
        createRoomName = view.findViewById(R.id.CreateRoomName);

        //Create room button
        createRoomButton = view.findViewById(R.id.CreateRoomButtonFragment);
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoom();
            }
        });
        return view;
    }

    /**
     * This Function creates a new Room object and adding it to the currentuser colltion
     * of "rooms"
     */
    private void createRoom(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String spinnerStr = enumSpinner.getSelectedItem().toString();
        Stream.of(Category.values())
                .forEach(cat -> {
                    if(cat.toString().equals(spinnerStr)){
                        //Creation of the room object
                        Room newRoom = new Room(currentUser.getEmail(), createRoomName.getText().toString(), cat);

                        //Query to get the current user and adding the room tho the "rooms" collection
                        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                queryDocumentSnapshots.getDocuments()
                                        .stream()
                                        .forEach(user -> {
                                            if (user.toObject(User.class).getEmail().equals(currentUser.getEmail())){
                                                db.collection("users")
                                                        .document(user.getId())
                                                        .collection("rooms")
                                                        .add(newRoom);
                                                Toast.makeText(getActivity(), newRoom.getName()+" Room created!", Toast.LENGTH_SHORT ).show();

                                                //closing fragment after its created and added to the collection
                                                getActivity().onBackPressed();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Room creation failed!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
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
