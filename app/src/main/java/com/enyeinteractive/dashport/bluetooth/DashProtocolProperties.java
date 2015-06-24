package com.enyeinteractive.dashport.bluetooth;

import android.support.annotation.IntDef;

/**
 * UUID values
 * @author tcastillo
 *         Date: 6/19/15
 *         Time: 7:55 AM
 */
interface DashProtocolProperties {

    /**
     * // Service UUID
     */
    String BISCUIT_SERVICE_UUID = "713D0000-503E-4C75-BA94-3148F18D941E";
    /**
     * // First read characteristic
     */
    String READ_1_CHARACTERISTIC_UUID = "713D0001-503E-4C75-BA94-3148F18D941E";
    /**
     * // Notify characteristic
     */
    String NOTIFY_CHARACTERISTIC = "713D0002-503E-4C75-BA94-3148F18D941E";
    /**
     * // Write w/o Response Characteristic
     */
    String WRITE_WITHOUT_RESPONSE_CAHR_UUID = "713D0003-503E-4C75-BA94-3148F18D941E";

}
