package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;
import static nl.bedrijvendagen.bedrijvendagen.StudentCredentials.comment;

public class CommentActivity extends AppCompatActivity {

    private TextView tvScannedName;
    private EditText etCommentField;
    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initViews();
        initListeners();
        setFont();
    }

    private void initViews() {
        tvScannedName = findViewById(R.id.tvScannedName);
        etCommentField = findViewById(R.id.etCommentField);
        bSave = findViewById(R.id.bSave);
    }

    private void initListeners() {
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