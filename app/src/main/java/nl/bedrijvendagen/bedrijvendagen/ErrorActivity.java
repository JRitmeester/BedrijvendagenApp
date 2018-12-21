package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class ErrorActivity extends AppCompatActivity {

    private Button bTryAgain;
    private String submitUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups/";
    private int timeoutLength = 10000;
    private boolean overwriting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        overwriting = getIntent().getExtras().getBoolean("overwriting");

        initViews();
        initListeners();
        setFont();
    }

    private void initViews() {
        bTryAgain = findViewById(R.id.bTryAgain);
    }

    private void initListeners() {
        bTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retryIntent = new Intent(ErrorActivity.this, LoadActivity.class);
                retryIntent.putExtra("hasFailedBefore", true);
                retryIntent.putExtra("overwriting", overwriting);
                startActivity(retryIntent);
                finish();
            }
        });
    }

    private void setFont() {
        setTypeface(this, bTryAgain);
    }

//    private void retrySending() {
//        Toast.makeText(this, "Retrying to submit...", Toast.LENGTH_LONG).show();
//        try {
//            if (!isNetworkAvailable()) throw new Exception();
//            RequestQueue queue = Volley.newRequestQueue(this);
//
//            Log.d("LOGIN", "Attempting to make StringRequest...");
//            StringRequest submitRequest = new StringRequest(Request.Method.POST, submitUrl, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d("LOGIN", response);
//                    JSONObject jObj = null;
//                    try {
//                        jObj = new JSONObject(response);
//                    } catch (Exception e) {
//                        Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                        startActivity(errorIntent);
//                        e.printStackTrace();
//                        finish();
//                    }
//                    try {
//
//                        // Quite crude check; JSON response should contain a key named "created: <date> <time>". If this is not found, the response was not a successful one.
//                        if (response.contains("created")) {
//                            Intent confirmIntent = new Intent(ErrorActivity.this, ConfirmationActivity.class);
//                            startActivity(confirmIntent);
//                        } else {
//                            Intent secondErrorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                            startActivity(secondErrorIntent);
//                        }
//                        finish();
//                    } catch (Exception e) {
//                        Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                        startActivity(errorIntent);
//                        e.printStackTrace();
//                        finish();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(ErrorActivity.this, error.getMessage(), Toast.LENGTH_LONG);
//                    Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                    startActivity(errorIntent);
//                    finish();
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new LinkedHashMap<>();
//                    headers.put("X-Requested-With", "XmlHttpRequest");
//                    headers.put("X-Csrf-Token", CompanyCredentials.token);
//                    return headers;
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    Map<String, Object> params = new LinkedHashMap<>();
//                    if (userID == -1) {
//                        params.put("email", StudentCredentials.email);
//                    } else {
//                        params.put("account_id", StudentCredentials.userID);
//                    }
//                    params.put("comment", StudentCredentials.comment);
//                    return new JSONObject(params).toString().getBytes();
//                }
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/json";
//                }
//            };
//
//            submitRequest.setRetryPolicy(new DefaultRetryPolicy(timeoutLength, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            queue.add(submitRequest);
//        } catch (Exception e) {
//            Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//            startActivity(errorIntent);
//            e.printStackTrace();
//            finish();
//        }
//    }

//    private void retryOverwriting() {
////        Toast.makeText(this, "Retrying to overwrite...", Toast.LENGTH_LONG).show();
//        try {
//            if (!isNetworkAvailable()) throw new Exception();
//            RequestQueue queue = Volley.newRequestQueue(this);
//
//            String overwriteUrl = submitUrl + userID;
//
//            Log.d("LOGIN", "Retrying to overwrite...");
//            StringRequest submitRequest = new StringRequest(Request.Method.PUT, overwriteUrl, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d("LOGIN", response);
//                    JSONObject jObj = null;
//                    try {
//                        jObj = new JSONObject(response);
//                    } catch (Exception e) {
//                        Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                        startActivity(errorIntent);
//                        e.printStackTrace();
//                        finish();
//                    }
//                    try {
//                        // TODO: Checken of het is gelukt
//                        // Quite crude check; JSON response should contain a key named "created: <date> <time>". If this is not found, the response was not a successful one.
//                        if (response.contains("created")) {
//                            Intent confirmIntent = new Intent(ErrorActivity.this, ConfirmationActivity.class);
//                            startActivity(confirmIntent);
//                        } else {
//                            Intent secondErrorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                            startActivity(secondErrorIntent);
//                        }
//                        finish();
//                    } catch (Exception e) {
//                        Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                        startActivity(errorIntent);
//                        e.printStackTrace();
//                        finish();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(ErrorActivity.this, error.getMessage(), Toast.LENGTH_LONG);
//                    Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//                    startActivity(errorIntent);
//                    finish();
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new LinkedHashMap<>();
//                    headers.put("X-Requested-With", "XmlHttpRequest");
//                    headers.put("X-Csrf-Token", CompanyCredentials.token);
//                    return headers;
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    Map<String, Object> params = new LinkedHashMap<>();
//                    params.put("comment", StudentCredentials.comment);
//                    return new JSONObject(params).toString().getBytes();
//                }
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/json";
//                }
//            };
//            submitRequest.setRetryPolicy(new DefaultRetryPolicy(timeoutLength, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            queue.add(submitRequest);
//        } catch (Exception e) {
//            Intent errorIntent = new Intent(ErrorActivity.this, SecondErrorActivity.class);
//            startActivity(errorIntent);
//            e.printStackTrace();
//            finish();
//        }
//    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
