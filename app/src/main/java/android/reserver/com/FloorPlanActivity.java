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

public class FloorPlanActivity extends AppCompatActivity {

    private ImageView ivSmallTable1;
    private ImageView ivSmallTable2;
    private ImageView ivSmallTable3;
    private ImageView ivSmallTable4;
    private ImageView ivSmallTable5;
    private ImageView ivSmallTable6;

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

        int seatCountInt = getIntent().getIntExtra("SEAT_COUNT", 0);

        ImageView ivLargeTable1 = findViewById(R.id.iv_lrg_table1);
        ImageView ivLargeTable2 = findViewById(R.id.iv_lrg_table2);
        ImageView ivLargeTable3 = findViewById(R.id.iv_lrg_table3);
        ImageView ivLargeTable4 = findViewById(R.id.iv_lrg_table4);
        ImageView ivLargeTable5 = findViewById(R.id.iv_lrg_table5);
        ImageView ivLargeTable6 = findViewById(R.id.iv_lrg_table6);

        if (seatCountInt >= 4) {
            ivLargeTable1.setImageResource(R.drawable.rectanglefaded);
            ivLargeTable2.setImageResource(R.drawable.rectanglefaded);
            ivLargeTable3.setImageResource(R.drawable.rectanglefaded);
            ivLargeTable4.setImageResource(R.drawable.rectanglefaded);
            ivLargeTable5.setImageResource(R.drawable.rectanglefaded);
            ivLargeTable6.setImageResource(R.drawable.rectanglefaded);
        }
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
}