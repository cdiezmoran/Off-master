package com.offapps.off.Misc;

import android.app.Application;

import com.offapps.off.Data.Comment;
import com.offapps.off.Data.Floor;
import com.offapps.off.Data.Follow;
import com.offapps.off.Data.Like;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Mall_Store;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Carlos Diez on 12/06/2015.
 */
public class OffApplication extends Application{

    public static final String APPTAG = "Off";

    public static ConfigHelper configHelper;

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(Offer.class);
        ParseObject.registerSubclass(Store.class);
        ParseObject.registerSubclass(Mall.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Follow.class);
        ParseObject.registerSubclass(Floor.class);
        ParseObject.registerSubclass(Mall_Store.class);
        Parse.initialize(this, "Uqc86Bki5t3xKiFdT6ssSCvF8S9oi5VBVDrP5YaF", "0QHBVcYQDlRHSwtWbEcn2gz4RGSrgc9kZqjokYXk");

        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();
    }

    /*public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }*/

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }
}
