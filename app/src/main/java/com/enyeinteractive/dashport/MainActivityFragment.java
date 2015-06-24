package com.enyeinteractive.dashport;

import android.bluetooth.BluetoothDevice;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.enyeinteractive.dashport.bluetooth.DashController;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // //////////////////////
    // Constants
    private static final String TAG = MainActivityFragment.class.getSimpleName();

    // //////////////////////
    // Fields
    private BluetoothDevice bluetoothDevice;
    private DashController ctrl;

    // //////////////////////
    // Constructors

    public MainActivityFragment() {
    }
    // //////////////////////
    // Getter & Setter

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        initDevice(bluetoothDevice);
    }

    private void initDevice(BluetoothDevice bluetoothDevice) {


    }
    // //////////////////////
    // Methods from SuperClass/Interfaces


    @Override
    public View
    onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (bluetoothDevice == null) {
            Log.e(TAG, "onStart: no bluetooth device detected");
            return;
        }
        ctrl = new DashController(bluetoothDevice, getActivity());
        ctrl.callNotifier();
    }

    @Override
    public void onStop() {
        super.onStop();
        ctrl.close();
        ctrl = null;
    }

    // //////////////////////
    // Methods

    // //////////////////////
    // Inner and Anonymous Classes

}
