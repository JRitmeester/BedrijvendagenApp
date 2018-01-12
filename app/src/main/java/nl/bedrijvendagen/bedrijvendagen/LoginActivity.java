package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;


public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private TextView tvLostPassword;
    private Button bLogin;

    private final static String HEX = "0123456789ABCDEF";
    private String loginUrl = "https://www.bedrijvendagentwente.nl/auth/api/accounts/session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        setFont();
    }

    private void initViews() {
        // Initialise all necessary views on screen.
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvLostPassword = findViewById(R.id.tvLostPassword);
        bLogin = findViewById(R.id.bLogin);
    }

    private void initListeners() {
        // Add click function for button to submit login credentials.
        bLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread loginThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            login();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                loginThread.start();
            }
        });

        tvLostPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bedrijvendagentwente.nl/auth/front/accounts/lostPassword"));
                startActivity(browserIntent);
            }
        });
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

    public void login() {
        String email_ = etEmail.getText().toString();
        String password_ = etPassword.getText().toString();
        Map<String, Object> postData = new LinkedHashMap<>();
        postData.put("email", email_);
        postData.put("password", RequestHandler.SHA1(password_));

        HttpPostRequest task = new HttpPostRequest(postData);
        String response = "";
        try {
            response = task.execute(loginUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (task.responseCode == 200) {
            Log.d("LOGIN", "Response" + response);
            try {
                JSONObject jObj = new JSONObject(response);
                CompanyCredentials.auth = Boolean.valueOf(jObj.getString("auth"));
                CompanyCredentials.token = jObj.getString("_csrf");
                CompanyCredentials.company_id = Integer.parseInt(jObj.getString("id"));
                CompanyCredentials.email = jObj.getString("email");
                CompanyCredentials.company = jObj.getString("company_name");
                CompanyCredentials.password = RequestHandler.SHA1(password_);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("LOGIN", "auth: " + CompanyCredentials.auth + ", token: " + CompanyCredentials.token + ", id: " + CompanyCredentials.company_id);
            Log.d("LOGIN", "email: " + CompanyCredentials.email + ", company: " + CompanyCredentials.company);
            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_SHORT).show();
                }
            });
        }

//
//        if (RequestHandler.post("https://www.bedrijvendagentwente.nl/auth/api/accounts/session", params, true) == 200) {
//            Log.d("LOGIN", "Inside");
//            String response = RequestHandler.getResponse();
//            Log.d("LOGIN", "Response" + response);
//            try {
//                JSONObject jObj = new JSONObject(response);
//                auth = Boolean.valueOf(jObj.getString("auth"));
//                token = jObj.getString("_csrf");
//                company_id = Integer.parseInt(jObj.getString("id"));
//                email = jObj.getString("email");
//                company =, jObj.getString("company_name");
//                password = RequestHandler.SHA1(password_);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.d("LOGIN", "auth: " + auth + ", token: " + token + ", id: " + company_id);
//            Log.d("LOGIN", "email: " + email + ", company: " + company);
//            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(homeIntent);
//        } else {
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }
}
