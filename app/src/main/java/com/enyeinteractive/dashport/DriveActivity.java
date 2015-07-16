package com.enyeinteractive.dashport;

import android.bluetooth.BluetoothDevice;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class DriveActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (f instanceof DashControllerFragment) {
            BluetoothDevice device = getIntent().getParcelableExtra("device");
            ((DashControllerFragment) f).setBluetoothDevice(device);
        }
    }

}
