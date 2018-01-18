package nl.bedrijvendagen.bedrijvendagen;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.auth;
import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;

public class HomeActivity extends AppCompatActivity {

    private String countUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups/count";
    private String namesUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups";
    private String deleteURL = "https://www.bedrijvendagentwente.nl/auth/api/accounts/session";
    private RequestQueue queue;

    private TextView tvLogout;
    private TextView tvCompany;
    private TextView tvRecently;
    private TextView tvTotal;
    private Button bScanQR;

    private TextView tvRecent1;
    private TextView tvRecent2;
    private TextView tvRecent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initListeners();
        setFont();

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Pull the last three scanned visitors and set company name in top left.
        // Also gets called when the activity is first launched.
        updateNames();
        updateCount();
    }

    private void initViews() {
        tvLogout = findViewById(R.id.tvLogout);
        tvCompany = findViewById(R.id.tvCompany);
        tvRecently = findViewById(R.id.tvRecently);
        tvTotal = findViewById(R.id.tvTotalScans);
        bScanQR = findViewById(R.id.bScan);

        tvRecent1 = findViewById(R.id.tvRecent1);
        tvRecent2 = findViewById(R.id.tvRecent2);
        tvRecent3 = findViewById(R.id.tvRecent3);

        tvCompany.setText(CompanyCredentials.company);
    }

    private void initListeners() {
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        bScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanIntent = new Intent(HomeActivity.this, ScannerActivity.class);
                startActivity(scanIntent);
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvLogout);
        setTypeface(this, tvCompany);
        setTypeface(this, tvRecently);
        setTypeface(this, tvTotal);
        setTypeface(this, bScanQR);
        setTypeface(this, tvRecent1);
        setTypeface(this, tvRecent2);
        setTypeface(this, tvRecent3);

        tvLogout.setPaintFlags(tvLogout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void logout() {
        StringRequest logoutRequest = new StringRequest(Request.Method.DELETE, deleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("LOGOUT", response);
                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);
                    auth = Boolean.valueOf(jObj.getString("auth"));
                    if (!auth) {
                        CompanyCredentials.reset();
                        // Finish all activities and return to the login screen.
                        // This makes it not possible to use the "back" button to go from the login screen to the home (or any other) screen.
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        ComponentName cn = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(cn);
                        startActivity(mainIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG);
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("X-Requested-With", "XmlHttpRequest");
                headers.put("X-Csrf-Token", CompanyCredentials.token);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(logoutRequest);
    }

    private void updateNames() {
        StringRequest nameRequest = new StringRequest(Request.Method.GET, namesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("NAMES", response);
                JSONArray jArr;
                try {
                    jArr = new JSONArray(response);

                    String[] names = new String[3];
                    for (int i = 0; i < names.length; i++) {
                        names[i] = jArr.getJSONObject(i).getString(("name"));
                        names[i] = names[i].equals("null") ? "Manual entry" : names[i];
                    }
                    Log.d("NAMES (PARSED)", names[0] + ", " + names[1] + ", " + names[2]);
                    tvRecent1.setText(names[0]);
                    tvRecent2.setText(names[1]);
                    tvRecent3.setText(names[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG);
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("X-Requested-With", "XmlHttpRequest");
                headers.put("X-Csrf-Token", CompanyCredentials.token);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(nameRequest);

    }

    private void updateCount() {
        StringRequest countRequest = new StringRequest(Request.Method.GET, countUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("COUNT", response);
                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);
                    String countTitle = "Total scanned: ";
                    countTitle += jObj.getString("count");
                    tvTotal.setText(countTitle);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG);
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("X-Requested-With", "XmlHttpRequest");
                headers.put("X-Csrf-Token", CompanyCredentials.token);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(countRequest);

    }
}
