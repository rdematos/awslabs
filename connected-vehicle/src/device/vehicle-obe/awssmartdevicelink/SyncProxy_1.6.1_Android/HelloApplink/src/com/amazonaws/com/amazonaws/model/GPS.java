package com.amazonaws.com.amazonaws.model;

import android.util.JsonReader;
import android.util.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sanjoyg on 9/27/15.
 */
public class GPS {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private double speed;
    private double latitudeDegrees;
    private double longitudeDegrees;
    private int heading = 173;
    private int utcDay;
    private int utcMonth;
    private int utcYear;
    private int utcHours;
    private int utcMinutes;
    private int utcSeconds;

    public GPS() {
        DateTime dt = new DateTime(DateTimeZone.UTC);
        utcDay = dt.getDayOfMonth();
        utcMonth = dt.getMonthOfYear();
        utcYear = dt.getYear();
        utcHours = dt.getHourOfDay();
        utcMinutes = dt.getMinuteOfHour();
        utcSeconds = dt.getSecondOfMinute();
    }

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("speed").value(speed);
        writer.name("latitudeDegrees").value(latitudeDegrees);
        writer.name("longitudeDegrees").value(longitudeDegrees);
        writer.name("heading").value(heading);
        writer.name("utcDay").value(utcDay);
        writer.name("utcMonth").value(utcMonth);
        writer.name("utcYear").value(utcYear);
        writer.name("utcHours").value(utcHours);
        writer.name("utcMinutes").value(utcMinutes);
        writer.name("utcSeconds").value(utcSeconds);
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
            }
            else if (name.equals("utcDay")) {
                utcDay = reader.nextInt();
            }
            else if (name.equals("utcMonth")) {
                utcMonth = reader.nextInt();
            }
            else if (name.equals("utcYear")) {
                utcYear = reader.nextInt();
            }
            else if (name.equals("utcHours")) {
                utcHours = reader.nextInt();
            }
            else if (name.equals("utcMinutes")) {
                utcMinutes = reader.nextInt();
            }
            else if (name.equals("utcSeconds")) {
                utcSeconds = reader.nextInt();
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

    public int getUtcDay() {
        return utcDay;
    }

    public void setUtcDay(int utcDay) {
        this.utcDay = utcDay;
    }

    public int getUtcHours() {
        return utcHours;
    }

    public void setUtcHours(int utcHours) {
        this.utcHours = utcHours;
    }

    public int getUtcMinutes() {
        return utcMinutes;
    }

    public void setUtcMinutes(int utcMinutes) {
        this.utcMinutes = utcMinutes;
    }

    public int getUtcMonth() {
        return utcMonth;
    }

    public void setUtcMonth(int utcMonth) {
        this.utcMonth = utcMonth;
    }

    public int getUtcSeconds() {
        return utcSeconds;
    }

    public void setUtcSeconds(int utcSeconds) {
        this.utcSeconds = utcSeconds;
    }

    public int getUtcYear() {
        return utcYear;
    }

    public void setUtcYear(int utcYear) {
        this.utcYear = utcYear;
    }

    public String getTimeString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // month is offset
       // calendar.set(utcYear, utcMonth-1, utcDay, utcHours, utcMinutes, utcSeconds);
        String timeString = dateFormat.format(calendar.getTime());
        return timeString;
    }
}
