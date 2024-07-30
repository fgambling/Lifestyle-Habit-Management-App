package com.comp90018.a2.settings;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.R;
import com.comp90018.a2.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChange extends AppCompatActivity {

    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private Button buttonSave;

    private View back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword_settings);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        buttonSave = findViewById(R.id.buttonSave);
        back = findViewById(R.id.backSettings);

        buttonSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void changePassword(){
        String password = oldPassword.getText().toString();
        String newOne = newPassword.getText().toString();
        String confirm = confirmPassword.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        if(!newOne.equals(confirm)){
            Toast.makeText(PasswordChange.this, "the password provided is not same in the confirm",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(newOne.isEmpty()){
            Toast.makeText(PasswordChange.this, "Cannot have empty password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(newOne.length() < 6){
            Toast.makeText(PasswordChange.this, "Password must have at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newOne).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PasswordChange.this, "User password updated.",
                                        Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PasswordChange.this, "Too many request, please try again later.",
                                    Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(PasswordChange.this, "Old password is incorrect.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}