package com.shlompie.mimaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<String[]> favorite_landmarks;

    public MyAdapter(Context ct, ArrayList<String[]> favorite_landmarks) {
        context = ct;
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
                Toast.makeText(v.getContext(), favorite_landmarks.get(position)[1] + ", " + favorite_landmarks.get(position)[2], Toast.LENGTH_SHORT).show();
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.row_title);
            view_btn = itemView.findViewById(R.id.view_btn);
        }

    }
}
