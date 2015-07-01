package com.enyeinteractive.dashport;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.enyeinteractive.dashport.bluetooth.BluetoothCommandManager;
import com.enyeinteractive.dashport.robot.DRSignalPacket;
import com.enyeinteractive.dashport.robot.DashController;
import com.enyeinteractive.dashport.robot.RobotProperties;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class DashControllerFragment extends Fragment {

    // //////////////////////
    // Constants
    private static final String TAG = DashControllerFragment.class.getSimpleName();

    // //////////////////////
    // Fields
    private BluetoothDevice bluetoothDevice;
    private DashController ctrl;

    @InjectView(R.id.seek_left)
    public SeekBar seekLeft;

    @InjectView(R.id.seek_right)
    public SeekBar seekRight;

    @InjectView(R.id.status_indicator)
    public TextView statusIndicator;

    // //////////////////////
    // Constructors

    public DashControllerFragment() {
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
        ctrl = new DashController(bluetoothDevice, getActivity(),
                new DashController.StatusChangeListener() {
                    @Override
                    public void onStatusChange(final @BluetoothCommandManager.ConnectionState int state) {
                        getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            switch(state) {
                                                                case BluetoothCommandManager.CONNECTING:
                                                                    statusIndicator.setTextColor(Color.YELLOW);
                                                                    statusIndicator.setText("connecting...");
                                                                    break;
                                                                case BluetoothCommandManager.CONNECTED:
                                                                    statusIndicator.setTextColor(Color.GREEN);
                                                                    statusIndicator.setText("connected.");
                                                                    break;
                                                                case BluetoothCommandManager.DISCONNECTED:
                                                                    statusIndicator.setTextColor(Color.RED);
                                                                    statusIndicator.setText("disconnected.");
                                                                default:
                                                                    //nothing
                                                            }
                                                        }
                                                    }
                        );
                    }

                    @Override
                    public void receivedNotifyWithProperties(RobotProperties properties) {
//                        Log.v(TAG, "received notify with properties=" + properties);
                    }

                    @Override
                    public void receivedNotifyWithData(byte[] data) {
                        Log.v(TAG, "received notify with data=" + Arrays.toString(data));
                    }

                    @Override
                    public void receivedNotifyWithSignals(DRSignalPacket signals) {
                        Log.v(TAG, "received notify with signals=" + signals);
                    }
                });
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

    @OnClick(R.id.action_color_picker)
    public void setLightColor(View view) {
        ColorPickerDialogBuilder
                .with(getActivity())
                .setTitle("Choose color")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[]
                            allColors) {
                        ctrl.setEyeColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @OnClick(R.id.action_reconnect)
    public void connectToDevice(View view) {
        ctrl.connect();
    }
    // //////////////////////
    // Inner and Anonymous Classes

}
