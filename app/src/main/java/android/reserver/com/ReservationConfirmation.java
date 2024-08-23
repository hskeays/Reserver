package android.reserver.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReservationConfirmation extends AppCompatActivity {

    TextView tvConfirmationMsg;
    Button btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get intent extras
        String confirmationMsg = getIntent().getStringExtra("CONFIRMATION_MSG");
        String customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        String tableName = getIntent().getStringExtra("TABLE_NAME");
        String selectedDay = getIntent().getStringExtra("SELECTED_DAY");
        String selectedTime = getIntent().getStringExtra("SELECTED_TIME");

        // Initialize views
        tvConfirmationMsg = findViewById(R.id.tv_confirmation);
        btnEmail = findViewById(R.id.btn_email);

        // Set confirmation message view text
        tvConfirmationMsg.setText(confirmationMsg);

        // Set email details
        String emailSubject = "Reservation Confirmation";
        String emailTextBody = String.format("Hey it's %s, I reserved table %s on %s at %s.", customerName, tableName, selectedDay, selectedTime);

        // Add on click listener on Email button to send an email
        btnEmail.setOnClickListener(view -> sendEmail(emailSubject, emailTextBody));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu); // Inflate the app bar menu
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

    private void sendEmail(String subject, String textBody) {
        // Create an Intent to send an email
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this

        // Add email details
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, textBody);

        // Verify that there is an email app to handle the intent
        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ReservationConfirmation.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}