package nl.bedrijvendagen.bedrijvendagen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.samples.vision.barcodereader.MainActivity;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.auth;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.company;
import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.ToastWrapper.createToast;

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
    private TextView tvChangeComments;

    private TextView tvRecent1;
    private TextView tvRecent2;
    private TextView tvRecent3;

    private RelativeLayout rlWrapperRecent1;
    private RelativeLayout rlWrapperRecent2;
    private RelativeLayout rlWrapperRecent3;

    private int count;

    private String[] names = new String[3];
    private String[] ids = new String[3];
    private String[] comments = new String[3];

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
//        Refresher.refresh(this);
        updateCount();
//        updateNames();
        StudentCredentials.reset();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initViews() {
        tvLogout = findViewById(R.id.tvLogout);
        tvCompany = findViewById(R.id.tvCompany);
        tvRecently = findViewById(R.id.tvRecent);
        tvTotal = findViewById(R.id.tvTotalScans);
        bScanQR = findViewById(R.id.bScan);

        tvRecent1 = findViewById(R.id.tvRecent1);
        tvRecent2 = findViewById(R.id.tvRecent2);
        tvRecent3 = findViewById(R.id.tvRecent3);

        rlWrapperRecent1 = findViewById(R.id.rlWrapperRecent1);
        rlWrapperRecent2 = findViewById(R.id.rlWrapperRecent2);
        rlWrapperRecent3 = findViewById(R.id.rlWrapperRecent3);

        tvChangeComments = findViewById(R.id.tvChangeComments);

        Log.d("SETTING COMPANY", company);
        tvCompany.setText(CompanyCredentials.company);

        // Set underline for tvChangeComments
        tvChangeComments.setPaintFlags(tvChangeComments.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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
//                Intent scanIntent = new Intent(HomeActivity.this, ScannerActivity.class);
                Intent scanIntent = new Intent(HomeActivity.this, BarcodeCaptureActivity.class);
                startActivity(scanIntent);
            }
        });

        rlWrapperRecent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(HomeActivity.this, CommentActivity.class);
                commentIntent.putExtra("isOverwriting", true);
                StudentCredentials.firstName = names[0];
                StudentCredentials.comment = comments[0];
                StudentCredentials.userID = Integer.parseInt(ids[0]);
                startActivity(commentIntent);
            }
        });

        rlWrapperRecent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(HomeActivity.this, CommentActivity.class);
                StudentCredentials.firstName = names[1];
                StudentCredentials.comment = comments[1];
                StudentCredentials.userID = Integer.parseInt(ids[1]);
                commentIntent.putExtra("isOverwriting", true);
                startActivity(commentIntent);
            }
        });

        rlWrapperRecent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(HomeActivity.this, CommentActivity.class);
                StudentCredentials.firstName = names[2];
                StudentCredentials.comment = comments[2];
                StudentCredentials.userID = Integer.parseInt(ids[2]);
                commentIntent.putExtra("isOverwriting", true);

                startActivity(commentIntent);
            }
        });

        tvChangeComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeCommentsIntent = new Intent(HomeActivity.this, StandardCommentActivity.class);
                startActivity(changeCommentsIntent);

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
                        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createToast(error.getMessage(), Toast.LENGTH_LONG, HomeActivity.this);
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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
        Log.d("NAMES", "Updating names...");
        StringRequest nameRequest = new StringRequest(Request.Method.GET, namesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);
                JSONArray jArr;
                try {
                    jArr = new JSONArray(response);

                    //TODO: Test global instance of names (String[]).
//                    String[] names = new String[3];
                    for (int i = 0; i < Math.min(count, 3); i++) {
                        try {
                            names[i] = jArr.getJSONObject(i).getString("name");
                            Log.d("NAMES", "Found name and stored " + names[i]);
                            ids[i] = jArr.getJSONObject(i).getString("id");
                            comments[i] = jArr.getJSONObject(i).getString("comments");
                        } catch (JSONException e) {
                            Log.e("PARSING NAMES", e.getMessage());
                        }
                    }

                    Log.d("NAMES (PARSED)", names[0] + ", " + names[1] + ", " + names[2]);
                    if (names[0] != null) tvRecent1.setText(names[0]);
                    if (names[1] != null) tvRecent2.setText(names[1]);
                    if (names[2] != null) tvRecent3.setText(names[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createToast(error.getMessage(), Toast.LENGTH_LONG, HomeActivity.this);
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
                    count = Integer.parseInt(jObj.getString("count"));
                    Log.d("COUNT!!!", jObj.getString("count"));
                    countTitle += count;
                    tvTotal.setText(countTitle);

                    updateNames();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createToast(error.getMessage(), Toast.LENGTH_LONG, HomeActivity.this);
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
