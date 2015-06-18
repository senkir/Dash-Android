package com.enyeinteractive.dashport;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tcastillo
 *         Date: 6/17/15
 *         Time: 11:05 PM
 */
public class BTConnectFragment extends Fragment {

    // //////////////////////
    // Constants
    private static final String TAG = BTConnectFragment.class.getSimpleName();

    /**
     * TODO:
     * 1) scan for BT Pheripherals
     * 2) list by RSSI (ie: signal strength)
     */

    // //////////////////////
    // Fields
    @InjectView(android.R.id.list)
    RecyclerView list;
    private BluetoothScanner scanner;

    // //////////////////////
    // Constructors

    // //////////////////////
    // Getter & Setter

    // //////////////////////
    // Methods from SuperClass/Interfaces

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bt_device_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        scanner = new BluetoothScanner();
        scanner.scan(getActivity(), new ScanFinishListener() {
            @Override
            public void onScanFinished(BluetoothScanner scanner) {
                initAdapter();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    // //////////////////////
    // Methods
    public void initAdapter() {

    }

    // //////////////////////
    // Inner and Anonymous Classes
    public interface ScanFinishListener {
        public void onScanFinished(BluetoothScanner scanner);
    }

    private static class BluetoothScanner extends BroadcastReceiver {
        private boolean scanning;
        private ScanFinishListener listener;
        private ArrayMap<BluetoothDevice,Integer> devices = new ArrayMap<>();

        public void scan(Context context, ScanFinishListener listener) {
            if (scanning) {
                Log.w(TAG, "called to start scanning but scanning already active");
                return;
            }
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            this.listener = listener;
            if (adapter == null) {
                Toast.makeText(context, "bluetooth not supported", Toast.LENGTH_LONG).show();
                return;
            }
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(this, filter);
            adapter.startDiscovery();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    scanning = true;
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    scanning = false;
                    listener.onScanFinished(this);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int rssi = intent.getIntExtra(BluetoothDevice.EXTRA_RSSI, Integer.MIN_VALUE);
                    devices.put(device, rssi);
                default:
                    //noop
            }
        }
    }
}
