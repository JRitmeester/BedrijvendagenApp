package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;

public class ConfirmationActivity extends AppCompatActivity {

    private long timeCountInMilliSeconds = 5000;
    private ProgressBar progressBarCircle;
    private CountDownTimer countDownTimer;
    private int interval = 10;

    private TextView tvSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        initViews();
        setFont();
        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, interval) {

            @Override
            public void onTick(long millisUntilFinished) {
                float progress = (timeCountInMilliSeconds - millisUntilFinished) / interval;
                progressBarCircle.setProgress((int) progress);
            }

            @Override
            public void onFinish() {
                resetTimer();
                Intent homeIntent = new Intent(ConfirmationActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }.start();
        countDownTimer.start();
    }

    private void initViews() {
        tvSuccess = findViewById(R.id.tvSuccess);
        progressBarCircle = findViewById(R.id.progressBarCircle);
    }

    private void setFont() {
        // TODO: Replace footers with images and remove typeface for them.
        setTypeface(this, tvSuccess);
    }

    private void resetTimer() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / interval);
        progressBarCircle.setProgress(0);
    }
}
