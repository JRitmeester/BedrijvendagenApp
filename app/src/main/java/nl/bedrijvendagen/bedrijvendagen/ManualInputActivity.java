package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.email;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.firstName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.hasEmail;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.lastName;

public class ManualInputActivity extends AppCompatActivity {

    private TextView tvFirstName;
    private TextView tvLastName;
    private EditText etEmail;
    private Button bSaveEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
        initViews();
        initListeners();
        setFont();

        if (!hasEmail) {
            tvFirstName.setVisibility(View.GONE);
            tvLastName.setVisibility(View.GONE);
        } else {
            tvFirstName.setVisibility(View.VISIBLE);
            tvLastName.setVisibility(View.VISIBLE);
            tvFirstName.setText(firstName);
            tvLastName.setText(lastName);
        }
    }

    private void initViews() {
        tvFirstName = findViewById(R.id.etFirstName);
        tvLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        bSaveEntry = findViewById(R.id.bSaveEntry);
    }

    private void initListeners() {
        bSaveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEmail.getText().toString().isEmpty()) {
                    Toast.makeText(ManualInputActivity.this, "Please enter the student's email address.", Toast.LENGTH_LONG);
                } else {
                    // TODO: Verify email address?
                    email = etEmail.getText().toString();
                    Log.d("EMAIL", "Email was set to be:" + email);
                    Intent commentIntent = new Intent(ManualInputActivity.this, CommentActivity.class);
                    startActivity(commentIntent);
                }
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvFirstName);
        setTypeface(this, tvLastName);
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
