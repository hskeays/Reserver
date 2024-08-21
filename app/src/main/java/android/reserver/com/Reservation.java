package android.reserver.com;

public class Reservation {
    private int id;
    private int seatId;
    private String customerName;
    private String day;
    private String time;

    public Reservation(int id, int seatId, String customerName, String day, String time) {
        this.id = id;
        this.seatId = seatId;
        this.customerName = customerName;
        this.day = day;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
