package com.comp90018.a2.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.comp90018.a2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainSettingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_preference_layout);
        View settingsButton = findViewById(R.id.edit_profile);
        RelativeLayout security_button = findViewById(R.id.security_section);
        RelativeLayout aboutus = findViewById(R.id.about);
        RelativeLayout declaration = findViewById(R.id.declaration);
        RelativeLayout logout = findViewById(R.id.logout);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSettingActivity.this, ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        security_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSettingActivity.this, SecurityActivity.class);
                startActivity(intent);
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSettingActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        declaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSettingActivity.this, DeclarationPageActivity.class);
                startActivity(intent);
            }
        });





    }
}
