package com.example.android.backdropsapp.Utilities;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class Validation {

    private static Validation sharedInstance = null;

    private Validation() {};

    public static Validation getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new Validation();
        }
        return(sharedInstance);
    }

    public boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public boolean isValidPassword(String password) {
        return (!TextUtils.isEmpty(password) && passwordPattern.matcher(password).matches());
    }

    public boolean isValidConfirmPassword(String password, String confirmPassword){
        return (password.equals(confirmPassword));
    }

    private final Pattern passwordPattern
            = Pattern.compile("^" +
            "(?=.*[a-zA-Z])" +
            "(?=\\S+$)" +
            ".{6,}" +
            "$"
    );
}