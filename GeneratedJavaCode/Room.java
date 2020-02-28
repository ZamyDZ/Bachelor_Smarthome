package sensomod.generated;

public class Room {

    public Room(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    private String owner;

    private String name;

    public static enum roomCategory {

        Kitchen, Livingroom, Office, Sleepingroom
    }

    private roomCategory roomcategory;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public roomCategory getRoomcategory() {
        return roomcategory;
    }

    public void setRoomcategory(roomCategory roomcategory) {
        this.roomcategory = roomcategory;
    }
}
// Use IDE to generate toString and equals methods

