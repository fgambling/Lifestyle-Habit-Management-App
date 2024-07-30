package com.comp90018.a2.habits;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comp90018.a2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class HabitFragment extends Fragment {
    private LinearLayout habitsLayout;
    private HabitEntriesService habitService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habits, container, false);
        habitsLayout = view.findViewById(R.id.habitsLayout);

        // initialize habit service and set up the listener
        habitService = HabitEntriesService.getInstance();

        habitService.addOnHabitsChangedListener(new HabitEntriesService.OnHabitsChangedListener() {
            @Override
            public void onHabitsChanged(List<HabitEntry> habits) {
                //Log.d("HabitTest", "tt");
                //habitService.loadEntries();
                loadHabitsFromDatabase();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inflate the layout for this fragment
        FloatingActionButton createHabit = view.findViewById(R.id.fag);

        createHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a new screen/activity for creating a new diary entry
                openCreateHabit();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("HabitTest", "tt");
        habitService.loadEntries();
        loadHabitsFromDatabase();
    }


    private void openCreateHabit() {
        Intent intent = new Intent(getActivity(), CreateHabit.class);
        startActivity(intent);
    }

    private void addNewHabit(String id, String title, String startDate, String endDate) {
        View habitView = LayoutInflater.from(getContext()).inflate(R.layout.habit_item, habitsLayout, false);
        TextView habitTitle = habitView.findViewById(R.id.habitTitle);
        habitTitle.setText(title);

        TextView habitDetails = habitView.findViewById(R.id.habitDetails);
        if (endDate.equals("9999/12/31")) {
            habitDetails.setText(startDate + " - No End Date");
        } else {
            habitDetails.setText(startDate + " - " + endDate);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // Adjust the format to match dates
            Date end = sdf.parse(endDate);
            Date now = new Date();
            if (now.after(end)) {
                // Set background color to grey
                habitView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGray));
            }
        } catch (Exception e) {
            Log.e("HabitFragment", "Parsing date failed", e);
        }

        // Add click listener
        habitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Habittest", "habit success" + id);
                Intent intent = new Intent(getActivity(), UpdateHabit.class);
                intent.putExtra("habitId", id);
                intent.putExtra("title", title);
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);
                startActivity(intent);
            }
        });

        habitsLayout.addView(habitView);
    }

    private void loadHabitsFromDatabase() {
        //Log.d("Habittest", "test");
        //habitService = HabitEntriesService.getInstance();
        habitsLayout.removeAllViews();

        // Load entries from Firebase
        //habitService.loadEntries();
        List<HabitEntry> allHabits = habitService.getAllEntries();
        //Log.d("HabitService", allHabits.toString());

        // For each habit, create a view in the habitsLayout
        for (HabitEntry habit : allHabits) {
            addNewHabit(habit.getId(), habit.getTitle(), habit.getStartDate(), habit.getEndDate());
        }
    }
}