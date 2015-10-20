package com.offapps.off.Data;

import android.net.Uri;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by carlosdiez on 8/25/15.
 */
@ParseClassName(ParseConstants.CLASS_MALL)
public class Mall extends ParseObject {

    public String getName(){
        return getString(ParseConstants.KEY_NAME);
    }

    public void setName(String name){
        put(ParseConstants.KEY_NAME, name);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(ParseConstants.KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint location){
        put(ParseConstants.KEY_LOCATION, location);
    }

    public String getAddress(){
        return getString(ParseConstants.KEY_ADDRESS);
    }

    public void setAddress(String address){
        put(ParseConstants.KEY_ADDRESS, address);
    }

    public Uri getImageUri(){
        ParseFile file = getParseFile(ParseConstants.KEY_IMAGE);
        return Uri.parse(file.getUrl());
    }

    public String getHeaderString() {
        ParseFile file = getParseFile(ParseConstants.KEY_HEADER);
        return Uri.parse(file.getUrl()).toString();
    }

    public String getSchedule() {
        return getString(ParseConstants.KEY_SCHEDULE);
    }

    public void setSchedule(String schedule) {
        put(ParseConstants.KEY_SCHEDULE, schedule);
    }

    public String getTwitterUser(){
        return getString(ParseConstants.KEY_TWITTER_USER);
    }

    public void setTwitterUser(String twitterUser) {
        put(ParseConstants.KEY_TWITTER_USER, twitterUser);
    }

    public String getFacebookUser() {
        return getString(ParseConstants.KEY_FACEBOOK_USER);
    }

    public void setFacebookUser(String facebookUser) {
        put(ParseConstants.KEY_FACEBOOK_USER, facebookUser);
    }

    public String getWebPage() {
        return getString(ParseConstants.KEY_WEB_PAGE);
    }

    public void setWebPage(String webPage) {
        put(ParseConstants.KEY_WEB_PAGE, webPage);
    }

    public static ParseQuery<Mall> getQuery(){
        return ParseQuery.getQuery(Mall.class);
    }
}