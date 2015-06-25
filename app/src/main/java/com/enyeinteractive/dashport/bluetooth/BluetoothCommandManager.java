package com.enyeinteractive.dashport.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * @author tcastillo
 *         Date: 6/18/15
 *         Time: 8:05 PM
 */
public class BluetoothCommandManager {

    // //////////////////////
    // Constants
    private static final String TAG = BluetoothCommandManager.class.getSimpleName();
    public static final int READ = 0;
    public static final int WRITE = 1;
    public static final int DESC = 2;
    public static final int CONNECT = 3;
    public static final int DISCONNECT = 4;
    private CommandWrapper<?> currentCommand;
    private InternalCallback internalCallback;
    private OnReadyListener onReadyListener;

    @IntDef({READ,WRITE,DESC,CONNECT,DISCONNECT})
    @interface Type{}

    private BluetoothGatt gatt;
    // //////////////////////
    // Fields
    Queue<CommandWrapper<?>> commands = new ArrayDeque<>();
    private final BluetoothAdapter adapter;
    BluetoothDevice device;
    boolean ready;
    private CommandCallback callback;

    // //////////////////////
    // Constructors
    private BluetoothCommandManager() {
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    private static BluetoothCommandManager instance;

    public static BluetoothCommandManager getInstance() {
        if (instance == null) {
            instance = new BluetoothCommandManager();
        }
        return instance;
    }

    // //////////////////////
    // Getter & Setter

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setOnReadyListener(OnReadyListener onReadyListener) {
        this.onReadyListener = onReadyListener;
    }

    // //////////////////////
    // Methods from SuperClass/Interfaces

    // //////////////////////
    // Methods
    public void connect(BluetoothDevice device, Context context, CommandCallback callback) {
        this.device = device;
        this.callback = callback;
        internalCallback = new InternalCallback();
        this.device.connectGatt(context, true, internalCallback);
    }

    public void run(CommandWrapper<?> command) {
        commands.add(command);
        if (currentCommand == null && ready) executeNext();
    }

    public synchronized boolean executeNext() {
        /**
         * http://stackoverflow.com/questions/17910322/android-ble-api-gatt-notification-not-received
         * write desc characteristics must come
         * before read characteristic calls
         *
         */
        if (currentCommand != null || commands.isEmpty()) return false;
        currentCommand = commands.poll();
        if (!currentCommand.execute(gatt)) {
            Log.w(TAG, "execute command failed.");
            currentCommand = null;
        }
        return true;
    }

    private void onFinish() {
        //disconnect
    }

    public void disconnect() {
        if (gatt != null) {
            gatt.disconnect();
            gatt = null;
        }
    }
    // //////////////////////
    // Inner and Anonymous Classes
    public interface CommandCallback {
        void onPostExecute(BluetoothCommandManager cmd, BluetoothGatt gatt,
                                  @Type int type, Object result, long id);

        void onChange(BluetoothCommandManager command, BluetoothGattCharacteristic characteristic);
    }

    public interface OnReadyListener {
        void onReady(BluetoothCommandManager cmd);
    }

    private class InternalCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (BluetoothGatt.GATT_SUCCESS == status && BluetoothGatt.STATE_CONNECTED == newState) {
                Log.v(TAG, "connected");
                BluetoothCommandManager.this.gatt = gatt;
                BluetoothCommandManager.this.gatt.discoverServices();
            }
            if (BluetoothGatt.GATT_FAILURE == status || BluetoothGatt.STATE_DISCONNECTED == newState) {
                Log.w(TAG, String.format("failure state.  status = %s state=%s", status, newState));
                ready = false;
            }
            currentCommand = null;
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            ready = true;
            if (status != BluetoothGatt.GATT_SUCCESS) return;
            if (currentCommand != null) {
                Log.w(TAG, "onServicesDiscovered: current command not null. cmd=" + currentCommand);
            }
            if (onReadyListener != null) {
                onReadyListener.onReady(BluetoothCommandManager.this);
                onReadyListener = null;
                executeNext();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            onPostBTCommand(READ, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            onPostBTCommand(WRITE, characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            /**
             * this should be triggered if notifications are properly set up
             */
            if (callback != null) {
                callback.onChange(BluetoothCommandManager.this, characteristic);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int
                status) {
            super.onDescriptorRead(gatt, descriptor, status);
            onPostBTCommand(DESC, descriptor);
        }

        private void onPostBTCommand(@Type int type, @Nullable Object result) {
            CommandWrapper<?> cmd = currentCommand;
            currentCommand = null;
            callback.onPostExecute(BluetoothCommandManager.this, gatt, DESC, result, cmd.id);
        }
    }

    private static abstract  class CommandWrapper<T> {
        T obj;
        long id;

        /**
         *
         * @param id unique identifier for call
         */
        public CommandWrapper(long id) {
            this.id = id;
        }

        protected abstract T getObject(BluetoothGatt gatt);

        public abstract  boolean execute(BluetoothGatt gatt);
    }


    public static abstract class ReadCharacteristic extends  CommandWrapper<BluetoothGattCharacteristic> {

        public ReadCharacteristic(long id) {
            super(id);
        }

        @Override
        public boolean execute(BluetoothGatt gatt) {
            return gatt.readCharacteristic(getObject(gatt));
        }

    }
    public static abstract class ReadDescriptor extends CommandWrapper<BluetoothGattDescriptor> {
        public ReadDescriptor(long id) {
            super(id);
            //TODO: ANDROID IMPL
        }

        @Override
        public boolean execute(BluetoothGatt gatt) {
            return gatt.readDescriptor(getObject(gatt));
        }

    }
    public static abstract class WriteCharacteristic extends  CommandWrapper<BluetoothGattCharacteristic> {

        public WriteCharacteristic(long id) {
            super(id);
        }

        @Override
        public boolean execute(BluetoothGatt gatt) {
            return gatt.writeCharacteristic(getObject(gatt));
        }

    }

    public static abstract class WriteDescriptor extends  CommandWrapper<BluetoothGattDescriptor> {

        public WriteDescriptor(long id) {
            super(id);
        }

        @Override
        public boolean execute(BluetoothGatt gatt) {
            return gatt.writeDescriptor(getObject(gatt));
        }

    }
}
