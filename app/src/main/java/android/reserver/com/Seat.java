package android.reserver.com;

public class Seat {
    private int id; // Added ID field
    private String name;
    private String type;

    // Constructor with ID, name, and type
    public Seat(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
