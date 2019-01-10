package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.comment;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.firstName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.lastName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;
import static nl.bedrijvendagen.bedrijvendagen.ToastWrapper.createToast;

public class LoadActivity extends AppCompatActivity {

    // TODO: Verify data flow: 1. if QR code is not scanned. 2. If QR code is scanned partially. 3. If QR code is complete.

    private String submitUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups/";

    private ImageView loadImage;
    private Animation rotation;
    private CountDownTimer countdown;

    private boolean overwriting;
    private boolean hasFailedBefore;
    private boolean isComplete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        initViews();

        Intent intent = getIntent();
        if (intent.hasExtra("overwriting")) {
            overwriting = getIntent().getExtras().getBoolean("overwriting");
        }

        hasFailedBefore = intent.getExtras().getBoolean("hasFailedBefore");

        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setFillAfter(true);
        loadImage.startAnimation(rotation);

        if (overwriting) {
            overwrite();
        } else {
            sendEntry();
        }
    }

    @Override
    public void onResume() {
        countdown = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (!isComplete) goToError();
            }
        }.start();
        super.onResume();
    }

    private void initViews() {
        loadImage = findViewById(R.id.ivLoad);
    }

    private void sendEntry() {
        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("UPLOAD", "Attempting to submit new entry.");
        StringRequest submitRequest = new StringRequest(Request.Method.POST, submitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Stop waiting for the response to come through... because, well... it's here.

                Log.d("LOGIN", response);
                JSONObject jObj = null;

                try {
                    jObj = new JSONObject(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("SUBMIT", e.getMessage());
                    goToError();

                }
                try {
                    // Quite crude check; JSON response should contain a key named "created: <date> <time>". If this is not found, the response was not a successful one.
                        countdown.cancel();
                        StudentCredentials.reset();
                        Intent confirmIntent = new Intent(LoadActivity.this, ConfirmationActivity.class);
                        startActivity(confirmIntent);
                        finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    goToError();
                    Log.d("SUBMIT", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createToast("HTTP Error code: " + String.valueOf(error.networkResponse.statusCode), Toast.LENGTH_LONG,LoadActivity.this);
                Log.d("SUBMIT", error.getMessage());
                goToError();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new LinkedHashMap<>();
                headers.put("X-Requested-With", "XmlHttpRequest");
                headers.put("X-Csrf-Token", CompanyCredentials.token);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.d("Email", StudentCredentials.email);
                Log.d("Comment", StudentCredentials.comment);
                Log.d("ID", "" + userID);

                Map<String, Object> params = new LinkedHashMap<>();

                if (!email.equals("default")) {
                    params.put("email", StudentCredentials.email);
                }

                if (userID != -1) {
                    params.put("account_id", StudentCredentials.userID);
                }
                params.put("name", firstName + " " + lastName);
                params.put("comments", comment);
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(submitRequest);
    }

    private void overwrite() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String overwriteUrl = submitUrl + userID;
        Log.d("OVERWRITE", "Attempting to overwrite...");
        StringRequest submitRequest = new StringRequest(Request.Method.PUT, overwriteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                countdown.cancel();
                Log.d("OVERWRITE RESPONSE", response);
                JSONObject jObj = null;
                boolean success = false;
                try {
                    jObj = new JSONObject(response);
                    //TODO: FIND BETTER WAY TO EVALUATE IF IT WAS A SUCCESS.
//                    success = comment.equals(jObj.getString("comments"));
//                    if (!success) {
//                        throw new Exception("");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    goToError();
                    Log.d("OVERWRITE 1", e.getMessage());
                }
                try {
                        StudentCredentials.reset();
                        isComplete = true;
                        Intent confirmIntent = new Intent(LoadActivity.this, ConfirmationActivity.class);
                        startActivity(confirmIntent);
                        finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    goToError();
                    Log.d("OVERWRITE 2", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createToast("HTTP Error code: " + String.valueOf(error.networkResponse.statusCode), Toast.LENGTH_LONG,LoadActivity.this);
                goToError();
                Log.d("OVERWRITE VOLLEY", error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new LinkedHashMap<>();
                headers.put("X-Requested-With", "XmlHttpRequest");
                headers.put("X-Csrf-Token", CompanyCredentials.token);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("comments", comment);
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(submitRequest);
    }

    private void goToError() {
        Intent errorIntent;
        if (!hasFailedBefore) {
            errorIntent = new Intent(this, ErrorActivity.class);
        }
        else {
            errorIntent = new Intent(this, SecondErrorActivity.class);
        }
        errorIntent.putExtra("overwriting", overwriting);
        startActivity(errorIntent);
        finish();
    }

}
