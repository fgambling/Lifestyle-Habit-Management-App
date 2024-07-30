package com.comp90018.a2.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.Constants;
import com.comp90018.a2.R;
import com.comp90018.a2.calendar.MoodCalendar;

import java.util.UUID;

public class DiaryEntryActivity extends AppCompatActivity {

    private ImageButton deleteButton;
    private ImageButton editButton;

    private final DiaryEntriesService diaryEntriesService = DiaryEntriesService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);

        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewEntryText = findViewById(R.id.textViewEntryText);
        TextView textViewFeeling = findViewById(R.id.textViewFeeling);
        TextView textViewAddress = findViewById(R.id.textViewAddress);
        ImageView imageView = findViewById(R.id.imageView);
        ImageView moodIndicator = findViewById((R.id.moodIndicator));

        deleteButton = findViewById(R.id.buttonDelete);
        editButton = findViewById(R.id.buttonEdit);

        // Get the selected diary entry by id
        UUID id = (UUID) getIntent().getSerializableExtra("id");
        DiaryEntry entry = diaryEntriesService.getEntryById(id);

        if (entry == null) {
            // If the entry is null, finish the activity
            finish();
            return;
        }

        String title = entry.getTitle();
        String date = entry.getDate();
        String entryText = entry.getEntryText();
        int mood = entry.getMood();
        String moodText = Constants.moodToString(mood);
        String address = entry.getAddress();
        byte[] imageBytes = entry.getImageBytes();

        // Display entry details
        textViewDate.setText(date);
        textViewTitle.setText(title);
        textViewEntryText.setText(entryText);
        textViewFeeling.setText(moodText);

        // Set mood indicator circle to correct colour
        switch (mood) {
            case 0:
                moodIndicator.setImageResource(R.drawable.mood_circle_vnegative);
                break;
            case 1:
                moodIndicator.setImageResource(R.drawable.mood_circle_negative);
                break;
            case 2:
                moodIndicator.setImageResource(R.drawable.mood_circle_neutral);
                break;
            case 3:
                moodIndicator.setImageResource(R.drawable.mood_circle_positive);
                break;
            case 4:
                moodIndicator.setImageResource(R.drawable.mood_circle_vpositive);
                break;
        }

        // display optional fields
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
        if (address != null) {
            // get the first 4 words of the address
            String[] parts = address.split(",");
            if (parts.length > 4) {
                address = String.format("%s %s %s %s", parts[0], parts[1], parts[2], parts[3]);
            }
            textViewAddress.setText(String.format("%s", address));
        } else {
            textViewAddress.setHeight(0);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryEntryActivity.this, UpdateDiaryEntryAction.class);
                intent.putExtra("id", String.valueOf(entry.getId())); // pass id into intent
                startActivity(intent);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiaryCRUDService diaryService = new DiaryCRUDService();
                String docIDtoDelete = entry.getDocumentId();
                diaryService.deleteDiary(docIDtoDelete).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Diary entry delete successfully
                        Log.d("DiaryEntryActivity", "delete diary success");

                    } else {
                        // Failed to delete diary entry
                        Log.d("DiaryEntryActivity", "delete diary failed");
                    }
                });
                diaryEntriesService.deleteEntry(entry.getId());
                MoodCalendar.getInstance().reload();
                finish();
            }
        });

    }

    public void onBackToListButtonClick(View view) {
        finish();
    }
}
