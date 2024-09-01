package android.reserver.com;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ReservationConfirmation extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    TextView tvConfirmationMsg;
    Button btnReminder;
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

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create notification channel
        createNotificationChannel();

        // Request POST_NOTIFICATIONS permission if needed
        requestNotificationPermission();

        // Get intent extras
        String confirmationMsg = getIntent().getStringExtra("CONFIRMATION_MSG");
        String customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        String tableName = getIntent().getStringExtra("TABLE_NAME");
        String selectedDay = getIntent().getStringExtra("SELECTED_DAY");
        String selectedTime = getIntent().getStringExtra("SELECTED_TIME");

        // Initialize views
        tvConfirmationMsg = findViewById(R.id.tv_confirmation);
        btnReminder = findViewById(R.id.btn_reminder);
        btnEmail = findViewById(R.id.btn_email);

        // Set confirmation message view text
        tvConfirmationMsg.setText(confirmationMsg);

        // Set email details
        String emailSubject = "Reservation Confirmation";
        String emailTextBody = String.format("Hey, it's %s! I reserved table %s on %s at %s.",
                                             customerName, tableName, selectedDay, selectedTime);

        // Add on click listener on Email button to send an email
        btnEmail.setOnClickListener(view -> sendEmail(emailSubject, emailTextBody));

        // Add on click listener on Reminder button to schedule a notification
        btnReminder.setOnClickListener(view -> scheduleNotification());
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

    private void requestNotificationPermission() {
        // Request POST_NOTIFICATIONS permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                                                  new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                                  NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reservation Reminder Channel";
            String description = "Channel for Reservation Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("reservationReminderChannel", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification() {
        // Check for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this,
                                                  new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                                  NOTIFICATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }

        // Check if the app can schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // Inform the user about the permission requirement
                Toast.makeText(this, "Please allow this app to schedule exact alarms in the app settings.",
                               Toast.LENGTH_SHORT).show();
                // Redirect the user to app settings to grant permission
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        // Set the time for the notification (4 days from now)
        Calendar calendar = Calendar.getInstance();
        // calendar.add(Calendar.SECOND, 3); *** FOR TESTING ***
        calendar.add(Calendar.DAY_OF_YEAR, 4);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule the notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Reminder set for 4 days from now!", Toast.LENGTH_SHORT).show();
        }
    }
}
