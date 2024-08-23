package android.reserver.com;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FloorPlanActivity extends AppCompatActivity {

    // Map to associate ImageViews with their respective table names
    private final Map<ImageView, String> tableMap = new HashMap<>();
    // HashMap to associate table names with their IDs
    private final HashMap<String, Integer> tableNameToIdHashMap = new HashMap<>();
    private DatabaseHelper dbHelper; // Database helper instance
    // ImageView references for small and large tables
    private ImageView ivSmallTable1, ivSmallTable2, ivSmallTable3, ivSmallTable4, ivSmallTable5, ivSmallTable6;
    private ImageView ivLargeTable1, ivLargeTable2, ivLargeTable3, ivLargeTable4, ivLargeTable5, ivLargeTable6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_floor_plan);

        dbHelper = new DatabaseHelper(this);

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adjust window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve extras from the Intent
        int seatCountInt = getIntent().getIntExtra("SEAT_COUNT", 0);
        ArrayList<String> unavailSeatNames = getIntent().getStringArrayListExtra("UNAVAIL_SEAT_NAMES");
        String selectedDay = getIntent().getStringExtra("SELECTED_DAY");
        String selectedTime = getIntent().getStringExtra("SELECTED_TIME");

        // Initialize ImageView variables
        initializeImageViews();

        initializeTableNameIdHashMap();

        // Create arrays for small and large table ImageViews
        ImageView[] smallTableImgViews = {ivSmallTable1, ivSmallTable2, ivSmallTable3, ivSmallTable4, ivSmallTable5, ivSmallTable6};
        ImageView[] largeTableImgViews = {ivLargeTable1, ivLargeTable2, ivLargeTable3, ivLargeTable4, ivLargeTable5, ivLargeTable6};

        // Set OnClickListeners for small tables
        setTableClickListeners(smallTableImgViews, "C", unavailSeatNames);

        // Set OnClickListeners for large tables
        setTableClickListeners(largeTableImgViews, "B", unavailSeatNames);

        // Set images for unavailable seats
        setImagesForUnavailableSeats(unavailSeatNames, seatCountInt);

        EditText etvName = findViewById(R.id.etv_name_field);
        Button btnReserve = findViewById(R.id.btn_reserve);

        btnReserve.setOnClickListener(view -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.getWritableDatabase();

            // check hashmap size is 1 - only one table selected
            // check customerName != null
            String customerName = etvName.getText().toString();
            if (tableMap.size() == 1 && !customerName.isEmpty()) {
                // create dialogue box "Thank you NAME for reserving table TABLENAME on DAY at TIME"
                AlertDialog.Builder builder = new AlertDialog.Builder(FloorPlanActivity.this);

                String tableName = tableMap.values().iterator().next();
                String tableId = Objects.requireNonNull(tableNameToIdHashMap.get(tableName)).toString();
                String message = String.format("Are you sure you want to reserve table %s on %s at %s?", tableName, selectedDay, selectedTime);

                builder.setMessage(message);

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                builder.setPositiveButton("Reserve", (dialog, which) -> {
                    long result = dbHelper.insertReservation(db, tableId, customerName, selectedDay, selectedTime);

                    if (result != -1) { // Check if insertion was successful
//                        Toast.makeText(FloorPlanActivity.this, "Reservation successful!", Toast.LENGTH_LONG).show();
                        String confirmation_msg = String.format("Thank you %s for reserving seat %s on %s at %s!", customerName, tableName, selectedDay, selectedTime);
                        Intent intent = new Intent(this, ReservationConfirmation.class);
                        intent.putExtra("CONFIRMATION_MSG", confirmation_msg);
                        intent.putExtra("CUSTOMER_NAME", customerName);
                        intent.putExtra("TABLE_NAME", tableName);
                        intent.putExtra("SELECTED_DAY", selectedDay);
                        intent.putExtra("SELECTED_TIME", selectedTime);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FloorPlanActivity.this, "Reservation failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    // Finish the activity after the reservation process
                    finish();
                });

                AlertDialog dialog = builder.create();
                dialog.show(); // Display the dialog
            } else {
                Toast.makeText(FloorPlanActivity.this, "Please select only one table and enter your name.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_back) {
            finish();
            return true;
        } else if (id == R.id.ic_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.ic_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes ImageView variables for small and large tables.
     */
    private void initializeImageViews() {
        ivSmallTable1 = findViewById(R.id.iv_small_table1);
        ivSmallTable2 = findViewById(R.id.iv_small_table2);
        ivSmallTable3 = findViewById(R.id.iv_small_table3);
        ivSmallTable4 = findViewById(R.id.iv_small_table4);
        ivSmallTable5 = findViewById(R.id.iv_small_table5);
        ivSmallTable6 = findViewById(R.id.iv_small_table6);

        ivLargeTable1 = findViewById(R.id.iv_lrg_table1);
        ivLargeTable2 = findViewById(R.id.iv_lrg_table2);
        ivLargeTable3 = findViewById(R.id.iv_lrg_table3);
        ivLargeTable4 = findViewById(R.id.iv_lrg_table4);
        ivLargeTable5 = findViewById(R.id.iv_lrg_table5);
        ivLargeTable6 = findViewById(R.id.iv_lrg_table6);
    }

    /**
     * Sets OnClickListeners for table ImageViews based on their names and availability.
     *
     * @param tableImgViews    Array of ImageView references for the tables
     * @param tablePrefix      Prefix for naming tables (e.g., "C" for small tables, "B" for large tables)
     * @param unavailSeatNames List of unavailable seat names
     */
    private void setTableClickListeners(ImageView[] tableImgViews, String tablePrefix, ArrayList<String> unavailSeatNames) {
        for (int i = 0; i < tableImgViews.length; i++) {
            String tableName = tablePrefix + (i + 1); // Naming tables as "C1", "C2", etc.
            tableImgViews[i].setOnClickListener(new ImageToggleListener(tableImgViews[i], tableName, unavailSeatNames));
        }
    }

    /**
     * Sets images for unavailable seats based on the provided list and seat count.
     *
     * @param unavailSeatNames List of unavailable seat names
     * @param seatCountInt     Total number of seats
     */
    private void setImagesForUnavailableSeats(ArrayList<String> unavailSeatNames, int seatCountInt) {
        // Set all small tables to "faded" if seat count is 4 or more
        if (seatCountInt >= 4) {
            for (ImageView smallTable : new ImageView[]{ivSmallTable1, ivSmallTable2, ivSmallTable3, ivSmallTable4, ivSmallTable5, ivSmallTable6}) {
                smallTable.setImageResource(R.drawable.roundfaded);
            }
        }

        // Check for unavailable seats and set the corresponding images
        for (String seatName : unavailSeatNames) {
            switch (seatName) {
                case "C1":
                    ivSmallTable1.setImageResource(R.drawable.roundfaded);
                    break;
                case "C2":
                    ivSmallTable2.setImageResource(R.drawable.roundfaded);
                    break;
                case "C3":
                    ivSmallTable3.setImageResource(R.drawable.roundfaded);
                    break;
                case "C4":
                    ivSmallTable4.setImageResource(R.drawable.roundfaded);
                    break;
                case "C5":
                    ivSmallTable5.setImageResource(R.drawable.roundfaded);
                    break;
                case "C6":
                    ivSmallTable6.setImageResource(R.drawable.roundfaded);
                    break;
                case "B1":
                    ivLargeTable1.setImageResource(R.drawable.rectanglefaded);
                    break;
                case "B2":
                    ivLargeTable2.setImageResource(R.drawable.rectanglefaded);
                    break;
                case "B3":
                    ivLargeTable3.setImageResource(R.drawable.rectanglefaded);
                    break;
                case "B4":
                    ivLargeTable4.setImageResource(R.drawable.rectanglefaded);
                    break;
                case "B5":
                    ivLargeTable5.setImageResource(R.drawable.rectanglefaded);
                    break;
                case "B6":
                    ivLargeTable6.setImageResource(R.drawable.rectanglefaded);
                    break;
                default:
                    break;
            }
        }
    }

    // Method to initialize table names and IDs HashMap
    private void initializeTableNameIdHashMap() {
        // C1 to C6 with values 1 to 6
        for (int i = 1; i <= 6; i++) {
            tableNameToIdHashMap.put("C" + i, i);
        }

        // B1 to B6 with values 7 to 12
        for (int i = 1; i <= 6; i++) {
            tableNameToIdHashMap.put("B" + i, i + 6);
        }
    }

    /**
     * Inner class to handle toggling of table images on click.
     */
    private class ImageToggleListener implements View.OnClickListener {
        private final ImageView imageView;
        private final String tableName;
        private final ArrayList<String> unavailSeatNames;

        public ImageToggleListener(ImageView imageView, String tableName, ArrayList<String> unavailSeatNames) {
            this.imageView = imageView;
            this.tableName = tableName;
            this.unavailSeatNames = unavailSeatNames;
        }

        @Override
        public void onClick(View v) {
            // Check if the table name is in the unavailable seat names list
            if (unavailSeatNames.contains(tableName)) {
                // If the table is unavailable, do nothing
                return;
            }

            Drawable currentDrawable = imageView.getDrawable();
            Drawable roundSolid = ResourcesCompat.getDrawable(getResources(), R.drawable.roundsolid, null);
            Drawable roundFaded = ResourcesCompat.getDrawable(getResources(), R.drawable.roundfaded, null);
            Drawable rectangleSolid = ResourcesCompat.getDrawable(getResources(), R.drawable.rectanglesolid, null);
            Drawable rectangleFaded = ResourcesCompat.getDrawable(getResources(), R.drawable.rectanglefaded, null);

            // Toggle the image based on the type of table
            if (isRoundTable()) {
                toggleImage(currentDrawable, roundSolid, roundFaded, R.drawable.roundsolid, R.drawable.roundfaded);
            } else if (isRectangleTable()) {
                toggleImage(currentDrawable, rectangleSolid, rectangleFaded, R.drawable.rectanglesolid, R.drawable.rectanglefaded);
            }
        }

        /**
         * Toggles the image state based on the current drawable.
         *
         * @param currentDrawable The current drawable of the ImageView
         * @param solidDrawable   Drawable for solid state
         * @param fadedDrawable   Drawable for faded state
         * @param solidResId      Resource ID for solid drawable
         * @param fadedResId      Resource ID for faded drawable
         */
        private void toggleImage(Drawable currentDrawable, Drawable solidDrawable, Drawable fadedDrawable, int solidResId, int fadedResId) {
            if (currentDrawable != null && solidDrawable != null && fadedDrawable != null) {
                if (Objects.equals(currentDrawable.getConstantState(), solidDrawable.getConstantState())) {
                    // Change to "faded" state
                    imageView.setImageResource(fadedResId);
                    tableMap.put(imageView, tableName); // Save the table name
                } else if (Objects.equals(currentDrawable.getConstantState(), fadedDrawable.getConstantState())) {
                    // Change back to "solid" state
                    imageView.setImageResource(solidResId);
                    tableMap.remove(imageView); // Remove the table name
                }
            }
        }

        /**
         * Checks if the table is a round table.
         *
         * @return True if the table is round, otherwise false
         */
        private boolean isRoundTable() {
            return tableName.startsWith("C");
        }

        /**
         * Checks if the table is a rectangular table.
         *
         * @return True if the table is rectangular, otherwise false
         */
        private boolean isRectangleTable() {
            return tableName.startsWith("B");
        }
    }
}
