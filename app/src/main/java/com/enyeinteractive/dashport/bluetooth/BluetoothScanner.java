package com.enyeinteractive.dashport.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import java.util.List;

/**
 * @author tcastillo
 *         Date: 6/18/15
 *         Time: 8:57 AM
 */
public class BluetoothScanner {

    // //////////////////////
    // Constants

    public static final boolean DEBUG = true;
    private static final String TAG = BluetoothScanner.class.getSimpleName();
    // //////////////////////
    // Fields

    private boolean scanning;
    private ScanFinishListener listener;

    private ArrayMap<BluetoothDevice, Integer> devices = new ArrayMap<>();
    private Handler handler;
    private Runnable finish;
    // //////////////////////
    // Constructors

    // //////////////////////
    // Getter & Setter
    @NonNull
    public ArrayMap<BluetoothDevice, Integer> getDevices() {
        return devices;
    }


    // //////////////////////
    // Methods from SuperClass/Interfaces

    // //////////////////////
    // Methods

    public void scanLE(@NonNull final ScanFinishListener listener) {
        BluetoothLeScanner leScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        final DashScanCallback callback = new DashScanCallback();
        leScanner.startScan(callback);
        handler = new Handler();
        this.listener = listener;
        finish = new Runnable() {
            @Override
            public void run() {
                BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner().stopScan(callback);
                devices.putAll((SimpleArrayMap<? extends BluetoothDevice, ? extends Integer>) callback.devices);
                listener.onScanFinished(BluetoothScanner.this);
            }
        };
        handler.postDelayed(finish, 10000);
    }

    // //////////////////////
    // Inner and Anonymous Classes
    public interface ScanFinishListener {
        void onScanFinished(BluetoothScanner scanner);
        void onScanResult(BluetoothScanner scanner, BluetoothDevice device, int rssi);
    }

    private class DashScanCallback extends ScanCallback {
        private ArrayMap<BluetoothDevice, Integer> devices = new ArrayMap<>();

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (device.getName() == null || !device.getName().contains("Dash")) return;
            Log.v(TAG, String.format("found device. address[%s] name[%s] type[%s] rssi[%s]",
                    device.getAddress(), device.getName(), device.getType(), result.getRssi()));
            devices.put(device, result.getRssi());
            listener.onScanResult(BluetoothScanner.this, device, result.getRssi());
            if (DEBUG) {
                handler.removeCallbacks(finish);
                finish.run();
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.v(TAG, "batch scan results=" + results.size());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "error=" + errorCode);
        }
    }
    // //////////////////////
    // Inner and Anonymous Classes



}
