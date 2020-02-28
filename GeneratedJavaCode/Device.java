package sensomod.generated;

public class Device {

    public Device() {
    }

    public static enum deviceType {

        Washingmachine, Toaster
    }

    private deviceType devicetype;

    public deviceType getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(deviceType devicetype) {
        this.devicetype = devicetype;
    }
}
// Use IDE to generate toString and equals methods

