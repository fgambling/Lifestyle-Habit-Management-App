package com.comp90018.a2.habits;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.R;
import com.comp90018.a2.camera.Camera;
import com.comp90018.a2.camera.Gallery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class CreateGoal extends AppCompatActivity {

    private EditText goalTitle;
    private TextView startDate;
    private TextView endDate;
    private Camera camera;
    private Gallery gallery;
    private Button cameraBtn;
    private Button galleryBtn;
    private Button doneButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        // Initialize your UI elements
        startDate = findViewById(R.id.sDate);
        endDate = findViewById(R.id.eDate);
        goalTitle = findViewById(R.id.goalT);
        doneButton = findViewById(R.id.done);
        backButton = findViewById(R.id.back);

        startDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 || s.length() == 5) {
                    if (!s.toString().substring(s.length() - 1).equals("/")) {
                        startDate.append("/");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        endDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 || s.length() == 5) {
                    if (!s.toString().substring(s.length() - 1).equals("/")) {
                        endDate.append("/");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //camera test
        cameraBtn = findViewById(R.id.cameraBtn);
        camera = new Camera(this);
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (camera.hasCameraPermission()) {
                    Log.d("abc", "click");
                    camera.dispatchTakePictureIntent();
                } else {
                    camera.requestCameraPermission();
                }
            }
        });

        galleryBtn = findViewById(R.id.galleryBtn);
        gallery = new Gallery(this);
        galleryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                gallery.dispatchPickPictureIntent();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == camera.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera.dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.myImageView);

        if (requestCode == Camera.getRequestImageCapture() && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            // Upload the image to Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "image_" + timestamp + ".jpg";
            StorageReference imageRef = storageRef.child("images/" + userId + "/" + imageFileName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully,  get the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save the image URL to Firestore
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    String imageUrl = uri.toString();
                    // Get the current user's authentication information
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String currentUserUid = auth.getCurrentUser().getUid();

                    // Create a new document in the "dairy" collection and set the image URL
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("imageURL", imageUrl);

                    firestore.collection("dairy")
                            .document(currentUserUid)
                            .set(dataMap) // Use set() to create a new document
                            .addOnSuccessListener(aVoid -> {
                                // Image URL saved to Firestore
                                Toast.makeText(CreateGoal.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle error
                                Log.e("Firestore Error", e.getMessage());
                            });
                });
            }).addOnFailureListener(e -> {
                // Handle error  the image uploading
                Log.e("Storage Error", e.getMessage());
            });
        }

        if (requestCode == Gallery.getRequestImagePick() && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }
}
