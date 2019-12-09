package com.example.android.backdropsapp.Services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserService {

    private static FirebaseAuth mFirebaseAuth;
    private static UserService sharedInstance = null;

    private UserService() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    };

    public static UserService getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new UserService();
        }
        return(sharedInstance);
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }
}
