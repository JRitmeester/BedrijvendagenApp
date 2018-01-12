package nl.bedrijvendagen.bedrijvendagen;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.auth;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.CompanyCredentials.password;

public class RequestHandler {

    private final static String HEX = "0123456789ABCDEF";
    public static String response;

    public static boolean checkToken() {
        Log.e("1", "1");
        get("https://www.bedrijvendagentwente.nl/auth/api/accounts/session");
        Log.e("2", "2");
        if (!auth) {
            Log.e("3", "3");
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("email", email);
            params.put("password", password);
            params.put("hash_password", "false");
            if (put("https://www.bedrijvendagentwente.nl/auth/api/accounts/session", params) != 202) {
                Log.e("4", "4");
                Log.e("TOKEN", "Attempted refresh of token was unsuccessful.");
                return false;
            } else {
                Log.d("TOKEN", "Token refreshed.");
                return true;
            }
        } else {
            Log.d("TOKEN", "Token is still valid.");
            return true;
        }
    }

    public static int post(String url_, Map<String, Object> params, boolean login) {
//        URL url = null;
//        try {
//            url = new URL(url_);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

        StringBuilder postData = new StringBuilder();
        postData.append('{');
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() > 1) postData.append(',');
            if (param.getKey() == "account_id" || param.getKey() == "company_id") {
                postData.append("\"" + param.getKey() + "\":" + param.getValue());
            } else {
                postData.append("\"" + param.getKey() + "\":\"" + param.getValue() + "\"");
            }

        }
        postData.append('}');
//
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection)url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("X-Requested-With", "XmlHttpRequest");
//            if (!login) {
//                conn.setRequestProperty("X-Csrf-Token", "\"" +  CompanyCredentials.token + "\"");
//            }
//            Log.d("POST", postData.toString());
//            conn.setDoOutput(true);
//            conn.getOutputStream().write(postData.toString().getBytes());
//            Log.d("POST", "Request has been sent.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        processResponse(conn);
//        try {
//            return conn.getResponseCode();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return -1;
//        }
        //Some url endpoint that you may have
        String myUrl = "http://myApi.com/get_some_data";
        //String to place our result in
        String result;
        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();
        //Perform the doInBackground method, passing in our url
        try {
            result = getRequest.execute(myUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int delete(String url_) {
        URL url = null;
        try {
            url = new URL(url_);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Requested-With", "XmlHttpRequest");
            String token = CompanyCredentials.token;
            conn.setRequestProperty("X-Csrf-Token", token);
            conn.connect();
            Log.d("DELETE", "Request has been sent.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        processResponse(conn);
        try {
            return conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void get(String url_) {
        URL url = null;
        try {
            url = new URL(url_);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Requested-With", "XmlHttpRequest");
            String token = "\"" + CompanyCredentials.token + "\"";
            conn.setRequestProperty("X-Csrf-Token", token);
            conn.setDoOutput(true);
            conn.connect();
            Log.d("GET", "Request has been sent.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        processResponse(conn);
    }

    public static int put(String url_, Map<String, Object> params) {
        //checkToken();
        URL url = null;
        try {
            url = new URL(url_);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        StringBuilder postData = new StringBuilder();
        postData.append('{');
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() > 1) postData.append(',');
            postData.append("\"" + param.getKey() + "\":\"" + param.getValue() + "\"");
        }
        postData.append('}');

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Requested-With", "XmlHttpRequest");
            conn.setRequestProperty("X-Csrf-Token", CompanyCredentials.token);
            conn.setDoOutput(true);
            Log.d("PUT", postData.toString());
            conn.getOutputStream().write(postData.toString().getBytes());
            Log.d("PUT", "Request has been sent.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        processResponse(conn);
        try {
            return conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String SHA1(String text) {
        // SHA-1 Hashing from: https://codebutchery.wordpress.com/2014/08/27/how-to-get-the-sha1-hash-sum-of-a-string-in-android/
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("UTF-8"), 0, text.length());
            byte[] sha1hash = md.digest();
            return toHex(sha1hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        int l = buf.length;
        StringBuffer result = new StringBuffer(2 * l);

        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f))
                .append(HEX.charAt(b & 0x0f));
    }

    private static void processResponse(HttpURLConnection conn) {
        // If the login was successful:
        assert conn != null;
        try {
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            Log.d("RESPONSE", "Attepmting to read response.");
            response = "";
            for (int c; (c = in.read()) >= 0; ) {
                response += (char) c;
            }
            Log.d("RESPONSE", "Returned code " + conn.getResponseCode());
            Log.d("RESPONSE", response);

            // If login was not successful.
        } catch (IOException e) {
            Log.e("RESPONSE", "Check the error stream for more details.");
            try {
                System.out.println("Error code: " + conn.getResponseCode());
                Reader in = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                Log.e("ERROR STREAM", "Error:");
                for (int c; (c = in.read()) >= 0; ) {
                    System.out.print((char) c);
                }
                System.out.println("");

            } catch (IOException e2) {
                Log.e("RESPONSE", "Unhandled network exception.");
                e2.printStackTrace();
            }
        }
    }

    public static String getResponse() {
        String response_ = response;
        response = null;
        return response_;
    }

}
