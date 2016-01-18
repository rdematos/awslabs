package com.amazonaws.com.amazonaws.model;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;

/**
 * Created by sanjoyg on 9/27/15.
 */
public class GPS {

    private double speed = Double.MAX_VALUE;
    private double latitudeDegrees = Double.MAX_VALUE;
    private double longitudeDegrees = Double.MAX_VALUE;
    private int heading = 173;


    public GPS() {

    }

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        if(speed != Double.MAX_VALUE){
            writer.name("speed").value(speed);
        }
        if(latitudeDegrees != Double.MAX_VALUE && longitudeDegrees != Double.MAX_VALUE) {
            writer.name("latitudeDegrees").value(latitudeDegrees);
            writer.name("longitudeDegrees").value(longitudeDegrees);
        }
        writer.name("heading").value(heading);
        writer.endObject();
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("speed")) {
                speed = reader.nextDouble();
            }
            else if (name.equals("latitudeDegrees")) {
                latitudeDegrees = reader.nextDouble();
            }
            else if (name.equals("longitudeDegrees")) {
                longitudeDegrees = reader.nextDouble();
            }
            else if (name.equals("heading")) {
                heading = reader.nextInt();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public double getLatitudeDegrees() {
        return latitudeDegrees;
    }

    public void setLatitudeDegrees(double latitudeDegrees) {
        this.latitudeDegrees = latitudeDegrees;
    }

    public double getLongitudeDegrees() {
        return longitudeDegrees;
    }

    public void setLongitudeDegrees(double longitudeDegrees) {
        this.longitudeDegrees = longitudeDegrees;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
