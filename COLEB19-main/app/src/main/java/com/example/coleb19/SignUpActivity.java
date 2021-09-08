package com.example.coleb19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText name;
    EditText mailSignUp;
    EditText passSignUp;
    EditText confirmPassword;

    TextView loginLink;

    ProgressDialog progressDialog;


    public void signUp(View view) {

        final String namee = name.getText().toString();
        final String email = mailSignUp.getText().toString().trim();
        String password = passSignUp.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if(namee.isEmpty()){
            name.setError("Name is required!");
            name.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            mailSignUp.setError("Email is required!");
            mailSignUp.requestFocus();
            return;
        }

        if(CommonMethods.isNotAnEmail(email)) {
            mailSignUp.setError("Please provide a correct email address!");
            mailSignUp.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passSignUp.setError("Password is required!");
            passSignUp.requestFocus();
            return;
        }

        if(CommonMethods.checkIfPassLengthNotValid(password)) {
            passSignUp.setError("Minimum password length is 6 characters!");
            passSignUp.requestFocus();
            return;
        }

        if(confirmPass.isEmpty()) {
            confirmPassword.setError("You need to confirm your password!");
            confirmPassword.requestFocus();
            return;
        } else if(!CommonMethods.checkIfConfirmPassMatchesPass(confirmPass, password)){
            confirmPassword.setError("Your passwords didn't match! Please recheck.");
            confirmPassword.requestFocus();
            return;
        }

        CommonMethods.displayLoadingScreen(progressDialog);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserClass user = new UserClass(namee, email);

                    FirebaseDatabase.getInstance().getReference("User")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                    CommonMethods.hideProgressDialog(progressDialog);
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Account already exists! Please try again with a different email address.", Toast.LENGTH_SHORT).show();
                    CommonMethods.hideProgressDialog(progressDialog);
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(SignUpActivity.this);

        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        name = findViewById(R.id.nameSignUp);
        mailSignUp = findViewById(R.id.mailSignUp);
        passSignUp = findViewById(R.id.passwordSignUp);
        confirmPassword = findViewById(R.id.confirmPasswordSignUp);

        loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }


}