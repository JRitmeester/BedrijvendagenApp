package nl.bedrijvendagen.bedrijvendagen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static nl.bedrijvendagen.bedrijvendagen.Frutiger.setTypeface;

public class SecondErrorActivity extends AppCompatActivity {

    private Button bReturnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_error);

        initViews();
        initListeners();
        setFont();
    }

    private void initViews() {
        bReturnHome = findViewById(R.id.bReturnHome);
    }

    private void initListeners() {
        bReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(SecondErrorActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                StudentCredentials.reset();
                finish();
            }
        });
    }

    private void setFont() {
        setTypeface(this, bReturnHome);
    }
}
