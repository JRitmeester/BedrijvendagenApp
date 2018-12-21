package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.firstName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.hasEmail;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.lastName;
import static nl.bedrijvendagen.bedrijvendagen.ToastWrapper.createToast;

public class ManualInputActivity extends AppCompatActivity {

    private TextView tvFirstName;
    private TextView tvLastName;
    private EditText etEmail;
    private Button bSaveEntry;
    private ImageView ivLogo;
    private View filler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
        initViews();
        initListeners();
        setFont();
        initLogoVisibilityChange();
    }

    @Override
    public void onResume() {
        if (!email.equals("default")) {
            // If the email is set anywhere, remove the fields.
            tvFirstName.setVisibility(View.GONE);
            tvLastName.setVisibility(View.GONE);
        } else if (!firstName.equals("default") && !lastName.equals("default")) {
            // If the entry is complete, but something not right, display the fields.
            tvFirstName.setVisibility(View.VISIBLE);
            tvLastName.setVisibility(View.VISIBLE);
            tvFirstName.setText(firstName);
            tvLastName.setText(lastName);
            tvFirstName.setEnabled(false);
            tvLastName.setEnabled(false);
        } else {
            // Otherwise, it's a regular manual input.
            tvFirstName.setVisibility(View.VISIBLE);
            tvLastName.setVisibility(View.VISIBLE);
            tvFirstName.setEnabled(true);
            tvLastName.setEnabled(true);
        }
        Refresher.refresh(this);
        super.onResume();
    }

    private void initViews() {
        tvFirstName = findViewById(R.id.etFirstName);
        tvLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        bSaveEntry = findViewById(R.id.bSaveEntry);
        ivLogo = findViewById(R.id.ivBDLogo);
        filler = findViewById(R.id.filler);
    }

    private void initListeners() {
        bSaveEntry.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                String firstName = tvFirstName.getText().toString();
                String lastName = tvLastName.getText().toString();

                if (firstName.isEmpty() || lastName.isEmpty()) {

                } else {

                    String email = etEmail.getText().toString();
                    if (!email.contains("@") || !email.contains(".")) {
                        createToast("Please enter a valid email address.", Toast.LENGTH_LONG, ManualInputActivity.this);
                    } else {
                        StudentCredentials.firstName = tvFirstName.getText().toString();
                        StudentCredentials.lastName = tvLastName.getText().toString();
                        StudentCredentials.email = email;
                        hasEmail = true;
                        Intent commentIntent = new Intent(ManualInputActivity.this, CommentActivity.class);
                        commentIntent.putExtra("isOverwriting", false);
                        startActivity(commentIntent);
                        finish();

                    }
                }
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvFirstName);
        setTypeface(this, tvLastName);
        setTypeface(this, etEmail);
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
                        ivLogo.setVisibility(View.GONE);
                        filler.setVisibility(View.VISIBLE);
                        Log.d("LOGO", "Invisible");
                    } else {
                        ivLogo.setVisibility(View.VISIBLE);
                        Log.d("LOGO", "Visible");
                        filler.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
