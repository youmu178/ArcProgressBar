package com.youzh.arcprogressbar;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ArcProgressBar mArcProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArcProgressBar = (ArcProgressBar) findViewById(R.id.arcProgressBar);

        ValueAnimator valueAnimator =ValueAnimator.ofInt(0, 75);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animation.getAnimatedValue();
                mArcProgressBar.setProgress((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(5000);
        valueAnimator.start();

    }
}
