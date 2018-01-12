package nl.bedrijvendagen.bedrijvendagen;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpPostRequest extends AsyncTask<String, Void, String> {

    private JSONObject postData;
    public int responseCode;
    public String response = "";

    public HttpPostRequest(Map<String, Object> postData) {
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-Requested-With", "XmlHttpRequest");

            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            responseCode = urlConnection.getResponseCode();
            Log.d("POST RESPONSE CODE", String.valueOf(responseCode));

            InputStream inputStream = null;
            try {
                inputStream = urlConnection.getInputStream();
            } catch (Exception e) {
                inputStream = urlConnection.getErrorStream();
            }

            BufferedInputStream bis = new BufferedInputStream(inputStream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int ris = bis.read();
            while (ris != -1) {
                buf.write((byte) ris);
                ris = bis.read();
            }
            response = buf.toString();

        } catch (Exception e) {
            Log.d("POST", e.getLocalizedMessage());

        }
        return response;
    }

}