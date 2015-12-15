package com.amazonaws.com.amazonaws.model;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;

/**
 * Created by sanjoyg on 9/27/15.
 */
public class AirbagStatus {

    private String driverCurtainAirbagDeployed = "NO_EVENT";
    private String passengerKneeAirbagDeployed =  "NO_EVENT";
    private String driverKneeAirbagDeployed = "NO_EVENT";
    private String passengerSideAirbagDeployed = "NO_EVENT";
    private String passengerAirbagDeployed = "NO_EVENT";
    private String driverAirbagDeployed = "NO_EVENT";
    private String passengerCurtainAirbagDeployed = "NO_EVENT";
    private String driverSideAirbagDeployed = "NO_EVENT";

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("driverCurtainAirbagDeployed").value(driverCurtainAirbagDeployed);
        writer.name("passengerKneeAirbagDeployed").value(passengerKneeAirbagDeployed);
        writer.name("driverKneeAirbagDeployed").value(driverKneeAirbagDeployed);
        writer.name("passengerSideAirbagDeployed").value(passengerSideAirbagDeployed);
        writer.name("passengerAirbagDeployed").value(passengerAirbagDeployed);
        writer.name("driverAirbagDeployed").value(driverAirbagDeployed);
        writer.name("passengerCurtainAirbagDeployed").value(passengerCurtainAirbagDeployed);
        writer.name("driverSideAirbagDeployed").value(driverSideAirbagDeployed);
        writer.endObject();
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("driverCurtainAirbagDeployed")) {
                driverCurtainAirbagDeployed = reader.nextString();
            }
            else if (name.equals("passengerKneeAirbagDeployed")) {
                passengerKneeAirbagDeployed = reader.nextString();
            }
            else if (name.equals("driverKneeAirbagDeployed")) {
                driverKneeAirbagDeployed = reader.nextString();
            }
            else if (name.equals("passengerSideAirbagDeployed")) {
                passengerSideAirbagDeployed = reader.nextString();
            }
            else if (name.equals("passengerAirbagDeployed")) {
                passengerAirbagDeployed = reader.nextString();
            }
            else if (name.equals("driverAirbagDeployed")) {
                driverAirbagDeployed = reader.nextString();
            }
            else if (name.equals("passengerCurtainAirbagDeployed")) {
                passengerCurtainAirbagDeployed = reader.nextString();
            }
            else if (name.equals("driverSideAirbagDeployed")) {
                driverSideAirbagDeployed = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    public String getDriverAirbagDeployed() {
        return driverAirbagDeployed;
    }

    public void setDriverAirbagDeployed(String driverAirbagDeployed) {
        this.driverAirbagDeployed = driverAirbagDeployed;
    }

    public String getDriverCurtainAirbagDeployed() {
        return driverCurtainAirbagDeployed;
    }

    public void setDriverCurtainAirbagDeployed(String driverCurtainAirbagDeployed) {
        this.driverCurtainAirbagDeployed = driverCurtainAirbagDeployed;
    }

    public String getDriverKneeAirbagDeployed() {
        return driverKneeAirbagDeployed;
    }

    public void setDriverKneeAirbagDeployed(String driverKneeAirbagDeployed) {
        this.driverKneeAirbagDeployed = driverKneeAirbagDeployed;
    }

    public String getDriverSideAirbagDeployed() {
        return driverSideAirbagDeployed;
    }

    public void setDriverSideAirbagDeployed(String driverSideAirbagDeployed) {
        this.driverSideAirbagDeployed = driverSideAirbagDeployed;
    }

    public String getPassengerAirbagDeployed() {
        return passengerAirbagDeployed;
    }

    public void setPassengerAirbagDeployed(String passengerAirbagDeployed) {
        this.passengerAirbagDeployed = passengerAirbagDeployed;
    }

    public String getPassengerCurtainAirbagDeployed() {
        return passengerCurtainAirbagDeployed;
    }

    public void setPassengerCurtainAirbagDeployed(String passengerCurtainAirbagDeployed) {
        this.passengerCurtainAirbagDeployed = passengerCurtainAirbagDeployed;
    }

    public String getPassengerKneeAirbagDeployed() {
        return passengerKneeAirbagDeployed;
    }

    public void setPassengerKneeAirbagDeployed(String passengerKneeAirbagDeployed) {
        this.passengerKneeAirbagDeployed = passengerKneeAirbagDeployed;
    }

    public String getPassengerSideAirbagDeployed() {
        return passengerSideAirbagDeployed;
    }

    public void setPassengerSideAirbagDeployed(String passengerSideAirbagDeployed) {
        this.passengerSideAirbagDeployed = passengerSideAirbagDeployed;
    }
}
