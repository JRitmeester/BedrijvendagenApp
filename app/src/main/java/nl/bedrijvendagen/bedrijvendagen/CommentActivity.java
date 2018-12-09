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
import android.widget.ImageView;
import android.widget.TextView;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.comment;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.firstName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.lastName;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.userID;

public class CommentActivity extends AppCompatActivity {

    private TextView tvScannedName;
    private EditText etCommentField;
    private Button bSave;
    private ImageView ivLogo;
    private View filler;

    private String parsedName;
    private String parsedId;
    private String parsedComment;
    private boolean overwriting = false;

    private String standardComment1;
    private String standardComment2;
    private String standardComment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initViews();
        initListeners();
        setFont();
        readStandardComments();

//

        Intent intent = getIntent();
        overwriting = intent.getExtras().getBoolean("isOverwriting");
        Log.d("Overwriting?", String.valueOf(overwriting));
        if (overwriting) {
//            if (intent.hasExtra("name")) {
            parsedName = intent.getStringExtra("name");
//            }
//            if (intent.hasExtra("id")) {
            parsedId = intent.getStringExtra("id");
            Log.d("PARSEDID", parsedId);
//            }

//            if (intent.hasExtra("comment")) {
            parsedComment = intent.getStringExtra("comment");
//            }
        }

        Log.d("PARSED FROM HOME:", parsedName + ", " + parsedId + ", " + parsedComment);

    }

    private void initViews() {
        tvScannedName = findViewById(R.id.tvScannedName);
        etCommentField = findViewById(R.id.etCommentField);
        bSave = findViewById(R.id.bSave);
        filler = findViewById(R.id.filler);
        ivLogo = findViewById(R.id.ivBDLogo);

        Log.d("Status", overwriting ? "Overwriting..." : "New entry being created.");
        Intent intent = getIntent();
        overwriting = intent.getExtras().getBoolean("isOverwriting");

        if (overwriting) {
            firstName = parsedName;
            lastName = "";
            tvScannedName.setText(firstName);
            parsedId = parsedId.replaceAll("\\D+", "");
            userID = Integer.valueOf(parsedId);
            etCommentField.setHint(parsedComment);
            Log.d("OVERWRITING", "Set name...");
        }
        if (!firstName.equals("default") && !lastName.equals("default")) {
            if (!overwriting) {
                tvScannedName.setText(firstName + " " + lastName);
            }
        }

    }

    private void initListeners() {
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = etCommentField.getText().toString();
                // Remove any entries being the hinted text.
                if (comment.equals("Add comment...")) {
                    comment = "";
                }
                Intent loadIntent = new Intent(CommentActivity.this, LoadActivity.class);
                loadIntent.putExtra("overwriting", overwriting);
                startActivity(loadIntent);
                finish();
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvScannedName);
        setTypeface(this, etCommentField);
        setTypeface(this, bSave);
    }

    private void readStandardComments() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {     // On touch event
            View v = getCurrentFocus();
            if (v instanceof EditText) {                        // If the touched area is an EditText
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {  // If you touch outside the EditText...
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // ... then hide the keyboard.
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}