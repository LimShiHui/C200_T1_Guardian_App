package sg.edu.rp.c346.id20032316.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SetGeoFencing extends AppCompatActivity {

    private GoogleMap map;
    EditText etLat, etLng, etRadius, etElderlyId;
    Button btnAdd, btnView;
    LatLng currentGeoFence;
    Circle circle, currentCircle, newCircle;
    ArrayList alGeoFence;
    ArrayAdapter<String> aaGeoFence;

    SharedPreferences prefs;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

//    String BASE_URL = "http://10.0.2.2/C200_guardian/getGuardianByContact.php?";
    String GEOFENCE_URL = "http://10.0.2.2/C200_guardian/getGeoFence.php";
    String ADD_GEOFENCE_URL = "http://10.0.2.2/C200_guardian/addGeoFence.php";
//    String ELDERLYID_URL = "http://10.0.2.2/C200_guardian/getElderlyId.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_geo_fencing);

        etLat = findViewById(R.id.etLat);
        etLng = findViewById(R.id.etLng);
        etRadius = findViewById(R.id.etRadius);
        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        etElderlyId = findViewById(R.id.etElderlyId); // delete


        alGeoFence = new ArrayList();
        aaGeoFence = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alGeoFence);

        asyncHttpClient = new AsyncHttpClient();
        requestParams = new RequestParams();

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        prefs = getSharedPreferences("records", MODE_PRIVATE);
        Integer idRetrieved = prefs.getInt("elderlyId", 0);

//        Log.i("check", "id in SetGeoFence: " + idRetrieved); // can delete

        if (idRetrieved == 0){
            Log.i("check", "loading details not found");
            Toast.makeText(this,"loading details not found",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("check", "elderly id: " + idRetrieved);
            Toast.makeText(this,"loading details successfully",Toast.LENGTH_SHORT).show();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                UiSettings ui = map.getUiSettings();

                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);

                int permissionCheck = ContextCompat.checkSelfPermission(SetGeoFencing.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(SetGeoFencing.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }

                LatLng poi_RP = new LatLng(1.44224, 103.785733);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_RP, 15));

                requestParams.put("id", idRetrieved); // elderly Id

                etElderlyId.setText(idRetrieved.toString());

                asyncHttpClient.get(GEOFENCE_URL, requestParams, new JsonHttpResponseHandler() {

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

                            for (int i = 0; i < result.length(); i++) {
                                JSONObject obj = (JSONObject) result.get(i);

                                Double lat = Double.parseDouble(obj.getString("Lat"));
                                Double lng = Double.parseDouble(obj.getString("Lon"));
                                Integer radius = Integer.parseInt(obj.getString("radius"));

                                LatLng geofence = new LatLng(lat, lng);

                                circle = map.addCircle(new CircleOptions()
                                        .center(geofence)
                                        .radius(radius)
                                        .strokeColor(Color.BLUE)
                                        .fillColor(Color.TRANSPARENT));
                            }
                            aaGeoFence.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // markers
//                LatLng poi_CausewayPoint = new LatLng(1.436065, 103.786263);
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_CausewayPoint, 15));

//                Marker cp = map.addMarker(new
//                        MarkerOptions()
//                        .position(poi_CausewayPoint)
//                        .title("Causeway Point")
//                        .snippet("Shopping")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//
//                LatLng poi_RP = new LatLng(1.44224, 103.785733);
//                Marker rp = map.addMarker(new
//                        MarkerOptions()
//                        .position(poi_RP)
//                        .title("Republic Polytechnic")
//                        .snippet("School")
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
//
//                LatLng poi_Home = new LatLng(1.433485, 103.786354); // my house
//                Marker home = map.addMarker(new
//                        MarkerOptions()
//                        .position(poi_Home)
//                        .title("Home")
//                        .snippet("Home")
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));

//                homeCircle = map.addCircle(new CircleOptions()
//                        .center(poi_Home)
//                        .radius(50)
////                        .radius(Integer.parseInt(etRadius.getText().toString())) // get from database
//                        .strokeColor(Color.TRANSPARENT)
//                        .fillColor(Color.BLUE));

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        etLat.setText(latLng.latitude + "");
                        etLng.setText(latLng.longitude + "");

                        currentGeoFence = latLng;

                        if (currentCircle != null) {
                            currentCircle.remove();
                        }
                        currentCircle = map.addCircle(new CircleOptions()
                                .center(currentGeoFence)
                                .radius(Integer.parseInt(etRadius.getText().toString()))
                                .strokeColor(Color.RED)
                                .fillColor(Color.TRANSPARENT));
                    }
                });

            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double lat = Double.parseDouble(etLat.getText().toString());
                Double lng = Double.parseDouble(etLng.getText().toString());

                LatLng viewCircle = new LatLng(lat, lng);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(viewCircle, 15));

//                currentGeoFence = geofence;

                if (currentCircle != null) {
                    currentCircle.remove();
                }
                currentCircle = map.addCircle(new CircleOptions()
                        .center(viewCircle)
                        .radius(Integer.parseInt(etRadius.getText().toString()))
                        .strokeColor(Color.RED)
                        .fillColor(Color.TRANSPARENT));
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer elderlyId = Integer.parseInt(etElderlyId.getText().toString());
                Double lat = Double.parseDouble(etLat.getText().toString());
                Double lng = Double.parseDouble(etLng.getText().toString());
                Integer radius = Integer.parseInt(etRadius.getText().toString());

                requestParams.put("elderlyId", elderlyId);
                requestParams.put("Lat", lat);
                requestParams.put("Lon", lng);
                requestParams.put("radius", radius);

                asyncHttpClient.post(ADD_GEOFENCE_URL, requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            String result = response.getString("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                newCircle = map.addCircle(new CircleOptions()
                        .center(currentGeoFence)
                        .radius(Integer.parseInt(etRadius.getText().toString()))
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.TRANSPARENT));

            }
        });


    }
}