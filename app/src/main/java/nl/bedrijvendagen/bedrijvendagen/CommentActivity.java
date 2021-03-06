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

    private Button bComment1;
    private Button bComment2;
    private Button bComment3;

    private final int MAX_COMMENT_LENGTH = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        overwriting = getIntent().getExtras().getBoolean("isOverwriting");
        Log.d("Overwriting?", String.valueOf(overwriting));

        initViews();
        initListeners();
        setFont();
    }

    private void initViews() {
        tvScannedName = findViewById(R.id.tvScannedName);
        etCommentField = findViewById(R.id.etCommentField);
        bSave = findViewById(R.id.bSave);
        filler = findViewById(R.id.filler);
        ivLogo = findViewById(R.id.ivBDLogo);

        bComment1 = findViewById(R.id.bComment1);
        bComment2 = findViewById(R.id.bComment2);
        bComment3 = findViewById(R.id.bComment3);

        standardComment1 = TextFileHandler.read(CommentActivity.this, "standardComment1.txt");
        standardComment2 = TextFileHandler.read(CommentActivity.this, "standardComment2.txt");
        standardComment3 = TextFileHandler.read(CommentActivity.this, "standardComment3.txt");

        bComment1.setText(ellipsis(standardComment1, MAX_COMMENT_LENGTH));
        bComment2.setText(ellipsis(standardComment2, MAX_COMMENT_LENGTH));
        bComment3.setText(ellipsis(standardComment3, MAX_COMMENT_LENGTH));

//        Log.d("Status", overwriting ? "Overwriting..." : "New entry being created.");
//        Intent intent = getIntent();
//        overwriting = intent.getExtras().getBoolean("isOverwriting");

        if (overwriting) {
//            firstName = parsedName;
//            lastName = "";
            tvScannedName.setText(firstName);
//            parsedId = parsedId.replaceAll("\\D+", "");
//            userID = Integer.valueOf(parsedId);
//            etCommentField.setHint(parsedComment);
            etCommentField.setHint(comment == "null" ? "" : comment);
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
                Log.d("COMMENT", "Saving...");
                String commentFieldText = etCommentField.getText().toString();
                if (!(overwriting && commentFieldText.isEmpty())) {
                    comment = commentFieldText;
                }
                // Remove any entries that are the hinted text.
                if (comment.equals("Add comment...")) {
                    comment = "";
                }
                Intent loadIntent = new Intent(CommentActivity.this, LoadActivity.class);
                loadIntent.putExtra("overwriting", overwriting);
                loadIntent.putExtra("hasFailedBefore", false);
                startActivity(loadIntent);
                finish();
            }
        });

        bComment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCommentField.setText(etCommentField.getText().toString() + standardComment1 + " ");
            }
        });

        bComment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCommentField.setText(etCommentField.getText().toString() + standardComment2 + " ");
            }
        });

        bComment3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCommentField.setText(etCommentField.getText().toString() + standardComment3 + " ");
            }
        });
    }

    private void setFont() {
        setTypeface(this, tvScannedName);
        setTypeface(this, etCommentField);
        setTypeface(this, bSave);
    }

//    private void readStandardComments() {
//        StringBuilder text = new StringBuilder();
//        try {
//            File standardComments = new File("standardComments.txt");
//            BufferedReader br = new BufferedReader(new FileReader(standardComments));
//            String line;
//
//            while((line = br.readLine()) != null) {
//                text.append(line);
//                text.append('\n');
//            }
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String[] standardCommentsArr = text.toString().split("\n");
//        standardComment1 = standardCommentsArr[0];
//        standardComment2 = standardCommentsArr[1];
//        standardComment3 = standardCommentsArr[2];
//    }

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

    private String ellipsis(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length - 3) + "...";
        }
        return text;

    }
}