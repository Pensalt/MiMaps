package com.shlompie.mimaps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandmarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandmarksFragment extends Fragment {

    RecyclerView recyclerView;

    public LandmarksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LandmarksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LandmarksFragment newInstance() {
        return new LandmarksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_landmarks, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        ArrayList<String[]> favorite_addresses = new ArrayList<>();
        favorite_addresses.add(new String[]{"Home", "18 Montgomery Road"});
        favorite_addresses.add(new String[]{"Work", "3 First Street"});
        favorite_addresses.add(new String[]{"Shop", "1 Second Street"});

        MyAdapter myAdapter = new MyAdapter(view.getContext(), favorite_addresses);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }
}