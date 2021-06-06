package com.shlompie.mimaps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

public class SettingsFragment extends Fragment {
    private View view;
    ImageView logoImg;
    TextInputLayout emailTxtInputLayout;
    SwitchMaterial metSwitch;
    Button logoutBTN;




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

        metSwitch = view.findViewById(R.id.swUnitsPrefs);
        emailTxtInputLayout = view.findViewById(R.id.email_text);
        logoutBTN = view.findViewById(R.id.btnLogout);

        // Inflate the layout for this fragment
        return view;
    }
}