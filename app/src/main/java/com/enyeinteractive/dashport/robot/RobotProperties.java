package com.enyeinteractive.dashport.robot;

import android.support.annotation.IntDef;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author tcastillo
 *         Date: 6/28/15
 *         Time: 3:34 PM
 */
public class RobotProperties {
    public static final int DRColorUndefined = 0;
    public static final int DRBlue=1;
    public static final int DRRedRobot=2;
    public static final int DRGreenRobot=3;
    public static final int DRYellowRobot=4;
    public static final int DRBlackRobot=5;
    public static final int DROrangeRobot=6;

    @IntDef({DRColorUndefined,
    DRBlue,
    DRRedRobot,
    DRGreenRobot,
    DRYellowRobot,
    DRBlackRobot,
    DROrangeRobot})
    @interface DRColorNames{}

    public String name;
    public short robotType;
    public short codeVersion;
    private int colorIndex;

    public static RobotProperties robotPropertiesWithData(byte[] data) {
        RobotProperties properties = new RobotProperties();

        ByteBuffer buffer = ByteBuffer.wrap(data);
        int index = 0;
        char type = (char) buffer.get();
        if (type == DashController.TYPE_NAME) {
            properties.robotType = buffer.get();
            properties.colorIndex = buffer.get();
            properties.codeVersion = buffer.get();
            //everything else is the name
            byte[] nameBytes = Arrays.copyOfRange(data, data[4],data[data.length - 1]);
            properties.name = new String(nameBytes);
        }
        return properties;
    }

    @DRColorNames
    public int getColorIndex() {
        return colorIndex;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[")
                .append("name=").append(name).append(",")
                .append("robotType=").append(robotType).append(",")
                .append("codeVersion=").append(codeVersion).append(",")
                .append("colorIndex=").append(colorIndex).append("]");
        return builder.toString();
    }

}
