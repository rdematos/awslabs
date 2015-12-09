package com.amazonaws.com.amazonaws.model;

/**
 * Created by sanjoyg on 9/29/15.
 */
public enum Quadrant {

    TopLeft,
    TopRight,
    BottomRight,
    BottomLeft;

    public static Quadrant getMovementQuadrant(double lastLat, double lastLong, double newLat, double newLong) {
        if (lastLong < 0) {
            lastLong = 360.00D - lastLong;
        }
        if (newLong < 0) {
            newLong = 360.00D - newLong;
        }

        boolean upwards = newLat >= lastLat;
        boolean westwards = newLong >= lastLong;
        if (upwards) {
            if (westwards) {
                return TopRight;
            }
            else {
                return TopLeft;
            }
        }
        else {
            if (westwards) {
                return BottomRight;
            }
            else {
                return BottomLeft;
            }
        }
    }
}
