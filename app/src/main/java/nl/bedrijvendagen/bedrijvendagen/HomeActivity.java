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

    private int count;

    private TextView tvRecent3;
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
        updateNames();
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
                Intent scanIntent = new Intent(HomeActivity.this, ScannerActivity.class);
                startActivity(scanIntent);
            }
        });

        tvRecent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Open comment view with name. Parse ID to comment activity I guess
                Intent commentIntent = new Intent(HomeActivity.this, CommentActivity.class);
//                commentIntent.putExtra("name", names[0]);
//                commentIntent.putExtra("comment", comments[0]);
//                commentIntent.putExtra("id", ids[0]);
                commentIntent.putExtra("isOverwriting", true);
                StudentCredentials.firstName = names[0];
                StudentCredentials.comment = comments[0];
                StudentCredentials.userID = Integer.parseInt(ids[0]);
                startActivity(commentIntent);
            }
        });
        tvRecent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Open comment view with name. Parse ID to comment activity I guess
                Intent commentIntent = new Intent(HomeActivity.this, CommentActivity.class);
//                commentIntent.putExtra("name", names[1]);
//                commentIntent.putExtra("comment", comments[1]);
//                commentIntent.putExtra("id", ids[1]);
                StudentCredentials.firstName = names[1];
                StudentCredentials.comment = comments[1];
                StudentCredentials.userID = Integer.parseInt(ids[1]);
                commentIntent.putExtra("isOverwriting", true);
                startActivity(commentIntent);
            }
        });
        tvRecent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Open comment view with name. Parse ID to comment activity I guess
                Intent commentIntent = new Intent(HomeActivity.this, CommentActivity.class);
//                commentIntent.putExtra("name", names[2]);
//                commentIntent.putExtra("comment", comments[2]);
//                commentIntent.putExtra("id", ids[2]);
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
                        names[i] = jArr.getJSONObject(i).getString("name");
                        Log.d("NAMES", "Found name and stored " + names[i]);
                        ids[i] = jArr.getJSONObject(i).getString("id");
                        comments[i] = jArr.getJSONObject(i).getString("comments");
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
                    countTitle += count;
                    tvTotal.setText(countTitle);
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
