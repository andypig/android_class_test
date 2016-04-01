package com.example.diva.simpleui;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;

/**
 * Created by DIVA on 2016/3/21.
 */
public class SimpleUIApplication extends Application{
    @Override
    public void onCreate(){
        super.onCreate();

        // Parse 的使用設定
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        // facebook 的使用設定:
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
