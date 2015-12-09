package com.amazonaws.com.amazonaws.model;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.PriorityQueue;

/**
 * Created by sanjoyg on 9/27/15.
 */
public class Car {

    private String vin = "52-452-52-100";
    private String prndl = "DRIVE";
    private GPS gps = new GPS();
    private AirbagStatus airbagStatus = new AirbagStatus();
    private double externalTemperature = 23.0F;  // Presumably in Centigrade.

    private String currentTopicSubscribe;
    private String currentGeohash;


    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("vin").value(vin);
        writer.name("prndl").value(prndl);
        writer.name("gps");
        gps.writeJson(writer);
        writer.name("airbagStatus"); airbagStatus.writeJson(writer);
        writer.name("externalTemperature").value((double) externalTemperature);
        writer.name("timestamp").value(gps.getTimeString());
        writer.name("pin");
        writer.beginObject();
        writer.name("location");
        writer.beginObject();
        writer.name("lat").value(gps.getLatitudeDegrees());
        writer.name("lon").value(gps.getLongitudeDegrees());
        writer.endObject();
        writer.endObject();
        writer.endObject();
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
            else if (name.equals("gps")) {
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

    public GPS getGps() {
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
}
