package com.youzh.arcprogressbar;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.youzh.ArcProgressBar;

public class MainActivity extends AppCompatActivity {

    private ArcProgressBar mArcProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArcProgressBar = (ArcProgressBar) findViewById(R.id.arcProgressBar);
        Button btRestart = (Button) findViewById(R.id.button);
        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArcProgressBar.restart();
            }
        });
        Button btStart = (Button) findViewById(R.id.button2);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValueAnimator valueAnimator =ValueAnimator.ofInt(0, 100);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mArcProgressBar.setProgress((int) animation.getAnimatedValue());
                    }
                });
                valueAnimator.setDuration(5000);
                valueAnimator.start();
            }
        });

    }
}
