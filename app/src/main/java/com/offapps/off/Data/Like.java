package com.offapps.off.Data;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by carlosdiez on 9/13/15.
 */
@ParseClassName(ParseConstants.CLASS_LIKES)
public class Like extends ParseObject {

    public Offer getOffer(){
        return (Offer) getParseObject(ParseConstants.KEY_OFFER);
    }

    public void setOffer(Offer offer) {
        put(ParseConstants.KEY_OFFER, offer);
    }

    public ParseUser getUser() {
        return getParseUser(ParseConstants.KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(ParseConstants.KEY_USER, user);
    }

    public static ParseQuery<Like> getQuery(){
        return ParseQuery.getQuery(Like.class);
    }
}
