package com.example.find4rescue.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("Risk")
public class Risk extends ParseObject {

    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_TYPE = "Type";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_IMAGE = "Image";
    public static final String KEY_RESCUER = "Rescuer";
    public static final String KEY_COORDINATES = "Coordinates";
    public static final String KEY_NUMOFRESCUERS = "NumOfRescuers";
    public static final String KEY_USERNAMES = "Usernames";
    public static final String KEY_DEALTORNOT = "DealtOrNot";

    public Risk() { }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String address) { put(KEY_ADDRESS, address);}

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) { put(KEY_TYPE, type); }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) { put(KEY_DESCRIPTION, description); }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) { put(KEY_IMAGE, image); }

    public ParseUser getRescuer() {
        return getParseUser(KEY_RESCUER);
    }

    public void setRescuer(ParseUser rescuer) { put(KEY_RESCUER, rescuer); }

    public String getCoordinates() { return getString(KEY_COORDINATES); }

    public void setCoordinates(String coordinates) { put(KEY_COORDINATES, coordinates); }

    public int getNumOfRescuers() { return getInt(KEY_NUMOFRESCUERS); }

    public void setNumOfRescuers(int numOfRescuers) { put(KEY_NUMOFRESCUERS, numOfRescuers); }

    public JSONArray getUsernames() { return getJSONArray(KEY_USERNAMES); }

    public void setUsernames(JSONArray usernames) { put(KEY_USERNAMES, usernames); }

    public JSONArray getDealtOrNot() { return getJSONArray(KEY_DEALTORNOT); }

    public void setDealtOrNot(JSONArray dealtOrNot) { put(KEY_DEALTORNOT, dealtOrNot); }

}
