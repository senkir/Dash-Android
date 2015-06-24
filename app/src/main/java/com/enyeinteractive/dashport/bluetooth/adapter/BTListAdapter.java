package com.enyeinteractive.dashport.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.enyeinteractive.dashport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tcastillo
 *         Date: 6/17/15
 *         Time: 11:52 PM
 */
public class BTListAdapter extends RecyclerView.Adapter<BTListAdapter.ViewHolder> {

    List<Integer> rssiValues;
    List<BluetoothDevice> devices;
    View.OnClickListener listener;

    public BTListAdapter(ArrayMap<BluetoothDevice, Integer> data, View.OnClickListener listener) {
        rssiValues = new ArrayList<>();
        devices = new ArrayList<>();
        devices.addAll(data.keySet());
        rssiValues.addAll(data.values());
        this.listener = listener;
    }

    @Override
    public BTListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bt_device, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(BTListAdapter.ViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        holder.title.setText(device.getName());
        holder.rssi.setText(String.valueOf(rssiValues.get(position)));
        View parent = (View) holder.title.getParent();
        parent.setTag(position);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.rssi)
        TextView rssi;

        public ViewHolder(View itemView, View.OnClickListener listener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(listener);
        }
    }
}
