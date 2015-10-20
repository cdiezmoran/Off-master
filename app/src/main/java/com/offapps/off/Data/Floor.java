package com.offapps.off.Data;

import android.net.Uri;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by carlosdiez on 9/24/15.
 */
@ParseClassName(ParseConstants.CLASS_FLOOR)
public class Floor extends ParseObject {
    public String getName(){
        return getString(ParseConstants.KEY_NAME);
    }

    public void setName(String name) {
        put(ParseConstants.KEY_NAME, name);
    }

    public Mall getMall() {
        return (Mall) getParseObject(ParseConstants.KEY_MALL);
    }

    public void setMall(Mall mall) {
        put(ParseConstants.KEY_MALL, mall);
    }

    public String getImageString() {
        ParseFile file = getParseFile(ParseConstants.KEY_IMAGE);
        return Uri.parse(file.getUrl()).toString();
    }

    public static ParseQuery<Floor> getQuery(){
        return ParseQuery.getQuery(Floor.class);
    }
}
