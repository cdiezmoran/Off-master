package com.offapps.off.Data;

import com.offapps.off.Misc.ParseConstants;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by carlosdiez on 9/4/15.
 */
@ParseClassName(ParseConstants.CLASS_COMMENTS)
public class Comment extends ParseObject {

    public String getText() {
        return getString(ParseConstants.KEY_TEXT);
    }

    public void setText(String text) {
        put(ParseConstants.KEY_TEXT, text);
    }

    public ParseUser getParseUser(){
        return getParseUser(ParseConstants.KEY_USER);
    }

    public void setUser(ParseUser user){
        put(ParseConstants.KEY_USER, user);
    }

    public Offer getOffer(){
        return (Offer) getParseObject(ParseConstants.KEY_OFFER);
    }

    public void setOffer(Offer offer){
        put(ParseConstants.KEY_OFFER, offer);
    }

    public static ParseQuery<Comment> getQuery(){
        return ParseQuery.getQuery(Comment.class);
    }
}
