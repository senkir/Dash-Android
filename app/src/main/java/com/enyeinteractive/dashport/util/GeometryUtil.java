package com.enyeinteractive.dashport.util;

import android.view.MotionEvent;

/**
 * @author tcastillo
 *         Date: 6/27/15
 *         Time: 12:09 PM
 */
public abstract class GeometryUtil {

    public static double distance(MotionEvent.PointerCoords origin, MotionEvent.PointerCoords location) {
        return Math.sqrt((origin.x-location.x)*(origin.x-location.x) +
                (origin.y-location.y)*(origin.y-location.y));
    }

    /**
     * Determine angle of points
     * @param origin
     * @param location
     * @return angle in radians
     */
    public static double angle(MotionEvent.PointerCoords origin, MotionEvent.PointerCoords location) {
        float angle = (float) Math.toDegrees(Math.atan2(location.y - origin.y, location.x - origin.x));
//        return Math.atan2(location.y - origin.y, location.x - origin.x) + Math.PI;
        angle += 90.0;
        return angle;
    }
}
