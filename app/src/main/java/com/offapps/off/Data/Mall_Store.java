package com.offapps.off.Data;

import android.net.Uri;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by carlosdiez on 10/3/15.
 */
@ParseClassName(ParseConstants.TABLE_MALL_STORES)
public class Mall_Store extends ParseObject {

    public Mall getMall() {
        return (Mall) getParseObject(ParseConstants.KEY_MALL);
    }

    public void setMall(Mall mall) {
        put(ParseConstants.KEY_MALL, mall);
    }

    public Store getStore() {
        return (Store) getParseObject(ParseConstants.KEY_STORE);
    }

    public void setStore(Store store) {
        put(ParseConstants.KEY_STORE, store);
    }

    public Floor getFloor() {
        return (Floor) getParseObject(ParseConstants.KEY_FLOOR);
    }

    public void setFloor(Floor floor) {
        put(ParseConstants.KEY_FLOOR, floor);
    }

    public String getImageString() {
        ParseFile file = getParseFile(ParseConstants.KEY_IMAGE);
        return Uri.parse(file.getUrl()).toString();
    }

    public static ParseQuery<Mall_Store> getQuery() {
        return ParseQuery.getQuery(Mall_Store.class);
    }
}
