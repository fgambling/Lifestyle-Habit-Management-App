package com.comp90018.a2.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.comp90018.a2.R;

import java.util.List;

public class CustomCalendarAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<MoodDay> moodDays;

    public CustomCalendarAdapter(Context context, List<MoodDay> moodDays) {
        this.context = context;
        this.moodDays = moodDays;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return moodDays.size();
    }

    @Override
    public Object getItem(int position) {
        return moodDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_day_item, parent, false);
        }

        // Get references to TextViews in the custom layout
        TextView textDayNumber = convertView.findViewById(R.id.textDayNumber);

        // Set the day number and events (customize as needed)
        textDayNumber.setText(moodDays.get(position).getDay());
        // Set events or other content for the day as needed

        // Set the background color based on cellStatus
        int status = moodDays.get(position).getMood();
        if (status == 0)
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.very_negative)); // Change color based on your condition
        else if (status == 1) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.negative));
        } else if (status == 2) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.neutral));
        } else if (status == 3) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.positive));
        }
        else if (status == 4) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.very_positive));
        }
        else {
            // Default background color
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.background));
        }
        return convertView;
    }
}