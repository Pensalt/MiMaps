package com.shlompie.mimaps;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private View view;

// allowing seb to pull

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings,container,false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        EditText emailField = view.findViewById(R.id.emailET_settings);
        emailField.setText(currentUser.getEmail());

        Switch swUnitsPrefs = view.findViewById(R.id.swUnitsPrefs);
        CheckBox outdoor_chk = view.findViewById(R.id.outdoor_chk);
        CheckBox dining_chk = view.findViewById(R.id.dining_chk);
        CheckBox cultural_chk = view.findViewById(R.id.cultural_chk);

        outdoor_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dining_chk.setChecked(false);
                cultural_chk.setChecked(false);
            }
        });
        dining_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outdoor_chk.setChecked(false);
                cultural_chk.setChecked(false);
            }
        });
        cultural_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outdoor_chk.setChecked(false);
                dining_chk.setChecked(false);
            }
        });

        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent goToMain = new Intent(v.getContext(), LoginActivity.class);
                startActivity(goToMain);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user_preferences").whereEqualTo("user_email", currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();

                        swUnitsPrefs.setChecked((Boolean) data.get("metric"));
                        outdoor_chk.setChecked((Boolean) data.get("outdoor"));
                        dining_chk.setChecked((Boolean) data.get("dining"));
                        cultural_chk.setChecked((Boolean) data.get("cultural"));
                    }
                } else {

                }
            }
        });

        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                data.put("user_email", currentUser.getEmail());
                data.put("metric", swUnitsPrefs.isChecked());
                data.put("outdoor", outdoor_chk.isChecked());
                data.put("dining", dining_chk.isChecked());
                data.put("cultural", cultural_chk.isChecked());

                db.collection("user_preferences").document(currentUser.getEmail()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(view.getContext(), "Settings Updated", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}