package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;

public class HomeActivity extends AppCompatActivity {

    private String countUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups";
    private String namesUrl = "https://www.bedrijvendagentwente.nl/companies/api/student_signups"; // "http://requestbin.fullcontact.com/1j4b8jh1";

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Pull the last three scanned visitors and set company name in top left.
        // Also gets called when the activity is first launched.
        Thread nameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (RequestHandler.checkToken()) {
                        updateNames();
                        updateCount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        nameThread.start();
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

//                Toast.makeText(HomeActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                Thread logoutThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (RequestHandler.checkToken()) {
                                logout();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                logoutThread.start();
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
        // TODO: Set the scanned amount.
        setTypeface(this, bScanQR);
        setTypeface(this, tvRecent1);
        setTypeface(this, tvRecent2);
        setTypeface(this, tvRecent3);

        tvLogout.setPaintFlags(tvLogout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void logout() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
//        if (RequestHandler.delete("https://www.bedrijvendagentwente.nl/auth/api/accounts/session") == 200) {
//            String response = RequestHandler.response;
//            try {
//                JSONObject jObj = new JSONObject(response);
//                auth = Boolean.valueOf(jObj.getString("auth"));
//                CompanyCredentials.token = jObj.getString("_csrf");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.d("LOGOUT", "auth: " + auth + ", token: " + CompanyCredentials.token);
//            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
//            startActivity(loginIntent);
//            finish();
//        } else {
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    Toast.makeText(HomeActivity.this, "Couldn't log out.", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }
    }

    private void updateNames() {
        Log.d("NAMES", "Trying to refresh names...");
        try {
            HttpGetRequest task = new HttpGetRequest();
            String response = "";
            try {
                response = task.execute(namesUrl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (task.responseCode == 200) {
                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);
                    JSONArray lastThreeScans = jObj.getJSONArray(""); //TODO: Verify this name.
                    tvRecent1.setText(lastThreeScans.getJSONObject(0).getString("name"));
                    tvRecent2.setText(lastThreeScans.getJSONObject(1).getString("name"));
                    tvRecent3.setText(lastThreeScans.getJSONObject(2).getString("name"));
                    Log.d("FETCH NAMES", tvRecent1.getText().toString() + ", " + tvRecent2.getText().toString() + ", " + tvRecent3.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Log.e("FETCH NAMES", "No response.");
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(HomeActivity.this, "Couldn't refresh names.", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void updateCount() {
        Log.d("COUNT", "Trying to update count...");
        HttpGetRequest task = new HttpGetRequest();
        String response = "";
        try {
            response = task.execute(countUrl).get();
            // https://stackoverflow.com/questions/17136769/how-to-parse-jsonarray-in-android
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(response);
                tvRecently.setText(jObj.getString("count"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e("FETCH COUNT", "No response.");
            }

        } catch (Exception e2) {
            e2.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(HomeActivity.this, "Couldn't refresh count.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
