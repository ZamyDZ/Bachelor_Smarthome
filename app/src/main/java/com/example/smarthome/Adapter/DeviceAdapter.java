package com.example.smarthome.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.smarthome.R;
import com.example.smarthome.model.Toaster;
import com.example.smarthome.model.Washingmachine;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    //current list of devices
    private ArrayList<Object> deviceList = new ArrayList<>();
    //OnDeviceClick-Listener to handle click events on the device cards
    private OnDeviceClickListener deviceListener;

    //constructor
    public DeviceAdapter(ArrayList<Object> deviceList){
        this.deviceList = deviceList;
    }

    public interface OnDeviceClickListener {
        void onDeviceClick(int position);
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        deviceListener = listener;
    }


    public static class DeviceViewHolder extends RecyclerView.ViewHolder{
        //Textviews
        public TextView deviceName;
        public TextView deviceStatus;
        public TextView deviceType;
        //Imageviews
        public ImageView deviceDashboard;
        public ImageView deviceStatusImage;

        public DeviceViewHolder(@NonNull View itemView, OnDeviceClickListener listener) {
            super(itemView);

            deviceName = itemView.findViewById(R.id.DeviceItemName);
            deviceStatus = itemView.findViewById(R.id.DeviceItemstatus);
            deviceType = itemView.findViewById(R.id.DeviceItemType);
            deviceStatusImage = itemView.findViewById(R.id.deviceItemCardStatus);

            //set click event on the dasboard icon
            deviceDashboard = itemView.findViewById(R.id.DeviceItemDashboard);
            deviceDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeviceClick(position);
                        }
                    }
                }
            });
        }
    }

    /**
     * creating the view to display the device item
     * @param parent current View of the mainactivity
     * @param viewType
     * @return Viewholder
     */
    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        DeviceViewHolder deviceViewHolder = new DeviceViewHolder(view, deviceListener);     //TODO ADD itemListener !!!
        return deviceViewHolder;
    }

    /**
     * For each Device in the deviceList at a certain position the information will be displayed
     * @param holder Deviceviewholder
     * @param position Position of a device in the ArrayList
     */
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Object deviceClass = deviceList.get(position);

        //if equals washingmachine
        if(deviceClass.getClass().equals(Washingmachine.class)){
            Washingmachine currentDevice = (Washingmachine) deviceList.get(position);

            holder.deviceName.setText(currentDevice.getName());
            //holder.deviceStatus.setText(String.valueOf(currentDevice.isPowerStatus()));
            holder.deviceType.setText("Washingmachine");
            if(!currentDevice.isPowerStatus()){
                holder.deviceStatusImage.setColorFilter(ContextCompat.getColor(holder.deviceStatusImage.getContext(), R.color.red));
                holder.deviceStatus.setText("OFF");
                holder.deviceStatus.setTextColor(Color.parseColor("#eb072d"));
            }
            if(currentDevice.isPowerStatus()){
                holder.deviceStatusImage.setColorFilter(ContextCompat.getColor(holder.deviceStatusImage.getContext(), R.color.green));
                holder.deviceStatus.setText("ON");
                holder.deviceStatus.setTextColor(Color.parseColor("#03fc62"));
            }
        }

        //if equals toaster
        if(deviceClass.getClass().equals(Toaster.class)){
            Toaster currentDevice = (Toaster) deviceList.get(position);

            holder.deviceName.setText(currentDevice.getDeviceName());
            //holder.deviceStatus.setText(String.valueOf(currentDevice.isPowerStatus()));
            holder.deviceType.setText("Toaster");
            if(!currentDevice.isPowerStatus()){
                holder.deviceStatusImage.setColorFilter(ContextCompat.getColor(holder.deviceStatusImage.getContext(), R.color.red));
                holder.deviceStatus.setText("OFF");
                holder.deviceStatus.setTextColor(Color.parseColor("#eb072d"));
            }
            if(currentDevice.isPowerStatus()){
                holder.deviceStatusImage.setColorFilter(ContextCompat.getColor(holder.deviceStatusImage.getContext(), R.color.green));
                holder.deviceStatus.setText("ON");
                holder.deviceStatus.setTextColor(Color.parseColor("#03fc62"));
            }
        }
    }

    /**
     * amount of rooms in the list
     * @return size
     */
    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    /**
     * Updateds the current list
     * @param currentDeviceList list that is provided to be update
     */
    public void updateDeviceList(ArrayList<Object> currentDeviceList){
        deviceList = currentDeviceList;
        notifyDataSetChanged();
    }


}
