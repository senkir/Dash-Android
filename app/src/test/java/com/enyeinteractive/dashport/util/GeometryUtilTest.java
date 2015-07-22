package com.enyeinteractive.dashport.util;

import android.view.MotionEvent;
import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author tcastillo
 *         Date: 7/21/15
 *         Time: 9:21 PM
 */
public class GeometryUtilTest extends TestCase {

    public void testDistance() throws Exception {
        MotionEvent.PointerCoords a = new MotionEvent.PointerCoords();
        a.x = 0;
        a.y = 0;
        MotionEvent.PointerCoords b = new MotionEvent.PointerCoords();
        b.x = 5;
        b.y = 0;
        assertThat(GeometryUtil.distance(a,b), is(equalTo(5.0)));
        a.x = 0;
        a.y = 5;
        b.x = 0;
        b.y = 0;
        assertThat(GeometryUtil.distance(a,b), is(equalTo(5.0)));
    }

    public void testAngle() throws Exception {
        MotionEvent.PointerCoords a = new MotionEvent.PointerCoords();
        a.x = 0;
        a.y = 0;
        MotionEvent.PointerCoords b = new MotionEvent.PointerCoords();
        b.x = 5;
        b.y = 0;
        assertThat(GeometryUtil.angle(a,b), is(equalTo(90.0)));
        a.x = 0;
        a.y = 5;
        b.x = 0;
        b.y = 0;
        assertThat(GeometryUtil.angle(a, b), is(equalTo(0.0)));
        a.x = 0;
        a.y = -5;
        b.x = 0;
        b.y = 0;
        assertThat(GeometryUtil.angle(a, b), is(equalTo(180.0)));
        a.x = 0;
        a.y = 0;
        b.x = -5;
        b.y = 0;
        assertThat(GeometryUtil.angle(a, b), is(equalTo(270.0)));

    }
}