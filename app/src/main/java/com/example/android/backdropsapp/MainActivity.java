package com.example.android.backdropsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.android.backdropsapp.Utilities.Constants.SPLASH_SCREEN_TIMER;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean firstTimeToOpenApp;
    private ImageView icon;
    private TextView backDrops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateVariables();
        checkIfFirstTimeToOpenApp();
    }

    private void initiateVariables(){
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        firstTimeToOpenApp = sharedPreferences.getBoolean("firstTimeToOpenApp",true);
        icon = findViewById(R.id.icon);
        backDrops = findViewById(R.id.backDrops);
    }

    private void checkIfFirstTimeToOpenApp() {
        if (firstTimeToOpenApp){
            setAnimation();
            setSplashScreen();
        }else {
            startLoginActivityIntent();
        }
    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.splash_screen_transtion);
        icon.startAnimation(animation);
        backDrops.startAnimation(animation);
    }

    private void setSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editSharedPreferences();
                startLoginActivityIntent();
            }
        }, SPLASH_SCREEN_TIMER);
    }

    private void startLoginActivityIntent(){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void editSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstTimeToOpenApp", false);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
    }
}
