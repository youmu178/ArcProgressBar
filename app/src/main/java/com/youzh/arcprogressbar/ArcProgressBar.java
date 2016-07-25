package com.youzh.arcprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by youzehong on 16/4/19.
 */
public class ArcProgressBar extends View {
    private final String TAG = "youzh";

    private Paint mArcPaint;
    private RectF mArcRect;
    private Paint mTextPaint;
    private Paint mDottedLinePaint;
    private float mArcWidth = 20.0f;
    private String mArcBgColor = "#FF916D";
    private String mDottedDefaultColor = "#8D99A1";
    private String mDottedRunColor = "#f0724f";
    private int mScressWidth;
    private int mPdDistance = 50;
    private int mArcCenterX;
    private int mArcRadius; // 圆弧半径
    private double bDistance;
    private double aDistance;
    private String text = "限时优惠";
    private int mDottedLineCount = 100; //线条数
    private int mDottedLineWidth = 30;//线条宽度
    private int mLineDistance = 20; // 两线之间的距离
    private int mProgress;
    private int mProgressMax = 100;//进度条最大值
    private float mExternalDottedLineRadius;
    private float mInsideDottedLineRadius;

    public ArcProgressBar(Context context) {
        this(context, null, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        int[] screenWH = getScreenWH();
        mScressWidth = screenWH[0];
        // 外层圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setColor(Color.parseColor(mArcBgColor));
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        //
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(50);
        mTextPaint.setColor(Color.BLACK);
        // 内测虚线的画笔
        mDottedLinePaint = new Paint();
        mDottedLinePaint.setAntiAlias(true);
        mDottedLinePaint.setStrokeWidth(6);
        mDottedLinePaint.setColor(Color.parseColor(mDottedDefaultColor));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = mScressWidth - 2 * mPdDistance;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcCenterX = (int) (w / 2.f);

        mArcRect = new RectF();
        mArcRect.top = 0;
        mArcRect.left = 0;
        mArcRect.right = w;
        mArcRect.bottom = h;

        mArcRect.inset(mArcWidth / 2, mArcWidth / 2);
        mArcRadius = (int) (mArcRect.width() / 2);

        double sqrt = Math.sqrt(mArcRadius * mArcRadius + mArcRadius * mArcRadius);
        bDistance = Math.cos(Math.PI * 45 / 180) * mArcRadius;
        aDistance = Math.sin(Math.PI * 45 / 180) * mArcRadius;

        // 内部虚线的外部半径
        mExternalDottedLineRadius = mArcRadius - mArcWidth / 2 - mLineDistance;
        // 内部虚线的内部半径
        mInsideDottedLineRadius = mExternalDottedLineRadius - mDottedLineWidth;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mArcRect, 135, 270, false, mArcPaint);
        canvas.drawText(text, mArcRadius - mTextPaint.measureText(text) / 2,
                (float) (mArcRadius + bDistance) - 2 * (mTextPaint.descent() + mTextPaint.ascent()), mTextPaint);
//        Log.i(TAG, (float) (mArcRadius + bDistance) - 2 * (mTextPaint.descent() + mTextPaint.ascent()) + "");

        drawDottedLineArc(canvas);
        drawRunDottedLineArc(canvas);
    }

    private void setMaxProgress(int max) {
        this.mProgressMax = max;
    }

    public void setProgress(int progress) {
        // 进度100% = 控件的75%
        this.mProgress = ((mDottedLineCount * 3 / 4) * progress) / mProgressMax;
        postInvalidate();
    }

    private void drawRunDottedLineArc(Canvas canvas) {
        mDottedLinePaint.setColor(Color.parseColor(mDottedRunColor));
        float evenryDegrees = (float) (2.0f * Math.PI / mDottedLineCount);

        float startDegress = (float) (225 * Math.PI / 180) + evenryDegrees / 2;

        for (int i = 0; i < mProgress; i++) {
            float degrees = i * evenryDegrees + startDegress;

            float startX = mArcCenterX + (float) Math.sin(degrees) * mInsideDottedLineRadius;
            float startY = mArcCenterX - (float) Math.cos(degrees) * mInsideDottedLineRadius;

            float stopX = mArcCenterX + (float) Math.sin(degrees) * mExternalDottedLineRadius;
            float stopY = mArcCenterX - (float) Math.cos(degrees) * mExternalDottedLineRadius;

            canvas.drawLine(startX, startY, stopX, stopY, mDottedLinePaint);
        }
    }

    private void drawDottedLineArc(Canvas canvas) {
        mDottedLinePaint.setColor(Color.parseColor(mDottedDefaultColor));
        // 360 * Math.PI / 180
        float evenryDegrees = (float) (2.0f * Math.PI / mDottedLineCount);

        float startDegress = (float) (135 * Math.PI / 180);
        float endDegress = (float) (225 * Math.PI / 180);


        for (int i = 0; i < mDottedLineCount; i++) {
            float degrees = i * evenryDegrees;
            // 过滤底部90度的弧长
            if (degrees > startDegress && degrees < endDegress) {
                continue;
            }

            float startX = mArcCenterX + (float) Math.sin(degrees) * mInsideDottedLineRadius;
            float startY = mArcCenterX - (float) Math.cos(degrees) * mInsideDottedLineRadius;

            float stopX = mArcCenterX + (float) Math.sin(degrees) * mExternalDottedLineRadius;
            float stopY = mArcCenterX - (float) Math.cos(degrees) * mExternalDottedLineRadius;


            canvas.drawLine(startX, startY, stopX, stopY, mDottedLinePaint);
        }
    }

    private int[] getScreenWH() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int[] wh = {displayMetrics.widthPixels, displayMetrics.heightPixels};
        return wh;
    }
}
