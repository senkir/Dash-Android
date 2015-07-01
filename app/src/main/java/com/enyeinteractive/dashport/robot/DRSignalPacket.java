package com.enyeinteractive.dashport.robot;

import com.enyeinteractive.dashport.util.IntUtil;

import java.nio.ByteBuffer;

/**
 * @author tcastillo
 *         Date: 6/28/15
 *         Time: 3:33 PM
 */
public class DRSignalPacket {
    @DashController.CommandType
    public char command;
    public int yaw;
    public int ambientLight;
    public int proximityLeft;
    public int proximityRight;
    public int leftMotor;
    public int rightMotor;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[")
                .append("yaw=").append(yaw).append(",")
                .append("ambientLight=").append(ambientLight).append(",")
                .append("proxL=").append(proximityLeft).append(",")
                .append("proxR=").append(proximityRight).append(",")
                .append("motorL=").append(leftMotor).append(",")
                .append("motorR=").append(rightMotor).append("]");
        return builder.toString();
    }

    public static DRSignalPacket fromData(byte[] bytes) {

        // [type - "2" - 1] [ mode - 0-10 - 1] [ yaw - 0-1024 - 2] [ambient light - 0-1024 - 2] [proxLeft - 0-1024 - 2] [proxRight - 0-1024 - 2] [mtrA1 - 0-255 - 1] [mtrA2 - 0-255 - 1] [mtrB1 - 0-255 - 1] [mtrB2 - 0-255 - 1]

        DRSignalPacket signals = new DRSignalPacket();

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        char mode;
        //16 bit
        short yaw, light, proxLeft, proxRight;
        //8 bit
        int mtrA1, mtrA2, mtrB1, mtrB2;
        mode = (char) buffer.get();
        yaw = buffer.getShort();
        light = buffer.getShort();
        proxLeft = buffer.getShort();
        proxRight = buffer.getShort();
        mtrA1 = buffer.get();
        mtrA2 = buffer.get();
        mtrB1 = buffer.get();
        mtrB2 = buffer.get();

        //noinspection ResourceType
        signals.command = mode;
        signals.yaw = yaw;
        signals.ambientLight = light;
        signals.proximityLeft = proxLeft;
        signals.proximityRight = proxRight;
        signals.leftMotor = mtrA1 - mtrA2;
        signals.rightMotor = mtrB1 - mtrB2;

        return signals;
    }
}
