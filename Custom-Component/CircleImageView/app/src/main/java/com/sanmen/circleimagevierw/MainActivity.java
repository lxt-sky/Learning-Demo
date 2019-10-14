package com.sanmen.circleimagevierw;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sanmen.library.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        CircleImageView circleImageView = findViewById(R.id.circleImage);
        circleImageView.setImageDrawable(getDrawable(R.mipmap.logo));
    }
}
