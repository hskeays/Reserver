package android.reserver.com;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;

public class CheckInActivity extends AppCompatActivity {

    private ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivPhoto = findViewById(R.id.iv_photo);

        Button btnCheckIn = findViewById(R.id.btn_check_in);
        Button btnActivateCamera = findViewById(R.id.btn_activate_camera);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnRetake = findViewById(R.id.btn_retake);
        Button btnCancel = findViewById(R.id.btn_cancel);

        btnCheckIn.setOnClickListener(v -> handleCheckInClick());
        btnActivateCamera.setOnClickListener(v -> handleActivateCameraClick());
        btnSave.setOnClickListener(v -> handleSaveClick());
        btnRetake.setOnClickListener(v -> handleRetakeClick());
        btnCancel.setOnClickListener(v -> finish());


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

    private void handleActivateCameraClick() {
        takePicturePreview.launch(null);
    }

    private void handleRetakeClick() {
        ivPhoto.setImageBitmap(null);
        takePicturePreview.launch(null);
    }

    // Handles the saving of the displayed image from the ImageView to the device's external storage.
    private boolean handleSaveClick() {
        Bitmap bitmap = ivPhoto.getDrawable() instanceof BitmapDrawable ? ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap() : null;

        if (bitmap == null) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "saved_image_" + System.currentTimeMillis() + ".png");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (imageUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
                    return true;
                }
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to create new MediaStore record", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void handleCheckInClick() {
        if (handleSaveClick()) {
            Toast.makeText(this, "Check in successful, we look forward to seeing you!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Check in unsuccessful, please try again.", Toast.LENGTH_LONG).show();
        }
    }

    // Capture and display a thumbnail image using the camera
    private final ActivityResultLauncher<Void> takePicturePreview = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), thumbnail -> {
        // Show thumbnail image
        ivPhoto.setImageBitmap(thumbnail);
    });
}