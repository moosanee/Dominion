package com.example.dominion;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

public class NotificationActivity extends AppCompatActivity {

    private ArrayList<String> postList = new ArrayList<String>();
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        layout = findViewById(R.id.posts);

        postList = getIntent().getStringArrayListExtra("postListKey");

        for (int i = 0; i < postList.size(); i ++){
            if (i == 0){
                TextView textView = findViewById(R.id.post1);
                textView.setText(postList.get(i));
            }else{
                TextView textView = new TextView(this);
                textView.setTextSize(textSize);
                textView.setTextColor(ACCENT_COLOR);
                textView.setIncludeFontPadding(false);
                textView.setTypeface(ResourcesCompat.getFont(this, R.font.alegreya_sc));
                textView.setText(postList.get(i));
                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                layout.addView(textView);
            }
        }

        Button dismiss = findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}
