package com.enyeinteractive.dashport.util;

/**
 * @author tcastillo
 *         Date: 6/27/15
 *         Time: 12:09 PM
 */
public class IntUtil {
    public static int toInt(byte[] bytes, int start, int end) {
        int i = 0;
        int out = 0;
        for (int j = start; j < end; j++) {
            out = out | (bytes[j] & 0xff) << i;
            i += 8;
        }
        return out;
    }
}
