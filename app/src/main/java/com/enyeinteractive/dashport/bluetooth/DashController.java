package com.enyeinteractive.dashport.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;

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
    public static final int ID_NOTIFY = 100;

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
    public static final int TYPE_NAME = 1;
    public static final int TYPE_SIGNALS = 2;
    public static final int TYPE_RUN_AUTOCOMPLETE = 3;
    private BluetoothGattService roboService;
    private final UUID biscuitUUID;
    private final UUID writeWithoutNotifyUUID;
    private final UUID notifyServicesUUID;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeWithoutResponseCharacteristic;

    @IntDef({TYPE_NAME, TYPE_SIGNALS, TYPE_RUN_AUTOCOMPLETE})
    @interface MessageTypes{}


    public static final byte TYPE_ALL_STOP = 0x0;
    public static final byte TYPE_SET_NAME = 0x1;
    public static final byte TYPE_DIRECT_DRIVE = 0x2;
    public static final byte TYPE_GYRO_DRIVE = 0x3;
    public static final byte TYPE_SET_EYES = 0x4;
    public static final byte TYPE_REQUEST_SIGNALS = 0x6;
    public static final byte TYPE_AUTO_MODE = 7;

    @IntDef({TYPE_ALL_STOP,
    TYPE_SET_NAME,
    TYPE_DIRECT_DRIVE,
    TYPE_GYRO_DRIVE,
    TYPE_SET_EYES,
    TYPE_REQUEST_SIGNALS,
    TYPE_AUTO_MODE})
    @interface CommandType{}
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


    // //////////////////////
    // Constructors

    public DashController(BluetoothDevice device, Context context) {
        this.context = context; //FIXME: this will leak so need to fix this later
        biscuitUUID = UUID.fromString(DashProtocolProperties.BISCUIT_SERVICE_UUID);
        writeWithoutNotifyUUID = UUID.fromString(DashProtocolProperties
                .WRITE_WITHOUT_RESPONSE_CAHR_UUID);
        notifyServicesUUID = UUID.fromString(DashProtocolProperties.READ_1_CHARACTERISTIC_UUID);
        BluetoothCommandManager.getInstance().connect(device, context, this);
        BluetoothCommandManager.getInstance().setOnReadyListener(new BluetoothCommandManager.OnReadyListener() {
            @Override
            public void onReady(BluetoothCommandManager cmd) {
                //grab services we care about
                BluetoothGatt gatt = cmd.getGatt();
                //iterate through services
                for (BluetoothGattService service : gatt.getServices()) {
                    if (service.getUuid() == biscuitUUID) {
                        //process
                        roboService = service;
                        writeWithoutResponseCharacteristic = service
                                .getCharacteristic(writeWithoutNotifyUUID);
                        BluetoothCommandManager.getInstance().run(new BluetoothCommandManager
                                .WriteCharacteristic(100) {


                            @Override
                            protected BluetoothGattCharacteristic getObject(BluetoothGatt gatt) {
                                if (writeWithoutResponseCharacteristic != null) {
                                    //data packet size is 14 bytes
                                    /**
                                     * request signal no notifications
                                     */
                                    byte command = TYPE_REQUEST_SIGNALS;
                                    byte[] data = new byte[16];
                                    data[15] = (byte)(data[15] & command);
                                    writeWithoutResponseCharacteristic.setValue(data);
                                }
                                return writeWithoutResponseCharacteristic;
                            }
                        });
                    }
                    notifyCharacteristic = service
                            .getCharacteristic(notifyServicesUUID);
                    BluetoothCommandManager.getInstance().run(new BluetoothCommandManager.WriteDescriptor(200) {

                        @Override
                        protected BluetoothGattDescriptor getObject(BluetoothGatt gatt) {
                            //enable notifications
                            gatt.setCharacteristicNotification(notifyCharacteristic, true);
                            BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptors().get(0);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            return descriptor;
                        }
                    });
                }


            }
        });
    }
    // //////////////////////
    // Getter & Setter

    // //////////////////////
    // Methods from SuperClass/Interfaces

    @Override
    public void onPostExecute(BluetoothCommandManager cmd, BluetoothGatt gatt, @BluetoothCommandManager.Type int type, Object result, long id) {

        if (id == ID_NOTIFY) {
            Log.v(TAG, "onPostExecute returned with object " + result);
            if (result instanceof BluetoothGattCharacteristic) {
                BluetoothGattCharacteristic c = (BluetoothGattCharacteristic) result;
                Log.v(TAG, "VALUE=" + Arrays.toString(c.getValue()));
            }
        } else {
            //NoOp
        }
    }

    // //////////////////////
    // Methods

    public boolean setEyeColor(int color) {
        int command = TYPE_SET_EYES;
        /**
         * turn bytes red green blue into a data packet
         */
        return false;
    }

    public boolean callNotifier() {
        final UUID serviceUUID = UUID.fromString(DashProtocolProperties.BISCUIT_SERVICE_UUID);
        final UUID characteristic = UUID.fromString(DashProtocolProperties.NOTIFY_CHARACTERISTIC);

        BluetoothGatt gatt = BluetoothCommandManager.getInstance().getGatt();
        if (gatt == null) {
            Log.e(TAG, "callNotifier: bluetooth not ready.");
            BluetoothCommandManager.getInstance().setOnReadyListener(new BluetoothCommandManager.OnReadyListener() {

                @Override
                public void onReady(BluetoothCommandManager cmd) {
                    BluetoothGatt gatt = cmd.getGatt();
                    if (gatt == null) return;
                    BluetoothGattCharacteristic c = gatt.getService(serviceUUID).getCharacteristic(characteristic);
                    gatt.setCharacteristicNotification(c,true);
                    //write descriptor characteristic
                    BluetoothCommandManager.getInstance().run(new BluetoothCommandManager.ReadCharacteristic(c));
                }
            });
            return false;
        }
        BluetoothGattCharacteristic c = gatt.getService(serviceUUID).getCharacteristic(characteristic);
        BluetoothCommandManager.getInstance().run(new BluetoothCommandManager.WriteCharacteristic(c));

        BluetoothCommandManager.getInstance().run(new BluetoothCommandManager.ReadCharacteristic(c));
        return true;
    }


    public void close() {
        BluetoothCommandManager.getInstance().disconnect();

    }
    // //////////////////////
    // Inner and Anonymous Classes

}
