package com.comp90018.a2.habits;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class CreateHabit extends AppCompatActivity {

    HabitEntriesService entriesService;
    private EditText habitTitle;
    private TextView startDate;
    private TextView endDate;
    //private Camera camera;
    //private Gallery gallery;
    //private Button cameraBtn;
    //private Button galleryBtn;
    private ImageView doneButton;
    private ImageView backButton;
    private Calendar calendar;
    private CheckBox noEndDateCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        // Initialize your UI elements
        startDate = findViewById(R.id.sDate);
        endDate = findViewById(R.id.eDate);
        calendar = Calendar.getInstance();
        noEndDateCheckBox = findViewById(R.id.endDateCheckBox);

        habitTitle = findViewById(R.id.habitT);
        doneButton = findViewById(R.id.done);
        backButton = findViewById(R.id.back);

        entriesService = HabitEntriesService.getInstance();


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateHabit.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                                startDate.setText(selectedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateHabit.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                                endDate.setText(selectedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        noEndDateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                endDate.setText("9999/12/31");
                endDate.setEnabled(false);
                endDate.setVisibility(View.INVISIBLE);

            } else {
                endDate.setText("");
                endDate.setVisibility(View.VISIBLE);
                endDate.setEnabled(true);
            }
        });

        //camera test
//        cameraBtn = findViewById(R.id.cameraBtn);
//        camera = new Camera(this);
//        cameraBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if (camera.hasCameraPermission()) {
//                    Log.d("abc", "click");
//                    camera.dispatchTakePictureIntent();
//                } else {
//                    camera.requestCameraPermission();
//                }
//            }
//        });

//        galleryBtn = findViewById(R.id.galleryBtn);
//        gallery = new Gallery(this);
//        galleryBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                gallery.dispatchPickPictureIntent();
//            }
//        });

        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) { saveHabit();}
        });

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Set any initial values or listeners for your UI elements
    }


    public void saveHabit() {
        //doneButton.setEnabled(false);
        String sDate = startDate.getText().toString();
        String eDate = endDate.getText().toString();
        String title = habitTitle.getText().toString();

        // Check if the fields are not empty before sending them back
        if(title.isEmpty() || sDate.isEmpty() || eDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            Date startDate = dateFormat.parse(sDate);
            Date endDate = dateFormat.parse(eDate);

            // Ensure dates are in the correct order and format
            if (startDate == null || endDate == null) {
                throw new ParseException("Invalid date", 0);
            }

            if (endDate.before(startDate)) {
                Toast.makeText(this, "End date should be after start date.", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format. Please use 'dd/MM/yyyy'.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new habit entry
        HabitEntry newEntry = new HabitEntry(sDate, eDate, title);

        try {
            // Add entry to the service and notify the adapter that the data set has changed
            //entriesService.addEntry(newEntry);

            entriesService.addEntry(newEntry);
            Toast.makeText(this, "Habit saved successfully.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle exceptions from adding entry
            Log.e("CreateHabit", "Failed to save the habit", e);
            Toast.makeText(this, "Failed to save the habit. Please try again.", Toast.LENGTH_SHORT).show();
        }

        // Close the activity, returning to the previous screen
        finish();
    }



}
