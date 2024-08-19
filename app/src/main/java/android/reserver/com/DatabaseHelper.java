package android.reserver.com;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "restaurant_reservations.db"; // Database name
    private static final int DATABASE_VERSION = 1; // Database version

    // Table and column names for seats
    private static final String TABLE_SEATS = "Seats";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type";

    // Table and column names for reservations
    private static final String TABLE_RESERVATIONS = "Reservations";
    private static final String COLUMN_RESERVATION_ID = "id";
    private static final String COLUMN_SEAT_ID = "seatId";
    private static final String COLUMN_CUSTOMER_NAME = "customerName";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_TIME = "time";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // Call the superclass constructor
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Seats table
        String createSeatsTable = "CREATE TABLE " + TABLE_SEATS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TYPE + " TEXT)";
        db.execSQL(createSeatsTable); // Execute the table creation command

        // Create Reservations table
        String createReservationsTable = "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                COLUMN_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SEAT_ID + " INTEGER, " +
                COLUMN_CUSTOMER_NAME + " TEXT, " +
                COLUMN_DAY + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_SEAT_ID + ") REFERENCES " + TABLE_SEATS + "(" + COLUMN_ID + "))";
        db.execSQL(createReservationsTable); // Execute the table creation command

        // Insert starter data into the database
        insertStarterData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEATS);
        onCreate(db); // Call onCreate to recreate the tables
    }

    // Method to insert starter data into the database
    private void insertStarterData(SQLiteDatabase db) {
        // Insert data into the Seats table
        db.execSQL("INSERT INTO " + TABLE_SEATS + " (" + COLUMN_NAME + ", " + COLUMN_TYPE + ") VALUES " +
                "('C1', 'Chair')," +
                "('C2', 'Chair')," +
                "('C3', 'Chair')," +
                "('C4', 'Chair')," +
                "('C5', 'Chair')," +
                "('C6', 'Chair')," +
                "('B1', 'Booth')," +
                "('B2', 'Booth')," +
                "('B3', 'Booth')," +
                "('B4', 'Booth')," +
                "('B5', 'Booth')," +
                "('B6', 'Booth')");

        // Insert data into the Reservations table (if needed)
        db.execSQL("INSERT INTO " + TABLE_RESERVATIONS + " (" + COLUMN_SEAT_ID + ", " +
                COLUMN_CUSTOMER_NAME + ", " + COLUMN_DAY + ", " + COLUMN_TIME + ") VALUES " +
                "(2, 'Clark Kent', 'Friday', '5:00')," +
                "(6, 'Lois Lane', 'Friday', '5:00')");
    }

    // Method to retrieve available seats based on the selected day and time
    public List<Seat> getAvailableSeats(String day, String time, int numberOfSeats) {
        List<Seat> availableSeats = new ArrayList<>(); // List to hold available seats
        SQLiteDatabase db = null; // Database instance
        Cursor cursor = null; // Cursor to traverse query results

        try {
            db = this.getReadableDatabase(); // Get readable database

            // Construct the SQL query to find available seats
            String query = "SELECT * FROM " + TABLE_SEATS + " WHERE id NOT IN " +
                    "(SELECT seatId FROM " + TABLE_RESERVATIONS + " WHERE day = ? AND time = ?)";

            // Log the query and parameters for debugging
            Log.d("DatabaseHelper", "Query: " + query);
            Log.d("DatabaseHelper", "Parameters: " + Arrays.toString(new String[]{day, time}));

            // Execute the query with parameters
            cursor = db.rawQuery(query, new String[]{day, time});

            // Check if the cursor has results
            if (cursor.moveToFirst()) {
                // Get column indices for seat attributes
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);

                do {
                    // Retrieve seat attributes from the cursor
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String type = cursor.getString(typeIndex);

                    // Create a new Seat object and add it to the list of available seats
                    availableSeats.add(new Seat(id, name, type));
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "No available seats found."); // Log if no seats found
            }
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
        } finally {
            // Ensure resources are closed to prevent memory leaks
            if (cursor != null) {
                cursor.close(); // Close cursor if it is not null
            }
            if (db != null) {
                db.close(); // Close database if it is not null
            }
        }
        return availableSeats; // Return the list of available seats
    }
}
