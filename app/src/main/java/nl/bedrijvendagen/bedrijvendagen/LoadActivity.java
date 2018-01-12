package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.comment;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class LoadActivity extends AppCompatActivity {

    private String url = "https://www.bedrijvendagentwente.nl/companies/api/student_signups";

    private ImageView loadImage;
    private float rotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        initViews();

        Thread saveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (RequestHandler.checkToken())
                        sendEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        saveThread.start();
        Thread imageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadImage.setRotation(rotation);
                rotation += 1.0f;
            }
        });
        imageThread.start();
    }

    private void initViews() {
        loadImage = findViewById(R.id.ivLoad);
    }

    private void sendEntry() {
        Map<String, Object> params = new LinkedHashMap<>();
        // TODO: Verify data flow: 1. if QR code is not scanned. 2. If QR code is scanned partially. 3. If QR code is complete.
        if (userID == -1) {
            params.put("email", email);
        } else {
            params.put("account_id", userID);
        }
        params.put("comment", comment);

        HttpPostRequest task = new HttpPostRequest(params);
        String response = "";
        try {
            response = task.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (task.responseCode == 200) {
            // If the upload is successful, reset the credentials to empty so that no mix-up of details can occur.
            StudentCredentials.reset();
            Intent confirmIntent = new Intent(this, ConfirmationActivity.class);
            startActivity(confirmIntent);
            finish();
        } else {
            Intent errorIntent = new Intent(this, ErrorActivity.class);
            startActivity(errorIntent);
            finish();
        }

    }

//    private void verifyUserID() {
//        if (StudentCredentials.userID == -1) {
//            Map<String,Object> params = new LinkedHashMap<>();
//            params.put("email", StudentCredentials.email);
//            if (RequestHandler.post("https://www.bedrijvendagentwente.nl/auth/api/accounts/exists", params, false) == 200) {
//                String response = RequestHandler.getResponse();
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    firstName = jObj.getString("first_name");
//                    lastName= jObj.getString("last_name");
//                    userID = Integer.parseInt(jObj.getString("id"));
//                    Log.d("USERID", "UserID is set to be" + userID);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(LoadActivity.this, "Couldn't retrieve user ID from email.", Toast.LENGTH_LONG).show();
//                        Intent errorIntent = new Intent(LoadActivity.this, ErrorActivity.class);
//                        startActivity(errorIntent);
//
//                    }
//                });
//            }
//        }
//    }
}
