package com.example.beautifulofsin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View {
    private Paint mPaint, mCriclePaint,mTextPaint;
    // 倾斜或旋转、快速变化,当在屏幕上画一条直线时, 横竖不会出现锯齿,
    // 但是当斜着画时, 就会出现锯齿的效果,所以需要设置抗锯齿
    private DrawFilter mDrawFilter;
    private int mTotalHeight, mTotalWidth;
    private int mXoffset = 0;
    private float[] mPointY;
    private float[] mDaymicPointY;
    //波浪线移动速度
    private static final int X_SPEED = 20;
    private int percent;

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //图片线条（通用）的抗锯齿需要另外设置
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        //实例化一个画笔
        mPaint = new Paint();
        //去除画笔锯齿
        mPaint.setAntiAlias(true);
        //设置画笔风格为实线
        mPaint.setStyle(Paint.Style.FILL);
        //设置画笔颜色
        mPaint.setColor(Color.GREEN);
        //实例化圆的画笔
        mCriclePaint = new Paint(mPaint);
        mCriclePaint.setColor(Color.parseColor("#88dddddd"));
        mCriclePaint.setAlpha(255);
        //实例化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //去除锯齿
        canvas.setDrawFilter(mDrawFilter);
        runWave();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
            canvas.drawCircle(mTotalWidth / 2, mTotalHeight / 2, mTotalWidth / 2, mCriclePaint);
            //设置颜色混合模式
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            //高减去宽除以2使水波纹底部在圆底部，动态改变percent值，在Y轴上变化
            for (int i = 0; i < mTotalWidth; i++) {
                canvas.drawLine(i, mTotalHeight - mDaymicPointY[i] - (mTotalHeight - mTotalWidth) / 2 - percent * mTotalWidth / 100, i, mTotalHeight - (mTotalHeight - mTotalWidth) / 2, mPaint);
            }
            //最后将画笔去除Xfermode
            mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
        //改变两条波纹的移动点
        mXoffset += X_SPEED;
        //如果已经移动到末尾处，则到头重新移动
        if (mXoffset > mTotalWidth) {
            mXoffset = 0;
        }
        String text = percent + "%";
        mTextPaint.setTextSize(80);
        float textLength = mTextPaint.measureText(text);
        canvas.drawText(text,(mTotalWidth - textLength) / 2,mTotalHeight / 2 - 20,mTextPaint);
        //引起view重绘
        postInvalidateDelayed(300);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalHeight = h;
        mTotalWidth = w;
        //数组的长度为view的宽度
        mPointY = new float[w];
        mDaymicPointY = new float[w];
        //这里我们以view的总宽度为周期，y = a * sin(2π) + b
        for (int i = 0; i < mTotalWidth; i++) {
            mPointY[i] = (float) (20 * (Math.sin(2 * Math.PI * i / w)));
        }
    }

    private void runWave() {
        // 超出屏幕的挪到前面，mXoffset表示第一条水波纹要移动的距离
        int yIntelrval = mPointY.length - mXoffset;
        //使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mPointY, 0, mDaymicPointY, mXoffset, yIntelrval);
        System.arraycopy(mPointY, yIntelrval, mDaymicPointY, 0, mXoffset);
    }
}
