package com.comp90018.a2.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.MainActivity;
import com.comp90018.a2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, passwordConfirmEditText;
    private Button signUpButton;
    private ImageView backButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Access a Firestore database instance from your Activity
        db = FirebaseFirestore.getInstance();

        // Initialize EditText and Button
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);
        signUpButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.back);
        // Set click listener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call signup logic/method
                performSignup();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void performSignup() {
        // Get the values from EditText fields
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        // Check if fields are empty or if passwords match

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "invalid email address", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            // staring signup process
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SignupActivity", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                writeUserToDatabase(uid, name, email, new WriteUserCallback() {
                                    @Override
                                    public void onSuccess(boolean success) {
                                        // deal with write to database result
                                        if (!success) {
                                            unsucessfulSignup(task);
                                        }
                                    }
                                });
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                user.updateProfile(profileUpdates);
                                //createDiaryWithUid(uid);
                                updateUI(user);
                                Log.d("SignupActivity", "createUserWithEmail:success");
                                Toast.makeText(SignupActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                unsucessfulSignup(task);
                                updateUI(null);
                            }
                        }
                    });


        }
    }

    private void unsucessfulSignup(@NonNull Task<AuthResult> task) {
        Log.w("SignupActivity", "createUserWithEmail:failure", task.getException());
        Toast.makeText(SignupActivity.this, "Failed to sign up",
                Toast.LENGTH_SHORT).show();
    }

    private void createDiaryWithUid(String uid) {
        Map<String, Object> emptyData = new HashMap<>();
        db.collection("diary").document(uid).collection("diaries").add(emptyData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SignupActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SignupActivity", "createDiaryWithUid:Error adding document", e);
                    }
                });
    }

    private void writeUserToDatabase(String uid, String name, String email, WriteUserCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        db.collection("users")
                .document(uid)
                .set(userData, SetOptions.merge()) // Merge with existing document data
                .addOnSuccessListener(aVoid -> {
                    // Callback for successful document addition
                    Log.d("SignupActivity", "DocumentSnapshot added with ID: " + uid);
                    callback.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    // Callback for failed document addition
                    Log.w("SignupActivity", "Error adding document", e);
                    callback.onSuccess(false);
                });
    }

    // Method to handle additional actions after successful login
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is logged in, perform necessary actions
            reload();

        } else {
            Toast.makeText(SignupActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle actions after successful login
    private void reload() {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("openSettingsFragment", true);
        startActivity(intent);
    }

    interface WriteUserCallback {
        void onSuccess(boolean success);
    }
}