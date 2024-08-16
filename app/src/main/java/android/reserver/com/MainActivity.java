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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_main); // Set the content view to the main activity layout

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check if there is a saved instance state to avoid recreating the fragment
        if (savedInstanceState == null) {
            // Begin the fragment transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace the container with the ReserveFragment
            transaction.replace(R.id.fragment_container, new ReserveFragment());

            // Commit the transaction
            transaction.commit();
        }
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
     * @return true if the item is handled, otherwise calls the superclass implementation.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle the "Info" menu item click
        if (id == R.id.ic_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
        // Handle the "Help" menu item click
        else if (id == R.id.ic_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item); // Call the superclass implementation for any unhandled menu items
    }
}
