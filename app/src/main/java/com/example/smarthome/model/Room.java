package com.example.smarthome.model;


import androidx.annotation.Nullable;

public class Room {
    //Owner fo the room who is a user
    private String owner;
    //Name of the Room TODO can be used for ID
    private String name;
    //Category of the Room; takes
    private Category roomCategory;
    //TODO ADD COLLECTION OF Devices


    public Room(String  owner, String name, Category roomCategory) {
        this.owner = owner;
        this.name = name;
        this.roomCategory = roomCategory;
    }

    //Default constructor
    public Room(){}

    /**
     * Getters & Setters
     */
    public String  getOwner() {
        return owner;
    }

    public void setOwner(String  owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(Category roomCategory) {
        this.roomCategory = roomCategory;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this.getName().equals(((Room)obj).getName()) && this.getOwner().equals(((Room)obj).getOwner()) && this.getRoomCategory().equals(((Room)obj).getRoomCategory())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Room{" +
                "owner=" + owner +
                ", name='" + name + '\'' +
                ", roomCategory=" + roomCategory +
                '}';
    }

}
