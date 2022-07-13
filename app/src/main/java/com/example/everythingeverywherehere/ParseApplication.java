package com.example.everythingeverywherehere;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(UserLoginDetails.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("BWDbw1d2UreB8QFrPLncUfBZc9yMLCk57KpKKrBc")
                .clientKey("G76qmFw8Fx7tVf08nWYxAVYZg4yB15lt5yFEK0gj")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
