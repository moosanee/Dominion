package com.example.dominion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DecisionDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_dialog);

        final Button yesButton = findViewById(R.id.yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chancellorKey", true);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        final Button noButton = findViewById(R.id.no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chancellorKey", false);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
