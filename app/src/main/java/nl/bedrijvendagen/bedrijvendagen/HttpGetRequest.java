package nl.bedrijvendagen.bedrijvendagen;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    public int responseCode;
    public String response = "";

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-Requested-With", "XmlHttpRequest");
            urlConnection.setRequestProperty("X-Csrf-Token", CompanyCredentials.token);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            responseCode = urlConnection.getResponseCode();
            Log.d("GET RESPONSE CODE", String.valueOf(responseCode));
            InputStream inputStream = null;
            try {
                inputStream = urlConnection.getInputStream();
            } catch (IOException exception) {
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

//    public int responseCode = -1;
//    public String response = "";
//    @Override
//    protected String doInBackground(String... params) {
//
//        String stringUrl = params[0];
//
//        try {
//            URL url = new URL(stringUrl);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.setRequestProperty("Content-Type", "application/json");
//            urlConnection.setRequestProperty("X-Requested-With", "XmlHttpRequest");
//            urlConnection.setRequestProperty("X-Csrf-Token", "{" + CompanyCredentials.token + "}");
//            urlConnection.setReadTimeout(15000);
//            urlConnection.setConnectTimeout(15000);
//            urlConnection.connect();
//
//            responseCode = urlConnection.getResponseCode();
//            Log.d("POST RESPONSE CODE", String.valueOf(responseCode));
//            InputStream inputStream;
//            if (responseCode ==  200) {
//                inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                // From here you can convert the string to JSON with whatever JSON parser you like to use
//                // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
//            } else {
//                inputStream = new BufferedInputStream(urlConnection.getErrorStream());
//                // Status code is not 200
//                // Do something to handle the error
//            }
//
//            BufferedInputStream bis = new BufferedInputStream(inputStream);
//            ByteArrayOutputStream buf = new ByteArrayOutputStream();
//            int ris = bis.read();
//            while (ris != -1) {
//                buf.write((byte) ris);
//                ris = bis.read();
//            }
//            response = buf.toString();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return response;
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//    }

}
