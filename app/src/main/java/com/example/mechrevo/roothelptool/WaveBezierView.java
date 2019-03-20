package com.example.mechrevo.roothelptool;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

/**
 * 新人匹配动画界面
 */
public class WaveBezierView extends View implements View.OnClickListener {
    private Path mPath;
    private Paint mPaintBezier, mPaintWhite;

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
    private Path mWhitePath, mWhitePath2;

    public WaveBezierView(Context context) {
        super(context);
    }

    public WaveBezierView(Context context, AttributeSet attrs) {
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
        mPaintBezier.setColor(Color.LTGRAY);
        mPaintBezier.setStrokeWidth(1);
        mPaintBezier.setStyle(Paint.Style.STROKE);

        mPaintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintWhite.setColor(Color.WHITE);
        mPaintWhite.setStrokeWidth(1);
        mPaintWhite.setStyle(Paint.Style.STROKE);
        mWaveLength = width;
    }

    public WaveBezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath = new Path();
        setOnClickListener(this);
        mWhitePath = new Path();
        mWhitePath2 = new Path();

        mScreenHeight = h;
        mScreenWidth = w;
        mCenterY = h / 2;//设定波浪在屏幕中央处显示

        //此处多加1，是为了预先加载屏幕外的一个波浪，持续报廊移动时的连续性
        mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 2);

        int mStartX = -mWaveLength + mOffsetX;
        int mStartY = mCenterY;

        int mEndX = mWaveLength / 2;
        int mEndY = 0;

        int startColor = getResources().getColor(R.color.colorStart);
        int endColor = getResources().getColor(R.color.colorEnd);
        LinearGradient linearGradient = new LinearGradient(mStartX, mStartY, mEndX, mEndY, startColor, endColor, Shader.TileMode.MIRROR);

        mPaintBezier.setShader(linearGradient);

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mWhitePath.reset();
        mWhitePath2.reset();
        //Y坐标每次绘制时减去偏移量，即波浪升高
        mPath.moveTo(-mWaveLength + mOffsetX, mCenterY);
        mWhitePath.moveTo((float) ((-mWaveLength + mOffsetX) * 2), mCenterY);
        mWhitePath2.moveTo((float) ((-mWaveLength + mOffsetX) * 1.5), mCenterY);
        //每次循环绘制两个二阶贝塞尔曲线形成一个完整波形（含有一个上拱圆，一个下拱圆）
        for (int i = 0; i < mWaveCount; i++) {
            mPath.rQuadTo(mWaveLength / 4, -120, mWaveLength / 2, 0);
            mPath.rQuadTo(mWaveLength / 4, +120, mWaveLength / 2, 0);
        }

        for (int i = 0; i < mWaveCount; i++) {
            mWhitePath.rQuadTo(mWaveLength / 4, -120, mWaveLength / 2, 0);
            mWhitePath.rQuadTo(mWaveLength / 4, +120, mWaveLength / 2, 0);
        }

        for (int i = 0; i < mWaveCount; i++) {
            mWhitePath2.rQuadTo(mWaveLength / 4, -120, mWaveLength / 2, 0);
            mWhitePath2.rQuadTo(mWaveLength / 4, +120, mWaveLength / 2, 0);
        }

//        mPath.lineTo(mScreenWidth, mScreenHeight);
//        mPath.lineTo(0, mScreenHeight);
//        mPath.close();
        canvas.drawPath(mPath, mPaintBezier);
        canvas.drawPath(mWhitePath, mPaintWhite);
        canvas.drawPath(mWhitePath2, mPaintWhite);
    }

    public void startAnim() {
        if (mValueAnimator == null) {
            //设置动画运动距离
            mValueAnimator = ValueAnimator.ofInt(0, mWaveLength);
            mValueAnimator.setDuration(2500);
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
        }
        mValueAnimator.start();
    }

    /**
     * 取消播放
     */
    public void cancalAnim() {
        if (mValueAnimator!=null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        startAnim();
    }
}