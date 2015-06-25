package com.enyeinteractive.dashport;

import android.bluetooth.BluetoothDevice;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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

    @InjectView(R.id.seek_left)
    public SeekBar seekLeft;

    @InjectView(R.id.seek_right)
    public SeekBar seekRight;

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
        seekLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    ctrl.setThrottleLeft(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO: ANDROID IMPL
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO: ANDROID IMPL
            }
        });

        seekRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    ctrl.setThrottleRight(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO: ANDROID IMPL
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO: ANDROID IMPL
            }
        });
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

    @OnClick(R.id.action_stop)
    public void stopAllActions(View view) {
        ctrl.reset();
    }
    // //////////////////////
    // Inner and Anonymous Classes

}
