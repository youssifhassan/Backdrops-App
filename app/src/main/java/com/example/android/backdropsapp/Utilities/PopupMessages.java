package com.example.android.backdropsapp.Utilities;

import android.content.Context;
import android.widget.Toast;

public class PopupMessages {

    private static PopupMessages sharedInstance = null;

    private PopupMessages() {
    }

    public static PopupMessages getInstance(){
        if (sharedInstance == null) {
            sharedInstance = new PopupMessages();
        }
        return(sharedInstance);
    }

    public void defaultMessage(String title, Context context){
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }
}
