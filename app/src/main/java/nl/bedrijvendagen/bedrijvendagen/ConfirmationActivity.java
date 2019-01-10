package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;

public class ConfirmationActivity extends AppCompatActivity {

    private RelativeLayout rlWrapper;
    private long timeCountInMilliSeconds = 3000;
    private ProgressBar progressBarCircle;
    private CountDownTimer countDownTimer;
    private int interval = 10;

    private TextView tvSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        initViews();
        initListeners();
        setFont();

        progressBarCircle.setProgress(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTimer();
                    }
                }, 500);
            }
        });
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
                conclude();
            }
        }.start();
        countDownTimer.start();
    }

    private void initViews() {
        rlWrapper = findViewById(R.id.rlWrapper);
        tvSuccess = findViewById(R.id.tvSuccess);
        progressBarCircle = findViewById(R.id.progressBarCircle);

    }

    private void initListeners() {
        rlWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conclude();
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvSuccess);
    }

    private void resetTimer() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / interval);
        progressBarCircle.setProgress(0);
    }

    private void conclude() {
        // When clicked anywhere, reset the timer and go to home screen.
        Intent homeIntent = new Intent(ConfirmationActivity.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        startActivity(homeIntent);

//        resetTimer();
//        finish();
    }
}
