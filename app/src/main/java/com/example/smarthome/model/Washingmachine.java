package com.example.smarthome.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Washingmachine has values about powerstatus, name and temperature of the water
 */
public class Washingmachine extends Device implements Serializable {

    private String name;
    private boolean powerStatus;
    private double temperature;
    //TODO add timer

    public Washingmachine(){
        super(DeviceType.Washingmachine);
    }

    public Washingmachine(String name){
        super(DeviceType.Washingmachine);
        this.name = name;
        this.powerStatus = false;
        this.temperature = 23.00;
    }

    //public Washingmachine(String name){
    //    this.name = name;
    //    this.powerStatus = false;
    //    this.temperature = 23.00;
    //}

    public Washingmachine(String name, boolean powerStatus, double temperature) {
        super(DeviceType.Washingmachine);
        this.name = name;
        this.powerStatus = powerStatus;
        this.temperature = temperature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPowerStatus() {
        return powerStatus;
    }

    public void setPowerStatus(boolean powerStatus) {
        this.powerStatus = powerStatus;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "Washingmachine{" +
                "name='" + name + '\'' +
                ", powerStatus=" + powerStatus +
                ", temperature=" + temperature +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Washingmachine that = (Washingmachine) o;
        return powerStatus == that.powerStatus &&
                Double.compare(that.temperature, temperature) == 0 &&
                Objects.equals(name, that.name);
    }

}
