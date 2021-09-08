package com.example.coleb19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences sharedpreferences;
    TextView signUpLink;
    TextView resetPasswordLink;
    EditText mailLogin;
    EditText passLogin;
    ProgressDialog progressDialog ;


    public void login(View view) {

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String email = mailLogin.getText().toString().trim();
        String password = passLogin.getText().toString().trim();

        if(email.isEmpty()) {
            mailLogin.setError("Email is required!");
            mailLogin.requestFocus();
            return;
        }

        if(CommonMethods.isNotAnEmail(email)) {
            mailLogin.setError("Please provide a correct email address!");
            mailLogin.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passLogin.setError("Password is required!");
            passLogin.requestFocus();
            return;
        }

        if(CommonMethods.checkIfPassLengthNotValid(password)) {
            passLogin.setError("Minimum password length is 6 characters!");
            passLogin.requestFocus();
            return;
        }

        CommonMethods.displayLoadingScreen(progressDialog);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("key", 1);
                    editor.apply();
                    CommonMethods.hideProgressDialog(progressDialog);
                    Intent intent = new Intent(getApplicationContext(), RegisterAsCovid19PatientActivity.class);
                    startActivity(intent);

                    CommonMethods.hideProgressDialog(progressDialog);
                }
                else{
                    CommonMethods.hideProgressDialog(progressDialog);
                    Toast.makeText(LoginActivity.this, "Failed to login! Please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    public void viewMap(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hideKeyboard();
        progressDialog = new ProgressDialog(LoginActivity.this);
        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

        int j = sharedpreferences.getInt("key", 0);

        if(j > 0){
            //auto login
            Intent activity = new Intent(getApplicationContext(), RegisterAsCovid19PatientActivity.class);
            startActivity(activity);
        }
        else
        {
           // stay in login page
        }
        mailLogin = findViewById(R.id.mailLogin);
        passLogin = findViewById(R.id.passwordLogin);

        signUpLink = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        resetPasswordLink = findViewById(R.id.resetPasswordLinkk);
        resetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }

    private void hideKeyboard() {
        try {
        this.getSupportActionBar().hide();
    }
    catch (NullPointerException e) {
        e.printStackTrace();
    }
    }





}