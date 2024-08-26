package android.reserver.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper; // Database helper instance
    private Intent audioIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Set the content view to the main activity layout
        setContentView(R.layout.activity_main);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase();

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        audioIntent = new Intent(this, AudioService.class);
        startService(audioIntent);

        // Check if there is a saved instance state to avoid recreating the fragment
        if (savedInstanceState == null) {
            // Replace the container with the ReserveFragment
            loadReserveFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(audioIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(audioIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();  // Close the underlying database
    }

    /**
     * Inflates the options menu in the toolbar.
     *
     * @param menu The menu to inflate.
     * @return true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu); // Inflate the app bar menu
        return true;
    }

    /**
     * Handles selection of menu items.
     *
     * @param item The selected menu item.
     * @return true if the item is handled; otherwise, calls the superclass implementation.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle the "Info" menu item click
        if (id == R.id.ic_info) {
            startActivity(new Intent(this, InfoActivity.class));
        }
        // Handle the "Help" menu item click
        else if (id == R.id.ic_help) {
            startActivity(new Intent(this, HelpActivity.class));
        }

        return super.onOptionsItemSelected(item); // Call the superclass implementation for any unhandled menu items
    }

    /**
     * Loads the ReserveFragment into the fragment container.
     */
    private void loadReserveFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ReserveFragment());
        transaction.commit(); // Commit the transaction
    }
}
