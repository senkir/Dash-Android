package com.enyeinteractive.dashport;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.enyeinteractive.dashport.bluetooth.BluetoothScanner;
import com.enyeinteractive.dashport.bluetooth.adapter.BTListAdapter;

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

    @InjectView(R.id.progress_spinner)
    View progressBar;

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
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar.setVisibility(View.VISIBLE);
        scanner = new BluetoothScanner();
        scanner.scanLE(new BluetoothScanner.ScanFinishListener() {
            @Override
            public void onScanFinished(BluetoothScanner scanner) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                initAdapter(scanner.getDevices());
            }

            @Override
            public void onScanResult(BluetoothScanner scanner, BluetoothDevice device, int rssi) {
                //TODO: ANDROID IMPL
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
    public void initAdapter(final ArrayMap<BluetoothDevice, Integer> devices) {
        Log.v(TAG, "initAdapter called with device count=" + devices.size());
        RecyclerView.Adapter adapter = new BTListAdapter(devices, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                BluetoothDevice device = devices.keyAt(position);
                Intent intent = new Intent(getActivity(), DriveActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
            }
        });
        list.setAdapter(adapter);
    }

}
