package com.offapps.off.Data;

import android.net.Uri;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by carlosdiez on 8/25/15.
 */
@ParseClassName(ParseConstants.CLASS_STORE)
public class Store extends ParseObject {

    public String getName(){
        return getString(ParseConstants.KEY_NAME);
    }

    public void setName(String name){
        put(ParseConstants.KEY_NAME, name);
    }

    public String getImageString(){
        ParseFile file = getParseFile(ParseConstants.KEY_IMAGE);
        return Uri.parse(file.getUrl()).toString();
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

    public String getHeaderString() {
        ParseFile file = getParseFile(ParseConstants.KEY_HEADER);
        return Uri.parse(file.getUrl()).toString();
    }

    public String getCategory(){
        return getString(ParseConstants.KEY_CATEGORY);
    }

    public void setCategory(String category) {
        put(ParseConstants.KEY_CATEGORY, category);
    }

    public int getFollowers() {
        return getNumber(ParseConstants.KEY_FOLLOWERS).intValue();
    }

    public void setFollowers(int followers) {
        put(ParseConstants.KEY_FOLLOWERS, followers);
    }

    public static ParseQuery<Store> getQuery(){
        return ParseQuery.getQuery(Store.class);
    }
}
