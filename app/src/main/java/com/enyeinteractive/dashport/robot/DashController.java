package com.enyeinteractive.dashport.robot;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.util.Log;
import com.enyeinteractive.dashport.bluetooth.BluetoothCommandManager;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author tcastillo
 *         Date: 6/18/15
 *         Time: 5:28 PM
 */
public class DashController implements BluetoothCommandManager.CommandCallback {

    // //////////////////////
    // Constants

    private static final String TAG = DashController.class.getSimpleName();

    /**
     *
     typedef NS_ENUM(char, DRMessageTypes) {
     DRMessageTypeName = '1',
     DRMessageTypeSignals = '2',
     DRMessageTypeAutoRunComplete = '3'
     };

     typedef NS_ENUM(char, DRCommandTypes) {
     DRCommandTypeAllStop = '0',
     DRCommandTypeSetName = '1',
     DRCommandTypeDirectDrive = '2',
     DRCommandTypeGyroDrive = '3',
     DRCommandTypeSetEyes = '4',
     DRCommandTypeRequestSignals = '6',
     DRCommandTypeAutoMode = '7',
     };

     */
    public static final char TYPE_NAME = '1';
    public static final char TYPE_SIGNALS = '2';
    public static final char TYPE_RUN_AUTOCOMPLETE = '3';
    public static final int CAPACITY = 14;

    @IntDef({TYPE_NAME, TYPE_SIGNALS, TYPE_RUN_AUTOCOMPLETE})
    @interface MessageTypes{}

    public static final char TYPE_ALL_STOP = '0';
    public static final char TYPE_SET_NAME = '1';
    public static final char TYPE_DIRECT_DRIVE = '2';
    public static final char TYPE_GYRO_DRIVE = '3';
    public static final char TYPE_SET_EYES = '4';
    public static final char TYPE_REQUEST_SIGNALS = '6';
    public static final char TYPE_AUTO_MODE = '7';

    @IntDef({TYPE_ALL_STOP,
    TYPE_SET_NAME,
    TYPE_DIRECT_DRIVE,
    TYPE_GYRO_DRIVE,
    TYPE_SET_EYES,
    TYPE_REQUEST_SIGNALS,
    TYPE_AUTO_MODE})
    public @interface CommandType{}
    /**
     * Color properties
     *
     [UIColor colorWithRed:0.191 green:0.287 blue:0.611 alpha:1.000], \
     [UIColor colorWithRed:0.905 green:0.150 blue:0.119 alpha:1.000], \
     [UIColor colorWithRed:0.149 green:0.591 blue:0.279 alpha:1.000], \
     [UIColor colorWithRed:0.929 green:0.799 blue:0.145 alpha:1.000], \
     [UIColor colorWithRed:0.136 green:0.147 blue:0.157 alpha:1.000], \
     [UIColor colorWithRed:0.952 green:0.501 blue:0.115 alpha:1.000], \
     */

    // //////////////////////
    // Fields

    private Context context;

    private BluetoothGattService roboService;
    private final UUID biscuitUUID;
    private final UUID writeWithoutNotifyUUID;
    private final UUID notifyServicesUUID;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeWithoutResponseCharacteristic;
    private int leftThrottle;
    private int rightThrottle;
    private RobotProperties properties;
    private final BluetoothDevice device;
    private WeakReference<StatusChangeListener> listener;

    // //////////////////////
    // Constructors

