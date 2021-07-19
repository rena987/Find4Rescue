package com.example.find4rescue;

import android.app.Application;

import com.example.find4rescue.models.Risk;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Risk.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("uRtqd8cWyeLBVGXaOJm7BrFPQj6WAEBrwg4iFZSS")
                .clientKey("YyCxsuQQ8zq4qvW19X3qzD4o7CR1clJjL4SlMdRW")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}
