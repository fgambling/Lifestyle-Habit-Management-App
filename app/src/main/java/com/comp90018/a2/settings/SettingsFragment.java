package com.comp90018.a2.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp90018.a2.R;

import android.content.Intent;

import android.util.Log;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.comp90018.a2.login.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private TextView textView1;
    private Button loginButton;
    private Button logoutButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_settings, container, false);
        logoutButton = view.findViewById(R.id.logoutButton);
        loginButton = view.findViewById(R.id.loginButton);
        textView1 = view.findViewById(R.id.textView1);

        // Setting up the click event listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // Perform user logout operations
                    signOut();
                    reload();
                    Log.d("MainActivity", "signInWithEmail:success");
                    Toast.makeText(requireContext(), "Logged out.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireContext(), "Already signed out, do not repeat operation.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // trigger LoginActivity when clicking
                goToLoginActivity();
            }
        });

        // Check the current user here and update the TextView's text
        updateTextView();

        return view;
    }

    private void reload() {
        Intent intent = new Intent(requireContext(), requireContext().getClass());
        intent.putExtra("openSettingsFragment", true);
        startActivity(intent);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        mAuth.signOut();
    }

    private void updateTextView() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayEmail = currentUser.getUid();
            if (displayEmail != null) {
                // if the user is logged in, display email
                textView1.setText("Hello, " + displayEmail);
            }
        } else {
            // If the user is not logged in, set default text
            textView1.setText("Hello World!");
        }
    }

}