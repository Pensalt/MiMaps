package com.shlompie.mimaps;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class LandmarksFragment extends Fragment {

    RecyclerView recyclerView;

    public LandmarksFragment() {
        // Required empty public constructor
    }

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

        // Getting firebase instances.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<String[]> favorite_addresses = new ArrayList<>(); // Arraylist of the user's favourite addresses.

        // Handling getting the user's saved landmarks.
        db.collection("saved_landmarks").whereEqualTo("user_email", currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        // Adding each of the current user's favourite addresses so they can be viewed or deleted.
                        favorite_addresses.add(new String[]{data.get("title").toString(), data.get("latitude").toString(), data.get("longitude").toString(), document.getId()});
                    }

                    LandmarksListAdapter landmarksListAdapter = new LandmarksListAdapter(view.getContext(), favorite_addresses);
                    recyclerView.setAdapter(landmarksListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                } else {

                }
            }
        });

        return view;
    }
}