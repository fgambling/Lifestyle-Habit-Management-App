package com.comp90018.a2.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.comp90018.a2.R;

import java.util.List;

public class CustomCardAdapter extends ArrayAdapter<DiaryEntry> {

    public CustomCardAdapter(Context context, List<DiaryEntry> entries) {
        super(context, 0, entries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the diary entry at the current position
        DiaryEntry entry = getItem(position);

        // Check if the view is being recycled, if not, inflate it
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_card_layout, parent, false);
        }

        // Find the views in the custom card layout
        TextView diaryTitle = convertView.findViewById(R.id.diaryTitle);
        TextView diaryDate = convertView.findViewById(R.id.diaryDate);
        TextView diaryMood = convertView.findViewById(R.id.diaryMood);
        TextView diarySummary = convertView.findViewById(R.id.diarySummary);
        ImageView moodIndicator = convertView.findViewById(R.id.moodIndicator);

        // Populate the views with data from the DiaryEntry object
        if (entry != null) {
            diaryTitle.setText(entry.getTitle());
            diaryDate.setText(entry.getDate());
            diarySummary.setText(entry.getEntryText());
            /**if (entry.getEntryText().length() > 26) {
                diarySummary.setText(entry.getEntryText().substring(0, 25) + "...");
            } else {
                diarySummary.setText(entry.getEntryText());
            }*/

            switch(entry.getMood()) {
                case 0:
                    diaryMood.setText("Very Negative");
                    moodIndicator.setImageResource(R.drawable.mood_circle_vnegative);
                    break;
                case 1:
                    diaryMood.setText("Negative");
                    moodIndicator.setImageResource(R.drawable.mood_circle_negative);
                    break;
                case 2:
                    diaryMood.setText("Neutral");
                    moodIndicator.setImageResource(R.drawable.mood_circle_neutral);
                    break;
                case 3:
                    diaryMood.setText("Positive");
                    moodIndicator.setImageResource(R.drawable.mood_circle_positive);
                    break;
                case 4:
                    diaryMood.setText("Very Positive");
                    moodIndicator.setImageResource(R.drawable.mood_circle_vpositive);
                    break;
            }
        }

        return convertView;
    }
}