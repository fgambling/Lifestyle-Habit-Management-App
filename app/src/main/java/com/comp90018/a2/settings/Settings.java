package com.comp90018.a2.settings;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.comp90018.a2.MainActivity.jobScheduler;

import android.app.AlertDialog;
import android.app.job.JobScheduler;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


import com.comp90018.a2.R;
import com.comp90018.a2.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.core.app.NotificationManagerCompat;

public class Settings extends Fragment {
    private FirebaseAuth mAuth;
    private RelativeLayout notification;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = mAuth.getCurrentUser();
        View view =inflater.inflate(R.layout.user_preference_layout, container, false);
        View settingsButton = view.findViewById(R.id.edit_profile);
        TextView textView = view.findViewById(R.id.username);
        RelativeLayout security_button = view.findViewById(R.id.security_section);
        RelativeLayout aboutus = view.findViewById(R.id.about);
        RelativeLayout declaration = view.findViewById(R.id.declaration);
        RelativeLayout logout = view.findViewById(R.id.logout);
        textView.setText(user.getDisplayName());
        notification = view.findViewById(R.id.notificationSwitch);
        boolean areNotificationsEnabled = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled();



        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!areNotificationsEnabled) {
                    showPermissionDialog();
                } else{
                    Intent intent = new Intent();
                    intent.setAction( android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        security_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), SecurityActivity.class);
                startActivity(intent);
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AboutUsActivity.class);
                startActivity(intent);
            }
        });

        declaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), DeclarationPageActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mAuth.signOut();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    cancelJob();
                    startActivity(intent);
                    Toast.makeText(requireContext(), "Logged out.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireContext(), "Already signed out, do not repeat operation.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Push Notification Permission");
        builder.setMessage("We would like to send you notifications. Would you like to enable push notifications?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction( android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ;
            }
        });
        builder.show();
    }

    public void cancelJob() {
        if (jobScheduler.getPendingJob(1) != null){
            jobScheduler.cancel(1);
            Log.d(TAG, "Service terminated!!!");
        }
    }

}
