package android.reserver.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FloorPlanActivity extends AppCompatActivity {

    private ImageView ivSmallTable1;
    private ImageView ivSmallTable2;
    private ImageView ivSmallTable3;
    private ImageView ivSmallTable4;
    private ImageView ivSmallTable5;
    private ImageView ivSmallTable6;

    private ImageView ivLargeTable1;
    private ImageView ivLargeTable2;
    private ImageView ivLargeTable3;
    private ImageView ivLargeTable4;
    private ImageView ivLargeTable5;
    private ImageView ivLargeTable6;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_floor_plan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Extras
        int seatCountInt = getIntent().getIntExtra("SEAT_COUNT", 0);
        ArrayList<String> unavailSeatNames = getIntent().getStringArrayListExtra("UNAVAIL_SEAT_NAMES");

        // Initialize the ImageView variables
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

        setImagesForUnavailableSeats(unavailSeatNames, seatCountInt);
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

    private void setImagesForUnavailableSeats(ArrayList<String> unavailSeatNames, int seatCountInt) {
        if (seatCountInt >= 4) {
            ivSmallTable1.setImageResource(R.drawable.roundfaded);
            ivSmallTable2.setImageResource(R.drawable.roundfaded);
            ivSmallTable3.setImageResource(R.drawable.roundfaded);
            ivSmallTable4.setImageResource(R.drawable.roundfaded);
            ivSmallTable5.setImageResource(R.drawable.roundfaded);
            ivSmallTable6.setImageResource(R.drawable.roundfaded);
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
}