package com.example.android.backdropsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.android.backdropsapp.Services.UserService;
import com.example.android.backdropsapp.Utilities.PopupMessages;
import com.example.android.backdropsapp.Utilities.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prepareMainView();
        checkIfUserAlreadyLogin();
    }

    private void prepareMainView() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progress = new ProgressDialog(this, R.style.MyAlertDialogStyle);
    }

    private void checkIfUserAlreadyLogin() {
        if (UserService.getInstance().getFirebaseAuth().getCurrentUser() != null) {
            startHomeActivity();
        }
    }

    public void login(View view) {
        if (checkValidation()) {
            loginAuthWithEmailAndPassword();
        }
    }

    private boolean checkValidation() {

        String user_email = email.getText().toString();
        String user_password = password.getText().toString();

        if (!Validation.getInstance().isValidEmail(user_email)) {
            emailErrorMessage(email);
            return false;
        }

        if (!Validation.getInstance().isValidPassword(user_password)) {
            passwordErrorMessage(password);
            return false;
        }

        return true;
    }

    private void loginAuthWithEmailAndPassword() {
        showDialog();
        UserService.getInstance().getFirebaseAuth().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PopupMessages.getInstance().defaultMessage("Login Successfull", LoginActivity.this);
                            startHomeActivity();
                        }
                        if (!task.isSuccessful()) {
                            checkIfEmailIsRegistered();
                        }
                        hideDialog();
                    }
                });

    }

    private void checkIfEmailIsRegistered() {
        UserService.getInstance().getFirebaseAuth().fetchSignInMethodsForEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(Task<SignInMethodQueryResult> task) {
                        if (task != null){
                            boolean checkIfEmailIsRegistered = !task.getResult().getSignInMethods().isEmpty();
                            if (checkIfEmailIsRegistered) {
                                PopupMessages.getInstance().defaultMessage("This Email Correct, Password Wrong",LoginActivity.this);
                            } else {
                                PopupMessages.getInstance().defaultMessage("This Email & Password not Registered",LoginActivity.this);
                            }
                        }
                    }
                });
    }

    public void signUp(View view) {
        startSignUpActivity();
    }

    private void emailErrorMessage(EditText email) {
        email.setError("Enter A Valid Email Address");
    }

    private void passwordErrorMessage(EditText password) {
        password.setError("Password Must Contain Characters And Numbers");
    }

    private void startHomeActivity() {
        Intent homeActivityIntent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(homeActivityIntent);
        finish();
    }

    private void startSignUpActivity() {
        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    private void showDialog() {
        progress.setMessage("Loading....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void hideDialog() {
        progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}