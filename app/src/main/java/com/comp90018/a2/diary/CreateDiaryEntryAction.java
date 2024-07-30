package com.comp90018.a2.diary;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.R;
import com.comp90018.a2.camera.Camera;
import com.comp90018.a2.location.GeocodeService;
import com.comp90018.a2.location.LatLong;
import com.comp90018.a2.location.LocationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateDiaryEntryAction extends AppCompatActivity {

    // Request  for capturing the picture
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    DiaryEntriesService entriesService;
    private EditText titleEditText;
    private EditText entryTextEditText;
    private SeekBar seekBar;
    private double latitude;
    private double longitude;
    private TextView locationDisplay;
    private LocationService locationService;
    private boolean hasLocation = false;
    private FirebaseAuth mAuth;
    private DiaryCRUDService diaryService = new DiaryCRUDService();
    private ViewSwitcher viewSwitcher;
    private String formattedDate;
    private ImageView backButton;
    private ImageButton cameraButton;
    private ImageButton photoButton;
    private ImageView tickButton;
    private Bitmap capturedImage;
    private Uri selectedImageUri;
    private Camera camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary_entry_action);

        // Initialize your UI elements
        locationDisplay = findViewById(R.id.locationDisplay);
        titleEditText = findViewById(R.id.titleEditText);
        entryTextEditText = findViewById(R.id.entryTextEditText);
        viewSwitcher = findViewById(R.id.switcher);
        seekBar = findViewById(R.id.seekBar);

        // initialise action bar buttons
        backButton = findViewById(R.id.back);
        cameraButton = findViewById(R.id.camera);
        photoButton = findViewById(R.id.photo);
        tickButton = findViewById(R.id.tick);

        // set the formatted date to current date by default
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formattedDate = currentDate.format(dateFormatter);

        // initialise camera service
        camera = new Camera(this);

        // Access location service
        locationService = new LocationService(this);

        // Request location permissions
        locationService.requestLocationPermissions(this);

        // Request camera permissions
        camera.requestCameraPermission();

        // Set the location callback
        locationService.setLocationCallback(location -> {
            Log.d(TAG, "Location received: " + location.getLatitude() + ", " + location.getLongitude());
            // Handle the updated location here
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            // Update UI or perform other actions with the new location data
            locationDisplay.setText(String.format("Location: %s, %s", latitude, longitude));
            // set hasLocation to true
            hasLocation = true;
        });

        locationService.startLocationUpdates();
        // Remember to stop location updates when they are no longer needed
        // locationService.stopLocationUpdates();

        // access the diary entry service
        entriesService = DiaryEntriesService.getInstance();

        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiaryEntry();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera.hasCameraPermission()) {
                    // Create an Intent for  image capturing
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Start the camera activity for result
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    // Handle the case where the camera app is not available
                    Toast.makeText(CreateDiaryEntryAction.this, "Camera app is not available.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Intent for pick an image from the gallery
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
            }
        });
    }

    // Add a method to save the diary entry when a save button is clicked
    public void saveDiaryEntry() {
        // disable done button
        tickButton.setEnabled(false);

        // show loading indicator
        viewSwitcher.showNext();

        // Get the values entered by the user
        String title = titleEditText.getText().toString();
        String entryText = entryTextEditText.getText().toString();
        int feeling = seekBar.getProgress();

        // Create a new DiaryEntry object
        DiaryEntry newEntry = new DiaryEntry(formattedDate, title, entryText, feeling);

        // convert captured image to byte array
        if (capturedImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            newEntry.setImageBytes(imageBytes);
        }

        // finish activity if no location
        if (!hasLocation) {
            entriesService.addEntry(newEntry);
            entriesService.getDiaryListAdapter().notifyDataSetChanged();
            finish();
            return;
        }

        // handle logic when location is available

        // store latlong
        LatLong latlong = new LatLong(latitude, longitude);
        newEntry.setLatlong(latlong);

        // Geocoder for getting address from latlong
        GeocodeService geoCoder = new GeocodeService();

        Disposable subscribe = geoCoder.getAddress(latlong)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> {
                    Log.d(TAG, "Address received: " + address);
                    // set the address if available
                    newEntry.setAddress(address);

                    // Save diary entry and update list
                    entriesService.addEntry(newEntry);
                    entriesService.getDiaryListAdapter().notifyDataSetChanged();

                    // end activity
                    finish();
                }, throwable -> {
                    // Handle errors here
                    System.err.println("Error: " + throwable.getMessage());
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationService.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions were granted; you can start location updates now
                locationService.startLocationUpdates();
            } else {
                // Permissions were denied; handle this case (e.g., show a message to the user)
                Toast.makeText(this, "location permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showDatePickerDialog(View view) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // Handle the selected date here
                // You can update the text in the dateEditText
                formattedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                EditText dateEditText = findViewById(R.id.dateEditText);
                dateEditText.setText(formattedDate);
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                capturedImage = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(R.id.imageView);

                // Set padding programmatically so borders are not cut off
                int paddingInDp = 3; // Replace with your desired padding value in dp
                float scale = getResources().getDisplayMetrics().density;
                int paddingInPx = (int) (paddingInDp * scale + 0.5f); // Convert dp to pixels
                imageView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

                // apply the image
                imageView.setImageBitmap(capturedImage);
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                //  URI of the selected image
                selectedImageUri = data.getData();
                //  selected image to the ImageView
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageURI(selectedImageUri);
            }
        }
    }
}