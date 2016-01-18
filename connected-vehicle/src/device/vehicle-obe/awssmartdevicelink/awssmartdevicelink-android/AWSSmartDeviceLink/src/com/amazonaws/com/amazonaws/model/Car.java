package com.amazonaws.com.amazonaws.model;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;

/**
 * Created by sanjoyg on 9/27/15.
 */
public class Car {
    private static final String TAG = "Car";

    private String vin = "52-452-52-100";
    private String prndl = "DRIVE";
    private GPS gps = null;
    private AirbagStatus airbagStatus =null;
    private double externalTemperature = 23.0F;  // Presumably in Centigrade.
    private String brakingStatus = null;
    private String currentTopicSubscribe;
    private String currentGeohash;

    public Car(Context context){
        if(context == null){
            Log.i(TAG, "Conext was null, setting VIN to default");
            return;
        }
       /* WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        this.vin = macAddress;
        */
        //This vin will hopefully come from the cloud. Even using the mac address on for the wifi adapter was the wrong approach
        // as it keeps track of a physical device, but while it seemed ok for a demo, Android 6.0+ only returns a default mac address that
        // will be the same on every device running that or newer OS.
        this.vin  =  ClientId.getUID();


        Log.i(TAG, "New car created with vin: " + this.vin);
    }

    public void writeJson(JsonWriter writer) throws IOException {
       if(gps == null){
           Log.e(TAG, "No stored GPS, bailing out");
           return;
       }
        writer.beginObject(); //1 START
        if(vin !=null){
            writer.name("vin").value(vin);
        }

        if(prndl != null){
           writer.name("prndl").value(prndl);
        }

        if(brakingStatus != null){
            writer.name("driverBraking").value(brakingStatus);
        }

        if(gps != null) {
            writer.name("gps");
            gps.writeJson(writer);
        }

        if(airbagStatus !=null) {
            writer.name("airbagStatus");
            airbagStatus.writeJson(writer);
        }

        writer.name("externalTemperature").value((double) externalTemperature);

        writer.endObject(); // 1 END
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("vin")) {
                vin = reader.nextString();
            }
            else if (name.equals("prndl")) {
                prndl = reader.nextString();
            }
            else if (name.equals("driverBraking")) {
                brakingStatus = reader.nextString();
            }
            else if (name.equals("gps")) {
                if(gps == null){
                    gps = new GPS();
                }
                gps.readJson(reader);
            }
            else if (name.equals("airbagStatus")) {
                airbagStatus.readJson(reader);
            }
            else if (name.equals("externalTemperature")) {
                externalTemperature = reader.nextDouble();
            }
            else {
                reader.skipValue();
                Log.e(TAG, "Uknown value in JSON - " + name);
            }
        }
        reader.endObject();
    }

    public AirbagStatus getAirbagStatus() {
        return airbagStatus;
    }

    public void setAirbagStatus(AirbagStatus airbagStatus) {
        this.airbagStatus = airbagStatus;
    }

    public double getExternalTemperature() {
        return externalTemperature;
    }

    public void setExternalTemperature(double externalTemperature) {
        this.externalTemperature = externalTemperature;
    }

    public GPS getGps(boolean autoCreate) {
        if(gps == null && autoCreate){
            gps = new GPS();
        }
        return gps;
    }

    public void setGps(GPS gps) {
        this.gps = gps;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPrndl() {
        return prndl;
    }

    public void setPrndl(String prndl) {
        this.prndl = prndl  ;
    }

    public void setCurrentGeohash(String currentGeohash) {
        this.currentGeohash = currentGeohash;
    }

    public String getCurrentGeohash() {
        return currentGeohash;
    }

    public String getCurrentTopicSubscribe() {
        return currentTopicSubscribe;
    }

    public void setCurrentTopicSubscribe(String currentTopicSubscribe) {
        this.currentTopicSubscribe = currentTopicSubscribe;
    }

    public void setBraking(String brakingStatus){this.brakingStatus = brakingStatus;}
    public String getBraking(){return brakingStatus;}
}
