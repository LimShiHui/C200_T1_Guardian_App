package sg.edu.rp.c346.id20032316.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Register extends AppCompatActivity {

    EditText etName, etContact, etPassword, etEmail;
    RadioGroup rgGender;
    DatePicker dpBirthdate;
    Button btnRegister;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

//    String ADD_USER_URL = "http://10.0.2.2/C200_guardian/addGuardian.php";
    String ADD_USER_URL = "https://C200Team1.myapplicationdev.com/addGuardian.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        dpBirthdate = findViewById(R.id.dpBirthdate);
        etContact = findViewById(R.id.etContact);
        rgGender = findViewById(R.id.rgGender);
        btnRegister = findViewById(R.id.btnRegister);

        asyncHttpClient = new AsyncHttpClient();
        requestParams = new RequestParams();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                int day = dpBirthdate.getDayOfMonth();
                int month = dpBirthdate.getMonth() + 1; // it starts with 0
                int year = dpBirthdate.getYear();
                String birthdate = year+ "-" + month + "-" + day;
                String contact = etContact.getText().toString();
                String gender = "";
                int radioId = rgGender.getCheckedRadioButtonId();
                if (radioId == R.id.rbMale) {
                    gender = "M";
                } else if (radioId == R.id.rbFemale) {
                    gender = "F";
                }

                requestParams.put("guardianName", name);
                requestParams.put("email", email);
                requestParams.put("password", password);
                requestParams.put("DOB", birthdate);
                requestParams.put("contactNum", contact);
                requestParams.put("Gender", gender);

                asyncHttpClient.post(ADD_USER_URL, requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

                        Log.i("DEBUG", "fail to connect to web service" + responseString);
                    }

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

                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}