package sensomod.generated;

public class Devices extends Context {

    public Devices(Washingdevice washingdevice, Toasterdevice toasterdevice) {
        this.name = "Devices";
        this.washingdevice = washingdevice;
        this.toasterdevice = toasterdevice;
    }

    private Washingdevice washingdevice;

    private Toasterdevice toasterdevice;

    /*TODO: create logic to return the Device*/
    public Device output() {
    }

    public Washingdevice getWashingdevice() {
        return washingdevice;
    }

    public void setWashingdevice(Washingdevice washingdevice) {
        this.washingdevice = washingdevice;
    }

    public Toasterdevice getToasterdevice() {
        return toasterdevice;
    }

    public void setToasterdevice(Toasterdevice toasterdevice) {
        this.toasterdevice = toasterdevice;
    }
}
// Use IDE to generate toString and equals methods

