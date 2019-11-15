package com.example.smarthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //Firebase Authentication
    private FirebaseAuth mAuth;
    //Firebase FireStore
    private FirebaseFirestore db;


    //Register Button Fragment
    private Button RegisterButton_Fragment;
    //Edittext
    private EditText UsernameInput_Fragment, EmailInput_Fragment, PasswordInput_Fragment;

    //InteractionLister of the Fragment
    private OnFragmentInteractionListener mListener;

    //Required empty public constructor
    public RegisterFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment RegisterFragment.
     */
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * creates Firebase refernce so a user can be created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Firebase Authentication & FireStore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * @param inflater Creating a new view so the fragment can be displayed
     * @param container ViewGroup of the fragmentcontainer
     * @param savedInstanceState
     * @return a view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        //Register Button
        RegisterButton_Fragment = view.findViewById(R.id.RegisterButton_Fragment);
        RegisterButton_Fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
        //Username Input
        UsernameInput_Fragment = view.findViewById(R.id.UsernameInput_Fragment);
        //Email Input
        EmailInput_Fragment = view.findViewById(R.id.Emailinput_Fragment);
        //Password Input
        PasswordInput_Fragment = view.findViewById(R.id.Passwordinput_Fragment);

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * TODO no usage declared yet
     * @param uri
     */
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Attaching the registerfragment
     * @param context
     */
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

    /**
     * Detaching the registerfragment
     */
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

    /**
     * Creates a new User in FirebaseAuthentication and in the Firebase "users" collection
     * //TODO Change later this description if User gets changed
     * Creates a new User from the Email-, User- & Passwordinput
     * FirebaseAuthentication creates a new User
     * if this was succesfull the user gets added to the "users" collection
     */
    private void createUser() {
        final User user = new User(EmailInput_Fragment.getText().toString(), UsernameInput_Fragment.getText().toString(), PasswordInput_Fragment.getText().toString());

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "New User created", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "FAILURE: Data not complete or wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Register failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
