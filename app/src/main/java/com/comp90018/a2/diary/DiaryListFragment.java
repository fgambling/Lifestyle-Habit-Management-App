package com.comp90018.a2.diary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.comp90018.a2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiaryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayAdapter<DiaryEntry> diaryListAdapter;

    // access the diary entry service
    private final DiaryEntriesService entriesService = DiaryEntriesService.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiaryListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiaryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiaryListFragment newInstance(String param1, String param2) {
        DiaryListFragment fragment = new DiaryListFragment();
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
        View view = inflater.inflate(R.layout.fragment_diary_list, container, false);
        ListView diaryListView = view.findViewById(R.id.diaryListView);
        FloatingActionButton fabCreateEntry = view.findViewById(R.id.fab);

        // use the adapter to display diary entries in card format
        entriesService.setDiaryListAdapter(new CustomCardAdapter(requireContext(), entriesService.getAllEntries()));
        diaryListAdapter = entriesService.getDiaryListAdapter();
        diaryListView.setAdapter(entriesService.getDiaryListAdapter());


        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DiaryEntry selectedEntry = diaryListAdapter.getItem(position);
                assert selectedEntry != null;
                openDiaryEntry(selectedEntry);
            }
        });

        fabCreateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a new screen/activity for creating a new diary entry
                openCreateEntryScreen();
            }
        });
        return view;
    }
    private void openDiaryEntry(DiaryEntry entry) {
        Intent intent = new Intent(requireContext(), DiaryEntryActivity.class);
        intent.putExtra("id", entry.getId()); // pass id into intent
        startActivity(intent);
    }

    private void openCreateEntryScreen() {
        Intent intent = new Intent(requireContext(), CreateDiaryEntryAction.class);
        startActivity(intent);
    }
}