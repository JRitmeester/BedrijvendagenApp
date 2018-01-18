package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class LoadActivity extends AppCompatActivity {

    // TODO: Verify data flow: 1. if QR code is not scanned. 2. If QR code is scanned partially. 3. If QR code is complete.

    private String submitUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups";

    private ImageView loadImage;
    private Animation rotation;
    private int timeoutLength = 10000;

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
                try {
                    jObj = new JSONObject(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }
                try {
                    if (!isNetworkAvailable()) throw new Exception();
                    // Quite crude check; JSON response should contain a key named "created: <date> <time>". If this is not found, the response was not a successful one.
                    if (response.contains("created")) {
                        Intent confirmIntent = new Intent(LoadActivity.this, ConfirmationActivity.class);
                        startActivity(confirmIntent);
                    } else {
                        Intent errorIntent = new Intent(LoadActivity.this, ErrorActivity.class);
                        startActivity(errorIntent);
                    }
                    finish();
                } catch (Exception e) {
                    Intent errorIntent = new Intent(LoadActivity.this, ErrorActivity.class);
                    startActivity(errorIntent);
                    e.printStackTrace();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoadActivity.this, error.getMessage(), Toast.LENGTH_LONG);
                Intent errorIntent = new Intent(LoadActivity.this, ErrorActivity.class);
                startActivity(errorIntent);
                finish();
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
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        submitRequest.setRetryPolicy(new DefaultRetryPolicy(timeoutLength, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(submitRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