    public DashController(BluetoothDevice device, Context context
    , final StatusChangeListener listener) {
        this.device = device;
        this.listener = new WeakReference<>(listener);
        this.context = context; //FIXME: this will leak so need to fix this later
        biscuitUUID = UUID.fromString(DashProtocolProperties.BISCUIT_SERVICE_UUID);
        writeWithoutNotifyUUID = UUID.fromString(DashProtocolProperties
                .WRITE_WITHOUT_RESPONSE_CAHR_UUID);
        notifyServicesUUID = UUID.fromString(DashProtocolProperties.NOTIFY_CHARACTERISTIC);
        BluetoothCommandManager.getInstance().setConnectionStateChangeListener(new BluetoothCommandManager.OnConnectionStateChangeListener() {


            @Override
            public int onStateChange(@BluetoothCommandManager.ConnectionState int state) {
                listener.onStatusChange(state);
                return state;
            }
        });
        BluetoothCommandManager.getInstance().setOnReadyListener(new BluetoothCommandManager.OnReadyListener() {
            @Override
            public void onReady(BluetoothCommandManager cmd) {
                //grab services we care about
                BluetoothGatt gatt = cmd.getGatt();
                //iterate through services
                roboService = gatt.getService(biscuitUUID);
                //process
                writeWithoutResponseCharacteristic = roboService.getCharacteristic(writeWithoutNotifyUUID);
                if (writeWithoutResponseCharacteristic == null) return;
                //data packet size is 14 bytes
                requestSignalNotifications(false);
                //setup notify events
                notifyCharacteristic = roboService.getCharacteristic(notifyServicesUUID);
                if (notifyCharacteristic == null) return;
                gatt.setCharacteristicNotification(notifyCharacteristic, true);
//                BluetoothCommandManager.getInstance().run(new BluetoothCommandManager.WriteDescriptor(200) {
//
//                    @Override
//                    protected BluetoothGattDescriptor getObject(BluetoothGatt gatt) {
//                        //enable notifications
//                        gatt.setCharacteristicNotification(notifyCharacteristic, true);
//                        BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptors().get(0);
//                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                        return descriptor;
//                    }
//                });
            }
        });
        connect();
    }


    public void connect() {
        BluetoothCommandManager.getInstance().connect(device, context, this);
    }

    private void requestSignalNotifications(boolean enable) {
        /**
         * request signal no notifications
         */
        ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
        buffer.put((byte) TYPE_REQUEST_SIGNALS);
        buffer.put(enable? (byte)1:0);
        sendData(buffer.array());

    }
    // //////////////////////
    // Getter & Setter

    // //////////////////////
    // Methods from SuperClass/Interfaces

    @Override
    public void onPostExecute(BluetoothCommandManager cmd, BluetoothGatt gatt,
                              @BluetoothCommandManager.CommandType int type, Object result, long id) {

        Log.v(TAG, "onPostExecute returned with object " + result);
        if (result instanceof BluetoothGattCharacteristic) {
            BluetoothGattCharacteristic c = (BluetoothGattCharacteristic) result;
            Log.v(TAG, "VALUE=" + Arrays.toString(c.getValue()));
        }
    }

    @Override
    public void onChange(BluetoothCommandManager command, BluetoothGattCharacteristic
            characteristic) {
//        Log.v(TAG, "onChange:" + characteristic.getUuid() + "value" + Arrays.toString
//                (characteristic.getValue()));
        byte[] data = characteristic.getValue();
        char type = (char) data[0];
        switch (type) {
            case TYPE_NAME:
                properties = RobotProperties.robotPropertiesWithData(data);
                if (listener != null && listener.get() != null) {
                    listener.get().receivedNotifyWithProperties(properties);
                    requestSignalNotifications(true);
                }
                break;
            case TYPE_SIGNALS:
                DRSignalPacket signals = DRSignalPacket.fromData(data);
                if (listener != null && listener.get() != null) {
                    listener.get().receivedNotifyWithSignals(signals);
                }
                break;
            case TYPE_RUN_AUTOCOMPLETE:
                if (listener != null && listener.get() != null) {
                    listener.get().receivedNotifyWithData(data);
                }
                break;
            default:
                //no-op
            Log.e(TAG, "onChange with unknown type. data=" +Arrays.toString
                    (characteristic.getValue()));
        }
    }
// //////////////////////
    // Methods

    public void setEyeColor(int color) {
        /**
         * turn bytes red green blue into a data packet
         */
        setEyeColor(Color.red(color),
                Color.green(color),
                Color.blue(color));
    }

    private byte intToByte(int raw) {
        return (byte) raw;
    }

