package sg.edu.rp.c346.id20032316.app;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import sg.edu.rp.c346.id20032316.app.databinding.ActivityShowMeBinding;

public class ShowMe extends AppCompatActivity {

    private GoogleMap map;
    ListView lvLocation;
    Circle circle;

    SharedPreferences prefs;

    ArrayList alLocation;
    ArrayAdapter<String> aaLocation;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

    String LOCATION_URL = "http://10.0.2.2/C200_guardian/getElderlyLocation.php";
    String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_me);

        lvLocation = findViewById(R.id.lvLocation);

        alLocation = new ArrayList();
        aaLocation = new ArrayAdapter<>(ShowMe.this, android.R.layout.simple_list_item_1, alLocation);
        lvLocation.setAdapter(aaLocation);

        asyncHttpClient = new AsyncHttpClient();
        requestParams = new RequestParams();

        prefs = getSharedPreferences("records", MODE_PRIVATE);
        Integer idRetrieved = prefs.getInt("elderlyId", 0);

//        Log.i("check", "id in ShowMe(Location): " + idRetrieved); // can delete

        if (idRetrieved == 0){
            Log.i("check", "loading details not found");
            Toast.makeText(this,"loading details not found",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("check", "elderly id: " + idRetrieved);
            Toast.makeText(this,"loading details successfully",Toast.LENGTH_SHORT).show();
        }


        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;

                UiSettings ui = map.getUiSettings();

                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);

                int permissionCheck = ContextCompat.checkSelfPermission(ShowMe.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(ShowMe.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }

                requestParams.put("id", idRetrieved); // elderly Id

                asyncHttpClient.get(LOCATION_URL, requestParams, new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

                        Log.i("DEBUG", "fail to connect to web service" + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            JSONArray result = response.getJSONArray("record");

                            Log.i("DEBUG", "records: " + result);
                            Log.i("DEBUG", "AL size: " + response.length());

                            for (int i = 0; i < result.length(); i++) {

                                JSONObject obj = (JSONObject) result.get(i);

                                Double lat = Double.parseDouble(obj.getString("Lat"));
                                Double lng = Double.parseDouble(obj.getString("Lon"));
                                String time = obj.getString("timestamp");

                                alLocation.add("Latitude: " + lat + "\nLongitude: " + lng + "\nTime: " + time);

                                LatLng geofence = new LatLng(lat, lng);

                                circle = map.addCircle(new CircleOptions()
                                        .center(geofence)
                                        .radius(10)
                                        .strokeColor(Color.TRANSPARENT)
                                        .fillColor(Color.YELLOW));
                            }
                            aaLocation.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        lvLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                requestParams.put("id", idRetrieved); // elderly Id

                asyncHttpClient.get(LOCATION_URL, requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            JSONArray result = response.getJSONArray("record");
                            JSONObject obj = (JSONObject) result.get(position);

                            Double lat = Double.parseDouble(obj.getString("Lat"));
                            Double lng = Double.parseDouble(obj.getString("Lon"));
                            LatLng geofence = new LatLng(lat, lng);

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(geofence, 20));
                            aaLocation.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                });
            }
        });


    }
}

