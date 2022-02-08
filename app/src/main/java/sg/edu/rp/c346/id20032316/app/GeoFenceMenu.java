package sg.edu.rp.c346.id20032316.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GeoFenceMenu extends AppCompatActivity {

    TextView tvGeoFencing, tvSetGeoFencing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence_menu);

        tvGeoFencing = findViewById(R.id.tvGeoFence);
        tvSetGeoFencing = findViewById(R.id.tvSetGeoFence);

        tvGeoFencing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeoFenceMenu.this, ShowGeoFencing.class);
                startActivity(intent);
            }
        });

        tvSetGeoFencing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeoFenceMenu.this, SetGeoFencing.class);
                startActivity(intent);
            }
        });
    }
}