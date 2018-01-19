package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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

import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.auth;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.company;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.company_id;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.password;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.token;
import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;


public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private TextView tvLostPassword;
    private ImageView ivLogo;
    private Button bLogin;
    private View filler;

    private String loginUrl = "https://www.bedrijvendagentwente.nl/auth/api/accounts/session";
    private String lostPasswordUrl = "https://www.bedrijvendagentwente.nl/auth/front/accounts/lostPassword";

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        setFont();

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
                    } else {
                        ivLogo.setVisibility(View.VISIBLE);
                        filler.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvLostPassword = findViewById(R.id.tvLostPassword);
        bLogin = findViewById(R.id.bLogin);
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

    private void login() {
        queue = Volley.newRequestQueue(this);

        final String email_ = etEmail.getText().toString();
        final String password_ = etPassword.getText().toString();

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
                        token = jObj.getString("_csrf");
                        auth = true;
                        company_id = Integer.parseInt(jObj.getString("id"));
                        company = jObj.getString("company_name");
                        email = jObj.getString("email");
                        password = SHA1.hash(password_);
                        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.e("LOGIN", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG);
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
                params.put("email", email_);
                params.put("password", SHA1.hash(password_));
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
