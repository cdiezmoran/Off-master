package com.offapps.off.Data;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by carlosdiez on 10/18/15.
 */
@ParseClassName(ParseConstants.CLASS_FOLLOWS)
public class Follow extends ParseObject {

    public ParseUser getUser() {
        return getParseUser(ParseConstants.KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(ParseConstants.KEY_USER, user);
    }

    public Store getStore() {
        return (Store) getParseObject(ParseConstants.KEY_STORE);
    }

    public void setStore(Store store) {
        put(ParseConstants.KEY_STORE, store);
    }

    public static ParseQuery<Follow> getQuery(){
        return ParseQuery.getQuery(Follow.class);
    }
}
