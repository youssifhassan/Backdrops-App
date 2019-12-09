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
import com.google.firebase.auth.UserProfileChangeRequest;


public class SignUpActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        prepareMainView();
    }

    private void prepareMainView() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        progress = new ProgressDialog(this, R.style.MyAlertDialogStyle);
    }

    public void signUp(View view) {
        if (checkValidation()){
            checkIfEmailIsRegistered();
        }
    }

    private boolean checkValidation() {

        String user_email = email.getText().toString();
        String user_password = password.getText().toString();
        String user_confirm_password = confirmPassword.getText().toString();

        if (!Validation.getInstance().isValidEmail(user_email)) {
            emailErrorMessage(email);
            return false;
        }

        if (!Validation.getInstance().isValidPassword(user_password)) {
            passwordErrorMessage(password);
            return false;
        }

        if (!Validation.getInstance().isValidConfirmPassword(user_password, user_confirm_password)) {
            confirmPasswordErrorMessage(confirmPassword);
            return false;
        }
        return true;
    }

    private void checkIfEmailIsRegistered() {
        showDialog();
        UserService.getInstance().getFirebaseAuth().fetchSignInMethodsForEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(Task<SignInMethodQueryResult> task) {
                if (task != null){
                    boolean checkIfEmailIsRegistered = !task.getResult().getSignInMethods().isEmpty();
                    if (checkIfEmailIsRegistered) {
                        PopupMessages.getInstance().defaultMessage("This Email Already Registered",SignUpActivity.this);
                        hideDialog();
                    } else {
                        registration(email.getText().toString(), password.getText().toString());
                    }
                }
            }
        });
    }

    private void registration(final String email, String password) {

        UserService.getInstance().getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task != null){
                            if (task.isSuccessful()){
                                userRegisterSuccessful();
                                startHomeActivity();
                            }else {
                                PopupMessages.getInstance().defaultMessage("SignUp Not Successful,Please Try Again", SignUpActivity.this);
                            }
                        }

                        hideDialog();
                    }
                });
    }

    private void userRegisterSuccessful(){
        String userName = email.getText().toString();
        int index = userName.indexOf('@');
        userName = userName.substring(0, index);

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName).build();
        UserService.getInstance().getFirebaseAuth().getCurrentUser().updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            PopupMessages.getInstance().defaultMessage("User Profile Updated", SignUpActivity.this);
                        }
                    }
                });
    }

    public void login(View view) {
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    private void startHomeActivity() {
        Intent homeIntent = new Intent(SignUpActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    private void emailErrorMessage(EditText email) {
        email.setError("Enter A Valid Email Address");
    }

    private void passwordErrorMessage(EditText password) {
        password.setError("Password Must Contain Characters And Numbers");
    }

    private void confirmPasswordErrorMessage(EditText confirmPassword) {
        confirmPassword.setError("The Passwords doesn't Matches");
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
