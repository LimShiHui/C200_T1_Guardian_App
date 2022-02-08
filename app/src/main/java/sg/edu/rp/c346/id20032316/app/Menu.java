package sg.edu.rp.c346.id20032316.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Menu extends AppCompatActivity {

    Button btnGeoFencing, btnShowMe;

//    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnGeoFencing = findViewById(R.id.btnGeoFencing);
        btnShowMe = findViewById(R.id.btnShowMe);


        btnGeoFencing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, GeoFenceMenu.class);
                startActivity(intent);
            }
        });

        btnShowMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, ShowMe.class);
                startActivity(intent);
            }
        });

    }
}
