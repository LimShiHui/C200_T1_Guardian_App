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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class ShowGeoFencing extends AppCompatActivity {

    private GoogleMap map;
    ListView lvGeoFences;
    Circle circle;
    ArrayList alGeoFence;
    ArrayAdapter<String> aaGeoFence;

    SharedPreferences prefs;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

//    String GEOFENCE_URL = "http://10.0.2.2/C200_guardian/getGeoFence.php";
    String GEOFENCE_URL = "http://C200Team1.myapplicationdev.com/getGeoFence.php";

    String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_geo_fencing);

        lvGeoFences = findViewById(R.id.lvGeofences);

        alGeoFence = new ArrayList();
        aaGeoFence = new ArrayAdapter<>(ShowGeoFencing.this, android.R.layout.simple_list_item_1, alGeoFence);
        lvGeoFences.setAdapter(aaGeoFence);

        asyncHttpClient = new AsyncHttpClient();
        requestParams = new RequestParams();

        prefs = getSharedPreferences("records", MODE_PRIVATE);
        Integer idRetrieved = prefs.getInt("elderlyId", 0);

//        Log.i("check", "id in ShowGeoFence: " + idRetrieved); // can delete

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

                int permissionCheck = ContextCompat.checkSelfPermission(ShowGeoFencing.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(ShowGeoFencing.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }

                requestParams.put("id", idRetrieved); // elderly Id

                asyncHttpClient.post(GEOFENCE_URL, requestParams, new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

                        Log.i("DEBUG", "fail to connect to web service" + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
//                            Log.i("DEBUG", "AL size: " + response.length());
                            JSONArray result = response.getJSONArray("record");

                            for (int i = 0; i < result.length(); i++) {

                                JSONObject obj = (JSONObject) result.get(i);

                                Double lat = Double.parseDouble(obj.getString("Lat"));
                                Double lng = Double.parseDouble(obj.getString("Lon"));
                                Integer radius = Integer.parseInt(obj.getString("radius"));

                                alGeoFence.add("Latitude: " + lat + "\nLongitude: " + lng + "\nRadius: " + radius);

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

            }
        });

        lvGeoFences.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                requestParams.put("id", idRetrieved); // elderly Id

                asyncHttpClient.get(GEOFENCE_URL, requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            JSONArray result = response.getJSONArray("record");
                                JSONObject obj = (JSONObject) result.get(position);

                                Double lat = Double.parseDouble(obj.getString("Lat"));
                                Double lng = Double.parseDouble(obj.getString("Lon"));
                                LatLng geofence = new LatLng(lat, lng);

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(geofence, 15));
                            aaGeoFence.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                });
            }
        });



    }
}