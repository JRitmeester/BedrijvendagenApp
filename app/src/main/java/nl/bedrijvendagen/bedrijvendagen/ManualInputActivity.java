package nl.bedrijvendagen.bedrijvendagen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
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
    }

    @Override
    public void onResume() {
        StudentCredentials.reset();
        if (!hasEmail) {
            tvFirstName.setVisibility(View.GONE);
            tvLastName.setVisibility(View.GONE);
        } else if (!firstName.equals("default") && !lastName.equals("default")) {
            tvFirstName.setVisibility(View.VISIBLE);
            tvLastName.setVisibility(View.VISIBLE);
            tvFirstName.setText(firstName);
            tvLastName.setText(lastName);
            tvFirstName.setEnabled(false);
            tvLastName.setEnabled(false);
        } else {
            tvFirstName.setVisibility(View.VISIBLE);
            tvLastName.setVisibility(View.VISIBLE);
            tvFirstName.setEnabled(true);
            tvLastName.setEnabled(true);
        }
        super.onResume();
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
                String email = etEmail.getText().toString();
                if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(ManualInputActivity.this, "Please enter a valid email address.", Toast.LENGTH_LONG).show();
                } else {
                    firstName = tvFirstName.getText().toString();
                    lastName = tvLastName.getText().toString();
                    email = etEmail.getText().toString();
                    Intent commentIntent = new Intent(ManualInputActivity.this, CommentActivity.class);
                    startActivity(commentIntent);
                    finish();
                }
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvFirstName);
        setTypeface(this, tvLastName);
        setTypeface(this, etEmail);
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