    private void setEyeColor(int r, int g, int b) {
        /**
         *
         DRColorUndefined = 0,
         [UIColor lightGrayColor], \
         DRBlue,
         [UIColor colorWithRed:0.191 green:0.287 blue:0.611 alpha:1.000], \
         DRRedRobot,
         [UIColor colorWithRed:0.905 green:0.150 blue:0.119 alpha:1.000], \
         DRGreenRobot,
         [UIColor colorWithRed:0.149 green:0.591 blue:0.279 alpha:1.000], \
         DRYellowRobot,
         [UIColor colorWithRed:0.929 green:0.799 blue:0.145 alpha:1.000], \
         DRBlackRobot,
         [UIColor colorWithRed:0.136 green:0.147 blue:0.157 alpha:1.000], \
         DROrangeRobot,
         [UIColor colorWithRed:0.952 green:0.501 blue:0.115 alpha:1.000], \

         */
        ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
        buffer.put((byte) TYPE_SET_EYES);
        buffer.put((byte) r);
        buffer.put((byte) g);
        buffer.put((byte) b);
        sendData(buffer.array());
    }

    static int identify = 0;

    private void sendData(final byte[] data) {
        Log.v(TAG, "sendData " + Arrays.toString(data));
        BluetoothCommandManager.getInstance().run(new BluetoothCommandManager.WriteCharacteristic
                (identify++) {

            @Override
            protected BluetoothGattCharacteristic getObject(BluetoothGatt gatt) {
                writeWithoutResponseCharacteristic.setValue(data);
                return writeWithoutResponseCharacteristic;
            }
        });
//        BluetoothCommandManager.getInstance().run(new BluetoothCommandManager.ReadCharacteristic(identify++) {
//            @Override
//            protected BluetoothGattCharacteristic getObject(BluetoothGatt gatt) {
//                return writeWithoutResponseCharacteristic;
//            }
//        });
    }

    public boolean callNotifier() {
        //extra setup
        return true;
    }

    public void reset() {
        ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
        buffer.putChar(TYPE_ALL_STOP);
        buffer.putInt(0);
        sendData(buffer.array());
    }

    public void setThrottleLeft(int throttle) {
        leftThrottle = throttle;
        sendMotor(leftThrottle,rightThrottle);
    }

    public void setThrottleRight(int throttle) {
        rightThrottle = throttle;
        sendMotor(leftThrottle,rightThrottle);
    }

    private float sanitze(float value, float min, float max) {
        value = Math.max(min, value);
        value = Math.min(max, value);
        return value;
    }

    private void sendMotor(float leftMotor, float rightMotor) {
        leftMotor = sanitze(leftMotor, -255, 255);
        rightMotor = sanitze(rightMotor, -255,255);

        final int mtrA1, mtrA2, mtrB1, mtrB2;

        if (leftMotor >= 0) {
            mtrA1 = Math.round(leftMotor);
            mtrA2 = 0;
        } else {
            mtrA1 = 0;
            mtrA2 = Math.round(-leftMotor);
        }

        if (rightMotor >= 0) {
            mtrB1 = Math.round(rightMotor);
            mtrB2 = 0;
        } else {
            mtrB1 = 0;
            mtrB2 = Math.round(-rightMotor);
        }

        /**
         * bitwise operation to join
         *     #define CLAMP(x, low, high)  (((x) > (high)) ? (high) : (((x) < (low)) ? (low) : (x)))
         *         // [type "2" -1]  [mtrA1 - 0-255 - 1] [mtrA2 - 0-255 - 1] [mtrB1 - 0-255 - 1] [mtrB2 - 0-255 - 1]


         */
        ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
        buffer.put((byte) TYPE_DIRECT_DRIVE);
        buffer.put((byte) mtrA1);
        buffer.put((byte) mtrA2);
        buffer.put((byte) mtrB1);
        buffer.put((byte) mtrB2);
        sendData(buffer.array());
    }


    public void close() {
        BluetoothCommandManager.getInstance().disconnect();
    }
    // //////////////////////
    // Inner and Anonymous Classes
    public interface StatusChangeListener {
        void onStatusChange(@BluetoothCommandManager.ConnectionState int state);
        void receivedNotifyWithSignals(DRSignalPacket signals);
        void receivedNotifyWithProperties(RobotProperties properties);
        void receivedNotifyWithData(byte[] data);
    }
}
