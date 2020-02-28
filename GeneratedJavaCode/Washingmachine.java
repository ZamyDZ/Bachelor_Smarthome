package sensomod.generated;

public class Washingmachine {

    public Washingmachine(String name, boolean powerstatus, double temperature) {
        this.name = name;
        this.powerstatus = powerstatus;
        this.temperature = temperature;
    }

    private String name;

    private boolean powerstatus;

    private double temperature;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPowerstatus() {
        return powerstatus;
    }

    public void setPowerstatus(boolean powerstatus) {
        this.powerstatus = powerstatus;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
// Use IDE to generate toString and equals methods

