package com.shlompie.mimaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // Declaring an instance of FirebaseAuth

    EditText emailET, passwordET, confirmPasswordET;
    Button confirmBtn;
    ImageView logoImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // SU stands for sign up.
        emailET = findViewById(R.id.etEmailSU);
        passwordET = findViewById(R.id.etPasswordSU);
        confirmPasswordET = findViewById(R.id.etConPassSU);
        confirmBtn = findViewById(R.id.btnSignUpSU);


        mAuth = FirebaseAuth.getInstance(); // Initializing the FirebaseAuth instance.

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailET.getText().toString().trim(); // Whitespace is not valid in an email address.
                String password = passwordET.getText().toString(); // Passwords are not trimmed as whitespace is valid in a password.
                String confirmPassword = confirmPasswordET.getText().toString();

                if (email.equals("") || password.equals("") || confirmPassword.equals(""))
                {
                    Toast.makeText(SignUpActivity.this, "Please fill out all fields!", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(confirmPassword))
                {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Log.i("signup","Sign Up successful");

                                // Intent to go to the main page.
                                Intent goToMain = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(goToMain);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show(); // customise this error message
                        }
                    });
                }

            }
        });
    }
}