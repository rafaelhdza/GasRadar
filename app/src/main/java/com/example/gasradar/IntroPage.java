package com.example.gasradar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;




public class IntroPage extends AppCompatActivity {

    Button btnSignUpEmail;
    Button btnLoginPage;
    Button btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUpEmail = findViewById(R.id.SignUpMainButton);
        btnSignUpEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroPage.this, SignUp.class));
            }
        });

        btnLoginPage = findViewById(R.id.LogInMainButton);
        btnLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroPage.this, LoginPage.class));
            }
        });

        btnGuest = findViewById(R.id.GuestButton);
        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroPage.this, MapsActivity.class));
            }
        });

    }




}
