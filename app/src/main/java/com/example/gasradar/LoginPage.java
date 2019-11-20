package com.example.gasradar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginPage extends AppCompatActivity {

    EditText emailId, password;
    Button btnLogin;
    TextView textSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.LogInEmail);
        password = findViewById(R.id.LogInPassword);
        btnLogin = findViewById(R.id.LogInButton);
        textSignUp = findViewById(R.id.textSignUp);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseAuth != null){
                    Toast.makeText(LoginPage.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginPage.this, MapsActivity.class);
                    startActivity(i);

                }
                else {
                    Toast.makeText(LoginPage.this, "Please Login", Toast.LENGTH_SHORT).show();
                }

            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter your email id");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if (email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(LoginPage.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(LoginPage.this, "Sign Up Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                startActivity(new Intent(LoginPage.this, MapsActivity.class));;
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(LoginPage.this, "Error occurred, Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignUp.class);
                startActivity(intent);
            }
        });

    }
}
