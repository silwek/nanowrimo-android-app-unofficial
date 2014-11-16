package nanowrimo.onishinji.model;

public class Friend {
    private final String name;
    private final String id;

    public Friend(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
