package com.youzh;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
    private Paint mTextPaint;
    private Paint mDottedLinePaint;
    private Paint mRonudRectPaint;
    private Paint mProgressPaint;
    private RectF mRountRect;
    private RectF mArcRect;
    /**
     * 圆弧宽度
     */
    private float mArcWidth = 20.0f;
    /**
     * 圆弧颜色
     */
    private int mArcBgColor = 0xFF916D;
    /**
     * 虚线默认颜色
     */
    private String mDottedDefaultColor = "#8D99A1";
    /**
     * 虚线变动颜色
     */
    private String mDottedRunColor = "#f0724f";
    /**
     * 圆弧两边的距离
     */
    private int mPdDistance = 50;
    /**
     * 底部默认文字
     */
    private String text = "限时优惠";
    /**
     * 线条数
     */
    private int mDottedLineCount = 100;
    /**
     * 线条宽度
     */
    private int mDottedLineWidth = 40;
    /**
     * 线条高度
     */
    private int mDottedLineHeight = 6;
    /**
     * 圆弧跟虚线之间的距离
     */
    private int mLineDistance = 20;
    /**
     * 进度条最大值
     */
    private int mProgressMax = 100;
    /**
     * 进度文字大小
     */
    private int mProgressTextSize = 80;

    private int mScressWidth;
    private int mProgress;
    private float mExternalDottedLineRadius;
    private float mInsideDottedLineRadius;
    private int mArcCenterX;
    private int mArcRadius; // 圆弧半径
    private double bDistance;
    private double aDistance;
    private boolean isRestart = false;
    private int mRealProgress;

    public ArcProgressBar(Context context) {
        this(context, null, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiAttributes(context, attrs);
        initView();
    }

    private void intiAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
        mPdDistance = a.getInteger(R.styleable.ArcProgressBar_ArcDistance, mPdDistance);
        mArcBgColor = a.getColor(R.styleable.ArcProgressBar_ArcBgColor, mArcBgColor);
        a.recycle();
    }


    private void initView() {
        int[] screenWH = getScreenWH();
        mScressWidth = screenWH[0];
        // 外层圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setColor(mArcBgColor);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        //
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(dp2px(getResources(), 16));
        mTextPaint.setColor(Color.WHITE);
        // 内测虚线的画笔
        mDottedLinePaint = new Paint();
        mDottedLinePaint.setAntiAlias(true);
        mDottedLinePaint.setStrokeWidth(mDottedLineHeight);
        mDottedLinePaint.setColor(Color.parseColor(mDottedDefaultColor));
        //
        mRonudRectPaint = new Paint();
        mRonudRectPaint.setAntiAlias(true);
        mRonudRectPaint.setColor(Color.parseColor(mDottedRunColor));
        mRonudRectPaint.setStyle(Paint.Style.FILL);
        // 中间进度画笔
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(Color.parseColor(mDottedRunColor));
        mProgressPaint.setTextSize(dp2px(getResources(), mProgressTextSize));
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

        mRountRect = new RectF();
        mRountRect.left = (float) (2 * mArcCenterX - 2 * aDistance) / 2 - mArcWidth / 2 + 40;
        mRountRect.top = (float) (mArcCenterX + bDistance) - 20;
        mRountRect.right = (float) (2 * mArcCenterX - (2 * mArcCenterX - 2 * aDistance) / 2) - mArcWidth / 2 - 40;
        mRountRect.bottom = (float) (mArcRadius + mArcRadius) - 20;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(mArcRect, 135, 270, false, mArcPaint);

        canvas.drawRoundRect(mRountRect, 100, 100, mRonudRectPaint);

        canvas.drawText(text, mArcRadius - mTextPaint.measureText(text) / 2,
                (float) (mArcRadius + bDistance) - 2 * (mTextPaint.descent() + mTextPaint.ascent()), mTextPaint);
//        Log.i(TAG, (float) (mArcRadius + bDistance) - 2 * (mTextPaint.descent() + mTextPaint.ascent()) + "");

        drawDottedLineArc(canvas);
        drawRunDottedLineArc(canvas);
        drawRunText(canvas);
        if (isRestart) {
            drawDottedLineArc(canvas);
        }
    }

    private void drawRunText(Canvas canvas) {
        String progressStr = this.mRealProgress + "%";
        canvas.drawText(progressStr, mArcCenterX - mProgressPaint.measureText(progressStr) / 2,
                mArcCenterX - (mProgressPaint.descent() + mProgressPaint.ascent()) / 2 - 20, mProgressPaint);
    }

    public void restart() {
        isRestart = true;
        this.mRealProgress = 0;
        invalidate();
    }

    private void setMaxProgress(int max) {
        this.mProgressMax = max;
    }

    public void setProgress(int progress) {
        // 进度100% = 控件的75%
        this.mRealProgress = progress;
        isRestart = false;
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

    private float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
