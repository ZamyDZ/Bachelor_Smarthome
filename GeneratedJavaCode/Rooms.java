package sensomod.generated;

public class Rooms extends Context {

    public Rooms(Users users) {
        this.name = "Rooms";
        this.users = users;
    }

    private Users users;

    public void decisionLogic() {
        this.owner = User.email;
    }

    /*TODO: create logic to return the Room*/
    public Room output() {
    // TODO
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
// Use IDE to generate toString and equals methods

