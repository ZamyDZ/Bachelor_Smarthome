package com.example.smarthome.model;


/**
 * this class is only used to get the device-type
 */
public class Device {
    private DeviceType deviceType;

    public Device(){}

    public Device(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceType=" + deviceType +
                '}';
    }
}
