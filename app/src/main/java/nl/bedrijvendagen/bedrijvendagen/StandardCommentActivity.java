package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
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

import com.android.volley.RequestQueue;

import java.io.IOException;
import java.io.OutputStreamWriter;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;

public class StandardCommentActivity extends AppCompatActivity {

    private TextView tvSkip;
    private TextView tvStandard;
    private Button bSave;
    private EditText etComment1;
    private EditText etComment2;
    private EditText etComment3;
    private ImageView ivBDLogo;
    private View filler;

    private RequestQueue queue;
    private String saveCommentsUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_comment);

        initViews();
        initListeners();
        setFont();
        initLogoVisibilityChange();
    }

    private void initViews() {
        tvSkip = findViewById(R.id.tvSkip);
        tvStandard = findViewById(R.id.tvStandard);
        bSave = findViewById(R.id.bSave);
        etComment1 = findViewById(R.id.etComment1);
        etComment2 = findViewById(R.id.etComment2);
        etComment3 = findViewById(R.id.etComment3);
        ivBDLogo = findViewById(R.id.ivBDLogo);
        filler = findViewById(R.id.filler);
    }

    private void initListeners() {
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(StandardCommentActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                saveComments();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveComments();
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvSkip);
        setTypeface(this, tvStandard);
        setTypeface(this, bSave);
        setTypeface(this, etComment1);
        setTypeface(this, etComment2);
        setTypeface(this, etComment3);

        // Set underline for tvLostPassword
        tvSkip.setPaintFlags(tvSkip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void initLogoVisibilityChange() {
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
                        ivBDLogo.setVisibility(View.GONE);
                        filler.setVisibility(View.VISIBLE);
                    } else {
                        ivBDLogo.setVisibility(View.VISIBLE);
                        filler.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void saveComments() {
        String[] standardComments = new String[3];
        standardComments[0] = etComment1.getText().toString();
        standardComments[1] = etComment2.getText().toString();
        standardComments[2] = etComment3.getText().toString();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("standardComments.txt", Context.MODE_PRIVATE));
            for (int i = 0; i < 3; i++) {
                outputStreamWriter.write(standardComments[i] + "\n");
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    // Hide keyboard when touched outside the fields.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View[] exceptions = {etComment1, etComment2, etComment3};
            Rect[] exceptionRects = new Rect[exceptions.length];
            for (int i = 0; i < exceptions.length; i++) {
                exceptionRects[i] = new Rect();
                exceptions[i].getGlobalVisibleRect(exceptionRects[i]);
            }

            View currentView = getCurrentFocus();
            boolean exceptionFound = false;

            for (Rect e : exceptionRects) {
                // See if the touch was inside the text fields.
                if (e.contains((int) event.getRawX(), (int) event.getRawY())) {
                    exceptionFound = true;
                    break;
                }
            }
            // If not...
            if (!exceptionFound) {
                if (currentView != null) {
                    currentView.clearFocus();
                }
                // ... hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
