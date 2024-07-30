package com.comp90018.a2.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.R;
import com.comp90018.a2.settings.SecurityActivity;

public class Pinactivity extends AppCompatActivity {

    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private Button buttonSave;

    private View back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepin_settings);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        buttonSave = findViewById(R.id.buttonSave);
        back = findViewById(R.id.backSettings);

        buttonSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = oldPassword.getText().toString();
                String newOne = newPassword.getText().toString();
                String confirm = confirmPassword.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("pin", password);
                intent.putExtra("newOne", newOne);
                intent.putExtra("confirm", confirm);

                setResult(RESULT_OK, intent);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String pin = data.getStringExtra("pin");
            String newOne = data.getStringExtra("newOne");
            String confirm = data.getStringExtra("confirm");

        }
    }
}