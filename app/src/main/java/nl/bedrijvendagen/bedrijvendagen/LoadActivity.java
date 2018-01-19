package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.firstName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.lastName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class LoadActivity extends AppCompatActivity {

    // TODO: Verify data flow: 1. if QR code is not scanned. 2. If QR code is scanned partially. 3. If QR code is complete.

    private String submitUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups";

    private ImageView loadImage;
    private Animation rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        initViews();
        sendEntry();

        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setFillAfter(true);
        loadImage.startAnimation(rotation);
    }

    private void initViews() {
        loadImage = findViewById(R.id.ivLoad);
    }

    private void sendEntry() {
        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("LOGIN", "Attempting to make StringRequest...");
        StringRequest submitRequest = new StringRequest(Request.Method.POST, submitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("LOGIN", response);
                JSONObject jObj = null;
                boolean valid;
                try {
                    jObj = new JSONObject(response);
                    valid = Boolean.valueOf(jObj.getString("valid"));
                    if (!valid) throw new Exception();
                } catch (Exception e) {
                    error(e);
                }
                try {
                    // Quite crude check; JSON response should contain a key named "created: <date> <time>". If this is not found, the response was not a successful one.
                    if (response.contains("created")) {
                        Intent confirmIntent = new Intent(LoadActivity.this, ConfirmationActivity.class);
                        startActivity(confirmIntent);
                        finish();
                    } else {
                        error();
                    }
                } catch (Exception e) {
                    error(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoadActivity.this, error.getMessage(), Toast.LENGTH_LONG);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new LinkedHashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("X-Requested-With", "XmlHttpRequest");
                headers.put("X-Csrf-Token", CompanyCredentials.token);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Object> params = new LinkedHashMap<>();
                if (userID == -1) {
                    params.put("email", StudentCredentials.email);
                } else {
                    params.put("account_id", StudentCredentials.userID);
                }
                params.put("comment", StudentCredentials.comment);
                params.put("name", firstName + " " + lastName);
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(submitRequest);
    }

    private void error(Exception... e) {
        Intent errorIntent = new Intent(this, ErrorActivity.class);
        startActivity(errorIntent);
        if (e.length > 0) {
            Log.e("LOAD", e[0].getMessage());
        }
        finish();
    }
}
