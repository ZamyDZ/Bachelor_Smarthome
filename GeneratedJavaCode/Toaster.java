package sensomod.generated;

public class Toaster {

    public Toaster(String name, boolean powerstatus) {
        this.name = name;
        this.powerstatus = powerstatus;
    }

    private String name;

    private boolean powerstatus;

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
}
// Use IDE to generate toString and equals methods

