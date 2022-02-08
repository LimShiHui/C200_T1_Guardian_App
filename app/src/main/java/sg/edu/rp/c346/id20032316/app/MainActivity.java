package sg.edu.rp.c346.id20032316.app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    EditText etContactNum, etPassword;
    Button btnLogin;
    TextView tvRegister, tvResult;

    SharedPreferences prefs;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

//    String GET_USER_URL = "http://10.0.2.2/C200_guardian/getGuardianByContact.php";
    String GET_USER_URL = "http://C200Team1.myapplicationdev.com/getGuardianByContact.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etContactNum = findViewById(R.id.etContactNum);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvResult = findViewById(R.id.tvResult);

        asyncHttpClient = new AsyncHttpClient();
        requestParams = new RequestParams();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contactNum = etContactNum.getText().toString();
                String password = etPassword.getText().toString();

                requestParams.put("contactNum", contactNum);
                requestParams.put("password", password);

                tvResult.setVisibility(TextView.INVISIBLE);


                asyncHttpClient.get(GET_USER_URL, requestParams, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {

                            String msg = response.getString("ErrorMessage");

                            if (msg.equals("")) {
                                Log.i("login","onSuccess: Success");
                                JSONArray rows = response.getJSONArray("record");
                                JSONObject record = rows.getJSONObject(0);
                                String guardianId = record.getString("guardianId");
                                Log.i("check", "Guardian Id: " + guardianId);

                                JSONArray elderly = record.getJSONArray("elderly");

                                JSONObject elderlyId = elderly.getJSONObject(0);
                                Integer id = elderlyId.getInt("id");
                                Log.i("check", "elderly id: " + id);

                                // for the used in other activity
                                prefs = getSharedPreferences("records", 0);
                                SharedPreferences.Editor editor = prefs.edit();

                                editor.putInt("elderlyId", id);
                                editor.commit(); // commit changes

                                // can delete
                                Integer idRetrieved = prefs.getInt("elderlyId", 0);
                                Log.i("check", "id in MainActivity: " + idRetrieved);
                                // delete till here

                                Intent intent = new Intent(MainActivity.this, Menu.class);
                                startActivity(intent);
                            } else {
                                etPassword.setError("invalid");
                                Log.i("login","onSuccess: invalid");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

    }
}