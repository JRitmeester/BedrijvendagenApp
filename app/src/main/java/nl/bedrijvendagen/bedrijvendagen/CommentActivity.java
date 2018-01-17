package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.comment;

public class CommentActivity extends AppCompatActivity {

    private TextView tvScannedName;
    private EditText etCommentField;
    private Button bSave;
    private ImageView ivLogo;
    private View filler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initViews();
        initListeners();
        setFont();

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

    private void initViews() {
        tvScannedName = findViewById(R.id.tvScannedName);
        etCommentField = findViewById(R.id.etCommentField);
        bSave = findViewById(R.id.bSave);
    }

    private void initListeners() {
        filler = findViewById(R.id.filler);
        ivLogo = findViewById(R.id.ivBDLogo);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = etCommentField.getText().toString();
                Intent loadIntent = new Intent(CommentActivity.this, LoadActivity.class);
                startActivity(loadIntent);
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvScannedName);
        setTypeface(this, etCommentField);
        setTypeface(this, bSave);
    }
}