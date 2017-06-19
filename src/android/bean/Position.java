package com.ruite.location.bean;


import org.json.JSONException;
import org.json.JSONObject;

public class Position {
    private Coords coords;
    private String timestamp;
    private int locType;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("timestamp", timestamp);
            json.put("coords", coords.toJSON());
            json.put("locType", locType);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getLocType() {
        return locType;
    }

    public void setLocType(int locType) {
        this.locType = locType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

}
