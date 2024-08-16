package android.reserver.com;

public class TimeSlot {
    private String time;
    private int id;

    public TimeSlot(String time) {
        this.time = time;
    }

    public TimeSlot(String time, int id) {
        this.time = time;
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }
}