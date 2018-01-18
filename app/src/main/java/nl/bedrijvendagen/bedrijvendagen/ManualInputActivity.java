package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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

public class ManualInputActivity extends AppCompatActivity {

    private TextView etFirstName;
    private TextView etLastName;
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

        if (!hasEmail && !(firstName.equals("default") && lastName.equals("default")) ) {
            Log.d("MANUAL", "Presetting names " + firstName + ", " + lastName);
            etFirstName.setText(firstName);
            etLastName.setText(lastName);
            etFirstName.setEnabled(false);
            etLastName.setEnabled(false);
        } else if (!hasEmail && (firstName.equals("default") && lastName.equals("default"))) {
            etFirstName.setEnabled(true);
            etLastName.setEnabled(true);
        } else {
            Log.e("MANUAL", "Something went wrong here...");
        }

        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
                int screenHeight = findViewById(android.R.id.content).getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                try {
                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        ivLogo.setVisibility(View.GONE);
                        filler.setVisibility(View.VISIBLE);
                    } else {
                        ivLogo.setVisibility(View.VISIBLE);
                        filler.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        bSaveEntry = findViewById(R.id.bSaveEntry);
        ivLogo = findViewById(R.id.ivBDLogo);
        filler = findViewById(R.id.filler);
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
        setTypeface(this, etFirstName);
        setTypeface(this, etLastName);
    }
}
