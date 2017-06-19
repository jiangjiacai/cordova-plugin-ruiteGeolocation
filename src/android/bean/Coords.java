package com.ruite.location.bean;


import org.json.JSONException;
import org.json.JSONObject;

public class Coords {

    /**
     * 纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 高度
     */
    private double altitude;
    /**
     * 精确度
     */
    private double accuracy;
    /**
     * 高度准确度
     */
    private double altitudeAccuracy;
    /**
     * 方向
     */
    private double heading;
    /**
     * 速度
     */
    private double speed;


    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("altitude", altitude);
            json.put("accuracy", accuracy);
            json.put("altitudeAccuracy", altitudeAccuracy);
            json.put("heading", heading);
            json.put("speed", speed);

            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitudeAccuracy() {
        return altitudeAccuracy;
    }

    public void setAltitudeAccuracy(double altitudeAccuracy) {
        this.altitudeAccuracy = altitudeAccuracy;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
