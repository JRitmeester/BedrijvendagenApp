package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.ToastWrapper.createToast;


public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private TextView tvLostPassword;
    private ImageView ivLogo;
    private Button bLogin;
    private View filler;
    private TextView tvLogin;
    private String loginUrl = "https://www.bedrijvendagentwente.nl/auth/api/accounts/session/";
    private String lostPasswordUrl = "https://bedrijvendagentwente.nl/auth/front/accounts/lostPassword/";

    private RequestQueue queue;
    private boolean loginIsCached = false;

    private String cachedUsername;
    private String cachedPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        setFont();
        initLogoVisibilityChange();

        // Store the login credentials (hashed pasword).
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
//        cachedUsername = sp.getString("USERNAME", "");
//        cachedPassword = sp.getString("PASSWORD", "");
//        if (!cachedUsername.equals("") && !cachedPassword.equals("")) {
//            loginIsCached = true;
//            etEmail.setText(cachedUsername);
//            etPassword.setText(cachedPassword);
//        }
        etEmail.setText(CompanyCredentials.getUsername(LoginActivity.this));
        etPassword.setText(CompanyCredentials.getPassword(LoginActivity.this));
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvLostPassword = findViewById(R.id.tvLostPassword);
        bLogin = findViewById(R.id.bLogin);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void initListeners() {
        filler = findViewById(R.id.filler);
        ivLogo = findViewById(R.id.ivBDLogo);
        bLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        tvLostPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lostPasswordUrl));
                startActivity(browserIntent);
            }
        });
    }

    private void initLogoVisibilityChange() {
        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
                int screenHeight = findViewById(android.R.id.content).getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // If keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d("KEYBOARD", "keypadHeight = " + keypadHeight);

                try {
                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        ivLogo.setVisibility(View.GONE);
                        filler.setVisibility(View.VISIBLE);
                        Log.d("LOGO", "Invisible");

                    } else {
                        ivLogo.setVisibility(View.VISIBLE);
                        Log.d("LOGO", "Visible");
                        filler.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        tvLogin.setText("LOG IN HERE");
        super.onBackPressed();
    }

    private void login() {
        queue = Volley.newRequestQueue(this);

        StringRequest loginRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("LOGIN", response);

                // First check if "auth" came back true (successful login).
                boolean authorised = false;
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(response);
                    authorised = Boolean.valueOf(jObj.getString("auth"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }

                // Proceed and save "auth" if true.
                if (authorised) {
                    Log.d("LOGIN", "Authorised. Processing response...");
                    try {
                        CompanyCredentials.token = jObj.getString("_csrf");
                        CompanyCredentials.company_id = Integer.parseInt(jObj.getString("id"));
                        CompanyCredentials.company = jObj.getString("company_name");
                        CompanyCredentials.email = jObj.getString("email");

                        Log.d("CURRENTLY", etPassword.getText().toString());
                        SharedPreferences settings = getSharedPreferences("Login", MODE_PRIVATE);
                        settings.edit()
                                .putString("USERNAME", email)
                                .putString("PASSWORD", etPassword.getText().toString())
                                .apply();

                        Intent standardCommentIntent = new Intent(LoginActivity.this, StandardCommentActivity.class);
                        startActivity(standardCommentIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.e("LOGIN", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_LONG).show();
                createToast("Invalid login", Toast.LENGTH_LONG, LoginActivity.this);
                tvLogin.setText("INVALID LOGIN");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new LinkedHashMap<>();
//              headers.put("Content-Type", "application/json");
                headers.put("X-Requested-With", "XmlHttpRequest");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Object> params = new LinkedHashMap<>();
                if (loginIsCached) {
                    Log.d("Cache","Login is cached.");
                    params.put("email", CompanyCredentials.getUsername(LoginActivity.this));
                    params.put("password", CompanyCredentials.getPassword(LoginActivity.this));
                } else {
                    Log.d("Cache","Login is NOT cached.");
                    params.put("email", etEmail.getText().toString());
                    params.put("password", SHA1.hash(etPassword.getText().toString()));
                }
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(loginRequest);
    }


    private void setFont() {
        setTypeface(this, etEmail);
        setTypeface(this, etPassword);
        setTypeface(this, tvLostPassword);
        //TODO: Remove footer.
        setTypeface(this, bLogin);

        // Set underline for tvLostPassword
        tvLostPassword.setPaintFlags(tvLostPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


    // Hide keyboard when touched outside the fields.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View[] exceptions = {etEmail, etPassword, bLogin};
            Rect[] exceptionRects = new Rect[exceptions.length];
            for (int i = 0; i < exceptions.length; i++) {
                exceptionRects[i] = new Rect();
                exceptions[i].getGlobalVisibleRect(exceptionRects[i]);
            }

            View currentView = getCurrentFocus();
            boolean exceptionFound = false;

            for (Rect e : exceptionRects) {
                // See if the touch was inside the text fields.
                if (e.contains((int) event.getRawX(), (int) event.getRawY())) {
                    exceptionFound = true;
                    break;
                }
            }
            // If not...
            if (!exceptionFound) {
                if (currentView != null) {
                    currentView.clearFocus();
                }
                // ... hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    try {
                        imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                        tvLogin.setText("LOG IN HERE");
                    } catch (NullPointerException e) {
                        // Do nothing, stop breaking!
                        Log.e("No focus", "No focus points were detecte so things would've broken for some reason. Everything is fine though...kjj.");
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
