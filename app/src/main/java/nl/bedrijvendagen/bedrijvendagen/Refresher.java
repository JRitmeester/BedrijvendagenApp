package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

import static android.content.Context.MODE_PRIVATE;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.auth;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.company;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.company_id;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.password;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.token;

public class Refresher {

    // Added in an attempt to prevent the logout after X minutes without being used.

    private static RequestQueue queue_;
    private static String update_url = "https://www.bedrijvendagentwente.nl/auth/api/accounts/session/";

    public static void refresh(final Context c) {

        queue_ = Volley.newRequestQueue(c);
        Log.d("REFHRESHING", "Refreshing session...");
        StringRequest loginRequest = new StringRequest(Request.Method.PUT, update_url, new Response.Listener<String>() {

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
                    Log.d("REFRESHER", "Refreshed current session");
                    try {
                        token = jObj.getString("_csrf");
                        company_id = Integer.parseInt(jObj.getString("id"));
                        company = jObj.getString("company_name");
                        email = jObj.getString("email");
//                        password = SHA1.hash(password_);
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
                params.put("email", CompanyCredentials.email);
                params.put("password", CompanyCredentials.getPassword(c));
                params.put("hash_password", false);
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue_.add(loginRequest);
    }

}
