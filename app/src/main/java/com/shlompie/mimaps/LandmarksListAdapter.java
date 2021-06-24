 package com.shlompie.mimaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class LandmarksListAdapter extends RecyclerView.Adapter<LandmarksListAdapter.MyViewHolder> {

    Context context;
    ArrayList<String[]> favorite_landmarks;

    public LandmarksListAdapter(Context ct, ArrayList<String[]> favorite_landmarks) {
        context = ct; // Declaring the context.
        this.favorite_landmarks = favorite_landmarks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(favorite_landmarks.get(position)[0]);
        holder.view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // POI geo information is sent to the map. This will be used to generate a route to the landmark from the users current location.
                MainActivity.getStaticFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragment(new LatLng(Double.parseDouble(favorite_landmarks.get(position)[1]), Double.parseDouble(favorite_landmarks.get(position)[2])))).commit();
            }
        });
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = favorite_landmarks.get(position)[3];

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("saved_landmarks").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.getStaticFragmentManager().beginTransaction().replace(R.id.fragment_container, new LandmarksFragment()).commit();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorite_landmarks.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        Button view_btn;
        Button delete_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.row_title);
            view_btn = itemView.findViewById(R.id.view_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }

    }
}
