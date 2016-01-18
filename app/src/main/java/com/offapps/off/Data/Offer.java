package com.offapps.off.Data;

import android.net.Uri;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by carlosdiez on 8/25/15.
 *
 */
@ParseClassName(ParseConstants.CLASS_OFFER)
public class Offer extends ParseObject {

    public String getName(){
        return getString(ParseConstants.KEY_NAME);
    }

    public void setName(String name){
        put(ParseConstants.KEY_NAME, name);
    }

    public String getDescription(){
        return getString(ParseConstants.KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(ParseConstants.KEY_DESCRIPTION, description);
    }

    public int getFromPercentage(){
        return getInt(ParseConstants.KEY_FROM_PERCENTAGE);
    }

    public void setFromPercentage(int fromPercentage){
        put(ParseConstants.KEY_FROM_PERCENTAGE, fromPercentage);
    }

    public int getToPercentage(){
        return getInt(ParseConstants.KEY_TO_PERCENTAGE);
    }

    public void setToPercentage(int toPercentage){
        put(ParseConstants.KEY_TO_PERCENTAGE, toPercentage);
    }

    public Store getStore(){
        return (Store) getParseObject(ParseConstants.KEY_STORE);
    }

    public void setStore(Store store){
        put(ParseConstants.KEY_STORE, store);
    }

    public String getImageString(){
        ParseFile file = getParseFile(ParseConstants.KEY_IMAGE);
        return Uri.parse(file.getUrl()).toString();
    }

    public Date getFromDate(){
        return getDate(ParseConstants.KEY_FROM_DATE);
    }

    public void setFromDate(Date fromDate){
        put(ParseConstants.KEY_FROM_DATE, fromDate);
    }

    public Date getToDate(){
        return getDate(ParseConstants.KEY_TO_DATE);
    }

    public void setToDate(Date toDate){
        put(ParseConstants.KEY_TO_DATE, toDate);
    }

    public int getLikeCount(){
        return getNumber(ParseConstants.KEY_LIKE_COUNT).intValue();
    }

    public void setLikeCount(int likeCount){
        put(ParseConstants.KEY_LIKE_COUNT, likeCount);
    }

    public int getViewCount() {
        return getNumber(ParseConstants.KEY_VIEW_COUNT).intValue();
    }

    public void setViewCount(int viewCount) {
        put(ParseConstants.KEY_VIEW_COUNT, viewCount);
    }

    public int getPopularity() {
        return getNumber(ParseConstants.KEY_POPULARITY).intValue();
    }

    public void setPopularity(int popularity) {
        put(ParseConstants.KEY_POPULARITY, popularity);
    }

    public static ParseQuery<Offer> getQuery(){
        return ParseQuery.getQuery(Offer.class);
    }
}
