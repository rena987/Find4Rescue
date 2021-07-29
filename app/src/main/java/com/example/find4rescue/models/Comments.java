package com.example.find4rescue.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

@ParseClassName("Comments")
public class Comments extends ParseObject {

    public static final String KEY_COMMENTEDRISK = "CommentedRisk";
    public static final String KEY_USERNAMES = "Usernames";
    public static final String KEY_MESSAGES = "Messages";

    public Comments() { }

    public ParseObject getRisk() {
        return getParseObject(KEY_COMMENTEDRISK);
    }

    public void setRisk(ParseObject risk) {
        put(KEY_COMMENTEDRISK, risk);
    }

    public JSONArray getUsernames() {
        return getJSONArray(KEY_USERNAMES);
    }

    public void setUsernames(JSONArray usernames) {
        put(KEY_USERNAMES, usernames);
    }

    public JSONArray getMessages() {
        return getJSONArray(KEY_MESSAGES);
    }

    public void setMessages(JSONArray messages) {
        put(KEY_MESSAGES, messages);
    }

}
