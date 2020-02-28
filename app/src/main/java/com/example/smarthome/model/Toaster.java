package com.example.smarthome.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Toaster take currently only a name and a power status
 *
 */
public class Toaster extends Device implements Serializable {


    private String deviceName;
    private boolean powerStatus;

    public Toaster(String deviceName){
        super(DeviceType.Toaster);
        this.deviceName = deviceName;
        this.powerStatus = false;
    }

    public Toaster(){
        super(DeviceType.Toaster);
    }

    public Toaster(String deviceName, boolean powerStatus) {
        super(DeviceType.Toaster);
        this.deviceName = deviceName;
        this.powerStatus = powerStatus;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isPowerStatus() {
        return powerStatus;
    }

    public void setPowerStatus(boolean powerStatus) {
        this.powerStatus = powerStatus;
    }

    @Override
    public String toString() {
        return "Toaster{" +
                "deviceName='" + deviceName + '\'' +
                ", powerStatus=" + powerStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Toaster toaster = (Toaster) o;
        return powerStatus == toaster.powerStatus &&
                Objects.equals(deviceName, toaster.deviceName);
    }
}
