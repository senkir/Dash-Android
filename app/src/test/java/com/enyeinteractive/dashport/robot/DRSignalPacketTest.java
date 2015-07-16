package com.enyeinteractive.dashport.robot;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author tcastillo
 *         Date: 6/30/15
 *         Time: 9:21 AM
 */
public class DRSignalPacketTest extends TestCase {

    public void testFromData() throws Exception {
        byte[] data = new byte[14];
        data[0] = (byte)52;
        data[1] = (byte)100;
        data[2] = (byte)100;
        data[3] = (byte)100;

        DRSignalPacket packet = DRSignalPacket.fromData(data);
        org.junit.Assert.assertThat(packet.command, is(equalTo('4')));
    }
}