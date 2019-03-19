package com.example.mechrevo.roothelptool;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

public class WaveBezierViewTwo extends View implements View.OnClickListener {
    private Path mPath;

    private Paint mPaintBezier;

    private int mWaveLength;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mCenterY;
    private int mWaveCount;

    private ValueAnimator mValueAnimator;
    //波浪流动X轴偏移量
    private int mOffsetX;
    //波浪升起Y轴偏移量
    private int mOffsetY;
    private int count = 0;
    private int width;
    private int height;

    public WaveBezierViewTwo(Context context) {
        super(context);
    }

    public WaveBezierViewTwo(Context context, AttributeSet attrs) {
        super(context, attrs);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度（像素）
        width = dm.widthPixels;
        // 屏幕高度（像素）
        height = dm.heightPixels;
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）

        mPaintBezier = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBezier.setColor(Color.WHITE);
        mPaintBezier.setStrokeWidth(8);
        mPaintBezier.setStyle(Paint.Style.STROKE);
        mWaveLength = width;
    }

    public WaveBezierViewTwo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath = new Path();
        setOnClickListener(this);

        mScreenHeight = h;
        mScreenWidth = w;
        mCenterY = h / 2;//设定波浪在屏幕中央处显示

        //此处多加1，是为了预先加载屏幕外的一个波浪，持续报廊移动时的连续性
        mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);

        int mStartX = -mWaveLength + mOffsetX;
        int mStartY = mCenterY;

        int mEndX = mWaveLength /2;
        int mEndY = 0;

        int startColor = getResources().getColor(R.color.colorStart);
        int endColor = getResources().getColor(R.color.colorEnd);
        LinearGradient linearGradient = new LinearGradient(mStartX,mStartY,mEndX,mEndY,startColor,endColor,Shader.TileMode.MIRROR);

//        mPaintBezier.setShader(linearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        //Y坐标每次绘制时减去偏移量，即波浪升高
        mPath.moveTo(-mWaveLength + mOffsetX, mCenterY);
        //每次循环绘制两个二阶贝塞尔曲线形成一个完整波形（含有一个上拱圆，一个下拱圆）
        for (int i = 0; i < mWaveCount; i++) {
            mPath.rQuadTo(mWaveLength / 4, -100, mWaveLength / 2, 0);
            mPath.rQuadTo(mWaveLength / 4, +100, mWaveLength / 2, 0);
        }
        mPath.lineTo(mScreenWidth, mScreenHeight+10);
        mPath.lineTo(0, mScreenHeight+10);
        mPath.close();
        canvas.drawPath(mPath, mPaintBezier);
    }

    @Override
    public void onClick(View view) {
        //设置动画运动距离
        mValueAnimator = ValueAnimator.ofInt(0, mWaveLength);
        mValueAnimator.setDuration(1500);
        //设置播放数量无限循环
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        mValueAnimator.setRepeatCount(1);
        //设置线性运动的插值器
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取偏移量，绘制波浪曲线的X横坐标加上此偏移量，产生移动效果
                mOffsetX = (int) valueAnimator.getAnimatedValue();
                count++;
                invalidate();
            }
        });
        mValueAnimator.start();
    }
}