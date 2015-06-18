package com.enyeinteractive.dashport;


import android.bluetooth.BluetoothDevice;
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
import android.widget.ProgressBar;
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
    ProgressBar progressBar;

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
        scanner.scanLE(getActivity(), new ScanFinishListener() {
            @Override
            public void onScanFinished(BluetoothScanner scanner) {
                progressBar.setVisibility(View.GONE);
                initAdapter(scanner.getDevices());
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
    public void initAdapter(ArrayMap<BluetoothDevice, Integer> devices) {
        Log.v(TAG, "initAdapter called with device count=" + devices.size());
        RecyclerView.Adapter adapter = new BTListAdapter(devices);
        list.setAdapter(adapter);
    }

    // //////////////////////
    // Inner and Anonymous Classes
    public interface ScanFinishListener {
        public void onScanFinished(BluetoothScanner scanner);
    }

}
