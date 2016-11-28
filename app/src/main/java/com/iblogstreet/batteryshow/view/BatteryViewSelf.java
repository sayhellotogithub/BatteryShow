package com.iblogstreet.batteryshow.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.iblogstreet.batteryshow.R;
import com.iblogstreet.batteryshow.utils.Loger;


/**
 * Created by 王军 on 2016/10/5.
 * 主要实现功能，显示电量
 * 如果充电中，则显示动画
 * 实现思路，先将其画出来，然后考虑兼容性
 */

public class BatteryViewSelf
        extends View
{
    private static final String TAG = "BatteryViewSelf";
    private int mColor;
    private int mStrokeColor;
    /**
     * 电量的最大值
     */
    private int mPower = 100;
    /**
     * 电池的宽度
     */
    int mBatteryWidth  = 25;
    /**
     * 电池的高度
     */
    int mBatteryHeight = 15;

    /**
     * 电池内边距
     */
    int mBatteryInsideMargin = 0;

    Paint mBorderPaint;
    Paint mBatteryPaint;
    private Paint mBatteryHeaderPaint;
    /**
     * 电池更新的时间间隔
     */
    private int mInterval        = 1000;
    /**
     * 充电时的颜色
     */
    private int mChargingColor   = Color.GREEN;
    /**
     * 低电量的颜色
     */
    private int mLowBatteryColor = Color.RED;

    /**
     * 圆角距形的角度
     */
    float mRadius;
    /**
     *外部圆角距形
     */
    private RectF mBoardRF;

    private float mStrokeWidth;

    int mAutoIncrementWidth = 0;

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    /**
     * 正在充电中
     */
    private boolean isCharging = false;

    private boolean mLastStatus = false;

    public BatteryViewSelf(Context context) {
        this(context, null);
    }

    public BatteryViewSelf(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public BatteryViewSelf(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        Loger.e(TAG, "BatteryViewSelf");

        mBatteryWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                        80,
                                                        getContext().getResources()
                                                                    .getDisplayMetrics());
        mBatteryHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                         30,
                                                         getContext().getResources()
                                                                     .getDisplayMetrics());

        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme()
                              .obtainStyledAttributes(attrs,
                                                      R.styleable.BatteryView,
                                                      defStyleAttr,
                                                      0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.BatteryView_bvRadius:
                    mRadius = a.getFloat(attr, 9f);
                    break;
                case R.styleable.BatteryView_bvStrokeWidth:
                    mStrokeWidth = a.getDimension(attr, 2);
                    break;
                case R.styleable.BatteryView_bvWidth:
                    mBatteryWidth = a.getDimensionPixelSize(attr, mBatteryWidth);
                    break;
                case R.styleable.BatteryView_bvHeight:
                    mBatteryHeight = a.getDimensionPixelSize(attr, mBatteryHeight);
                    break;
                case R.styleable.BatteryView_bvStrokeColor:
                    mStrokeColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.BatteryView_bvColor:
                    mColor = a.getColor(attr, Color.GREEN);
                    break;

                case R.styleable.BatteryView_bvInsideMargin:
                    mBatteryInsideMargin = a.getDimensionPixelSize(attr, 0);
                    break;

            }

        }
        a.recycle();
        Loger.e(TAG, "mStroke1" + mStrokeWidth);
    }

    void initPaint() {
        //初始画笔
        //外框画笔
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mStrokeColor);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mStrokeWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);


        //电池画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setStyle(Paint.Style.FILL);
        mBatteryPaint.setColor(mColor);
        mBatteryPaint.setAntiAlias(true);

        //电池头画笔

        mBatteryHeaderPaint = new Paint();
        mBatteryHeaderPaint.setStyle(Paint.Style.FILL);
        mBatteryHeaderPaint.setColor(mStrokeColor);
        mBatteryHeaderPaint.setAntiAlias(true);

    }

    /**
     * 中心点X坐标
     */
    float centerX;
    /**
     * 中心点Y坐标
     */
    float centerY;

    Rect mBatteryVolume;
    int  mBleft;
    int  mBright;
    int  mBtop;
    int  mBbottom;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        Loger.e(TAG, "onSizeChanged");
        initPaint();

        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        mBoardRF = new RectF();
        mBoardRF.left = centerX - mBatteryWidth / 2;
        mBoardRF.top = centerY - mBatteryHeight / 2;
        mBoardRF.right = centerX + mBatteryWidth / 2;
        mBoardRF.bottom = centerY + mBatteryHeight / 2;


        //画电池电量

        mBleft = (int) (mBoardRF.left + mBatteryInsideMargin + mStrokeWidth);
        mBtop = (int) (mBoardRF.top + mBatteryInsideMargin + mStrokeWidth);
        mBright = (int) (mBoardRF.right - mBatteryInsideMargin - mStrokeWidth);
        mBbottom = (int) (mBoardRF.bottom - mBatteryInsideMargin - mStrokeWidth);

        mBatteryVolume = new Rect();
        mBatteryVolume.left = mBleft;
        mBatteryVolume.top = mBtop;
        mBatteryVolume.bottom = mBbottom;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth  = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth  = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        if (modeWidth == MeasureSpec.AT_MOST || modeWidth == MeasureSpec.UNSPECIFIED) {
            sizeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                        mBatteryWidth,
                                                        getContext().getResources()
                                                                    .getDisplayMetrics());
            sizeWidth += getPaddingLeft() + getPaddingRight();
        }
        if (modeHeight == MeasureSpec.AT_MOST || modeHeight == MeasureSpec.UNSPECIFIED) {
            sizeHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                         mBatteryHeight,
                                                         getContext().getResources()
                                                                     .getDisplayMetrics());
            sizeHeight += getPaddingBottom() + getPaddingTop();
        }

        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    /**
     * 绘制电池
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画边框
        canvas.drawRoundRect(mBoardRF, mRadius, mRadius, mBorderPaint);
        float power_percent = mPower / 100.0f;

        //画电池电量

        mBatteryVolume.right = (int) (mBatteryVolume.left + getDynamicVolume(mBatteryWidth * power_percent) - 2 * mStrokeWidth);

        Loger.e(TAG, "mBatteryVolume.right" + mBatteryVolume.right);
        canvas.drawRect(mBatteryVolume, mBatteryPaint);

        //画电池头
        canvas.drawRect(mBoardRF.right,
                        centerY - mBatteryHeight / 4,
                        mBoardRF.right + mBatteryWidth / 6,
                        centerY + mBatteryHeight / 4,
                        mBatteryHeaderPaint);


    }


    /**
     *
     * @param mWidth
     * @return
     */
    private int getDynamicVolume(float mWidth) {
        if (isCharging) {
            mAutoIncrementWidth += 2;
        } else {
            mAutoIncrementWidth = 0;
        }
        if (mAutoIncrementWidth >= (mBatteryWidth - mWidth - mStrokeWidth)) {
            mAutoIncrementWidth = 0;
        }
        // Loger.e(TAG,"getDynamicVolume"+mAutoIncrementWidth+"mBright"+mBright+"mBright*power_percent"+(mBright*power_percent));
        return (int) (mWidth + mAutoIncrementWidth);
    }

    /**
     * 充电
     */
    void chargingPower() {

        postDelayed(new Runnable() {
            @Override
            public void run() {

                postInvalidate();
                if (isCharging) { chargingPower(); }
                Loger.e(TAG, "chargingPower");
            }
        }, mInterval);
    }


    public void setPower(int power) {
        Loger.e(TAG, "setPower");
        mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        if (isCharging) {
            //增加状态控制
            if (!mLastStatus) {
                mLastStatus = isCharging;
                chargingPower();
            }
        } else {
            Loger.e(TAG, "invalidate");
            mLastStatus = false;
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isCharging = false;
        mLastStatus = false;
    }
}
