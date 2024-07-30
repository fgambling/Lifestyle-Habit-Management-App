package com.comp90018.a2.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.comp90018.a2.R;
import com.comp90018.a2.diary.DiaryEntriesService;
import com.comp90018.a2.diary.DiaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MoodCalendar moodCalendar = MoodCalendar.getInstance();
    private TextView textViewMonthYear;
    private ImageView imageViewPrevious;
    private ImageView imageViewNext;
    private CustomCalendarAdapter customCalendarAdapter;
    List<MoodDay> moodDays;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // instantiate moodDays
        moodDays = new ArrayList<>();

        // set month display
        textViewMonthYear = view.findViewById(R.id.textViewMonthYear);
        textViewMonthYear.setText(moodCalendar.getYearMonthPretty());

        // create button listeners
        imageViewNext = view.findViewById(R.id.imageViewNext);
        imageViewPrevious = view.findViewById(R.id.imageViewPrevious);
        imageViewPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodCalendar.previousMonth();
                updateCalendar();
                textViewMonthYear.setText(moodCalendar.getYearMonthPretty());
            }
        });

        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodCalendar.nextMonth();
                updateCalendar();
                textViewMonthYear.setText(moodCalendar.getYearMonthPretty());
            }
        });

        // set grid view for calendar
        GridView gridViewCalendar = view.findViewById(R.id.gridViewCalendar);

        // dummy data for testing
//        Random random = new Random();
//        for (int i = 1; i <= 31; i++) {
//            moodDays.add(new MoodDay(i + "", random.nextInt(6)-1));
//        }

        // load diary entries into mood calendar
        moodCalendar.reload();

        // set grid adapter
        customCalendarAdapter = new CustomCalendarAdapter(getContext(), moodDays);
        gridViewCalendar.setAdapter(customCalendarAdapter);
        updateCalendar();

        return view;
    }

    private void updateCalendar(){
        moodDays.clear();
        moodDays.addAll(moodCalendar.getMoodDays());
        customCalendarAdapter.notifyDataSetChanged();
    }
}