package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class ErrorActivity extends AppCompatActivity {

    private Button bTryAgain;
    private String comment;
    private String submitUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

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
                retry();
            }
        });
    }

    private void setFont() {
        setTypeface(this, bTryAgain);
    }

    private void retry() {
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
                    // Quite crude check; JSON response should contain a key named "created: <date> <time>". If this is not found, the response was not a successful one.
                    if (response.contains("created")) {
                        Intent confirmIntent = new Intent(ErrorActivity.this, ConfirmationActivity.class);
                        startActivity(confirmIntent);
                    } else {
                        Intent errorIntent = new Intent(ErrorActivity.this, ErrorActivity.class);
                        startActivity(errorIntent);
                    }
                    finish();
                } catch (Exception e) {
                    Intent errorIntent = new Intent(ErrorActivity.this, ErrorActivity.class);
                    startActivity(errorIntent);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ErrorActivity.this, error.getMessage(), Toast.LENGTH_LONG);
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
        queue.add(submitRequest);
    }
}
