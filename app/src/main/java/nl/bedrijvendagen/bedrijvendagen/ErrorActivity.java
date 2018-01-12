package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.LinkedHashMap;
import java.util.Map;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class ErrorActivity extends AppCompatActivity {

    private Button bTryAgain;
    private String comment;
    private String url = "https://www.bedrijvendagentwente.nl/companies/api/student_signups";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        if (getIntent().hasExtra("comment")) {
            comment = getIntent().getStringExtra("comment");
        }
        initViews();
        initListeners();
        setFont();
    }

    private void initViews() {
        bTryAgain = findViewById(R.id.bTryAgain);
    }

    private void initListeners() {
        bTryAgain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Thread retryThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Normally, resend data. If it doesn't work, go to second error screen.
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("account_id", userID);
                        params.put("comments", comment);

                        HttpPostRequest task = new HttpPostRequest(params);
//                        String response = "";
//                        try {
//                            response = task.execute(url).get();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }

                        if (task.responseCode == 200) {
                            // If the upload is successful, reset the credentials to empty so that no mix-up of details can occur.
                            StudentCredentials.reset();
                            Intent homeIntent = new Intent(ErrorActivity.this, ConfirmationActivity.class);
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Intent secondErrorActivity = new Intent(ErrorActivity.this, SecondErrorActivity.class);
                            startActivity(secondErrorActivity);
                            finish();
                        }
                    }
                });
                retryThread.start();
            }
        });
    }

    private void setFont() {
        setTypeface(this, bTryAgain);
    }
}
