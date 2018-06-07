package com.samluys.linechartdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author luys
 * @describe 自定义折线图
 * @date 2018/6/6
 * @email samluys@foxmail.com
 */
public class LineChartView extends View {

    private int mSpaceLength;
    private Paint mBottomLinePaint;
    private Paint mYLinePaint;
    private Paint mBrokenLinePaint;
    private Paint mOutCirclePaint;
    private Paint mInCirclePaint;
    private Paint mXTextPaint;
    private Paint mYTextPaint;
    /**
     * 左右两边距边缘的距离
     */
    private int mSideLength = Utils.dp2px(getContext(), 28);
    /**
     * 整个折线图的高度
     */
    private int mHeight = Utils.dp2px(getContext(),220);
    /**
     * 屏幕的宽度
     */
    private int mScreenWidth = Utils.getScreenWidth(getContext());
    /**
     * 距离上边距的高度
     */
    private int mPaddingTop = Utils.dp2px(getContext(),15);
    /**
     * X轴底部文字的高度
     */
    private int mXTextHeight = Utils.dp2px(getContext(), 40);
    /**
     * 节点外圆的半径
     */
    private int outRadius = Utils.dp2px(getContext(), 6);
    /**
     * 节点内圆的半径
     */
    private int inRadius = Utils.dp2px(getContext(), 4);
    /**
     * 线条和节点内圆的颜色
     */
    private int mLineColor = Color.parseColor("#D61939");

    /**
     * 固定最高点在Y轴上的高度
     */
    private int mPeakHeight = 60;

    /**
     * x轴数据
     */
    private List<String> mXAxis = new ArrayList<>();

    /**
     * y轴数据
     */
    private List<String> mYAxis = new ArrayList<>();

    /**
     * Y轴上数值与高度集合
     */
    private List<Pair<String,Integer>> valueHeight = new ArrayList<>();


    private List<Pair> circlexy = new ArrayList<>();

    public LineChartView(Context context) {
        this(context,null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {

        // 获取间隔距离
        getSpaceLength();
        // 初始化Paint
        initYLine();
        initBottomLine();
        initOutCircle();
        initInCircle();
        initBrokenLine();
        initXtext();
        initYtext();
    }

    /**
     * 初始化Y轴上数值
     */
    private void initYtext() {
        mYTextPaint = new Paint();
        mYTextPaint.setColor(Color.WHITE);
        mYTextPaint.setTextSize(26);
        mYTextPaint.setStyle(Paint.Style.FILL);
        mYTextPaint.setAntiAlias(true);

    }

    /**
     * 初始化X轴标签
     */
    private void initXtext() {
        mXTextPaint = new Paint();
        mXTextPaint.setColor(Color.parseColor("#666666"));
        mXTextPaint.setTextSize(26);
        mXTextPaint.setStyle(Paint.Style.FILL);
        mXTextPaint.setAntiAlias(true);
    }

    /**
     * 初始化折线
     */
    private void initBrokenLine() {
        mBrokenLinePaint = new Paint();
        mBrokenLinePaint.setColor(mLineColor);
        mBrokenLinePaint.setStrokeWidth(Utils.dp2px(getContext(), 2));
        mBrokenLinePaint.setStyle(Paint.Style.STROKE);
        mBrokenLinePaint.setAntiAlias(true);
    }

    /**
     * 初始化节点内圆
     */
    private void initInCircle() {
        mInCirclePaint = new Paint();
        mInCirclePaint.setColor(mLineColor);
        mInCirclePaint.setStyle(Paint.Style.FILL);
        mInCirclePaint.setAntiAlias(true);
    }

    /**
     * 初始化外圆
     */
    private void initOutCircle() {
        mOutCirclePaint = new Paint();
        mOutCirclePaint.setColor(Color.parseColor("#F7D1D7"));
        mOutCirclePaint.setStyle(Paint.Style.FILL);
        mOutCirclePaint.setAntiAlias(true);
    }

    /**
     * 获取间隔距离
     */
    private void getSpaceLength() {
        mSpaceLength = (mScreenWidth - mSideLength*2)/5;
    }

    /**
     * 初始化竖直方向的线条
     */
    private void initYLine() {
        mYLinePaint = new Paint();
        mYLinePaint.setColor(Color.parseColor("#e7e7e7"));
        mYLinePaint.setStrokeWidth(Utils.dp2px(getContext(), 2));
        mYLinePaint.setStyle(Paint.Style.STROKE);
        mYLinePaint.setAntiAlias(true);
    }

    /**
     * 初始化底部横线paint
     */
    private void initBottomLine() {
        mBottomLinePaint = new Paint();
        mBottomLinePaint.setColor(Color.parseColor("#e7e7e7"));
        mBottomLinePaint.setStrokeWidth(Utils.dp2px(getContext(), 4));
        mBottomLinePaint.setStyle(Paint.Style.STROKE);
        mBottomLinePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 注意顺序，避免线条覆盖文字
        // 绘制竖直方向上的6条线
        drawYLine(canvas);
        // 绘制底部的X轴线
        drawBottomLine(canvas);
        // 绘制节点和折线图
        drawCircleLine(canvas);
        // 绘制Y轴上的数据和背景
        drawYtext(canvas);
        // 绘制X轴标签文字
        drawBottomText(canvas);
    }

    /**
     * 绘制Y轴上的数据和背景
     * @param canvas
     */
    private void drawYtext(Canvas canvas) {

        for (int i = 0; i < valueHeight.size(); i++) {
            Pair<String, Integer> pair = valueHeight.get(i);
            // 绘制节点上文字的背景
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.icon_price_trend);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            canvas.drawBitmap(bitmap, mSideLength+mSpaceLength*i - bitmapWidth/2,pair.second - bitmapHeight - 20, new Paint());
            // 用Rect计算Text内容宽度
            Rect bounds = new Rect();
            mYTextPaint.getTextBounds(pair.first, 0, pair.first.length(), bounds);
            int textWidth = bounds.right-bounds.left;
            // 绘制节点上的文字
            canvas.drawText(pair.first, mSideLength+mSpaceLength*i - textWidth/2,pair.second - bitmapHeight/2 - 14, mYTextPaint);
        }
    }

    /**
     * 绘制X轴标签文字
     * @param canvas
     */
    private void drawBottomText(Canvas canvas) {

        for (int i = 0; i < mXAxis.size(); i++) {
            String xValue = mXAxis.get(i);
            // 获取Text内容宽度
            Rect bounds = new Rect();
            mXTextPaint.getTextBounds(xValue, 0, xValue.length(),bounds);
            int width = bounds.right - bounds.left;
            canvas.drawText(xValue,mSideLength - width/2 + mSpaceLength*i ,mHeight - mXTextHeight/2,mXTextPaint);
        }
    }

    /**
     * 绘制节点和折线图
     * @param canvas
     */
    private void drawCircleLine(Canvas canvas) {
        circlexy.clear();
        for (int i = 0; i < valueHeight.size(); i++) {
            Pair<String, Integer> pair = valueHeight.get(i);
            // 绘制节点外圆
            canvas.drawCircle(mSideLength+mSpaceLength*i,pair.second,outRadius,mOutCirclePaint);
            // 绘制节点内圆
            canvas.drawCircle(mSideLength+mSpaceLength*i,pair.second,inRadius,mInCirclePaint);
            // 保存圆心坐标
            Pair<Integer,Integer> pairs = new Pair<>(mSideLength+mSpaceLength*i, pair.second);
            circlexy.add(pairs);
        }

        for (int i = 0; i < circlexy.size(); i++) {

            if (i != circlexy.size() - 1) {
                canvas.drawLine((int)circlexy.get(i).first,
                        (int)circlexy.get(i).second,
                        (int)circlexy.get(i+1).first,
                        (int)circlexy.get(i+1).second,mBrokenLinePaint);
            }
        }
    }

    /**
     * 绘制竖直方向上的6条线
     * @param canvas
     */
    private void drawYLine(Canvas canvas) {
        for (int i = 0; i < mYAxis.size(); i++) {
            canvas.drawLine(mSideLength+mSpaceLength*i,mPaddingTop,mSideLength+mSpaceLength*i,mHeight - mXTextHeight,mYLinePaint);
        }
    }

    /**
     * 绘制底部的X轴线
     * @param canvas
     */
    private void drawBottomLine(Canvas canvas) {
        canvas.drawLine(0,mHeight - mXTextHeight,mScreenWidth,mHeight - mXTextHeight,mBottomLinePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mSpaceLength * 24, mHeight);
    }

    /**
     * 设置数据
     * @param xAxis
     * @param yAxis
     */
    public void setData(List<String> xAxis, List<String> yAxis) {
        mXAxis.clear();
        mXAxis.addAll(xAxis);
        mYAxis.clear();
        mYAxis.addAll(yAxis);

        List<Double> list = new ArrayList<>();
        for (int i = 0; i < mYAxis.size(); i++) {
            String yValue = mYAxis.get(i);
            double ydouble = TextUtils.isEmpty(yValue) ? 0 : Double.valueOf(yValue);
            list.add(ydouble);
        }
        // 最小值
        double minValue = Collections.min(list);
        // 最大值
        double maxValue = Collections.max(list);
        // 差值
        double differValue = maxValue - minValue;

        // y轴总高度
        int yHeight = mHeight - mXTextHeight - mPaddingTop;
        // 最低点的高度 即 最小值对应的高度 固定
        int lowHeight = yHeight - mPeakHeight;
        // 最高点的高度 即 最大值对应的高度 固定
        int highHeight = mPeakHeight + mPaddingTop;
        // 轴线上每一份对应的值
        double eachValue = differValue / (lowHeight - highHeight);

        valueHeight.clear();
        for (int i = 0; i < mYAxis.size(); i++) {
            String yValue = mYAxis.get(i);
            double ydouble = TextUtils.isEmpty(yValue) ? 0 : Double.valueOf(yValue);

            if(ydouble == maxValue) {
                // 最高点 和 对应的值
                valueHeight.add(new Pair<String,Integer>(yValue, highHeight));
            } else if (ydouble == minValue) {
                // 最低点 和 对应的值
                valueHeight.add(new Pair<String,Integer>(yValue, lowHeight));
            } else {
                // 对应的高度和值
                int eachHeight = lowHeight - (int) ((ydouble-minValue) / eachValue);
                valueHeight.add(new Pair<String,Integer>(yValue, eachHeight));
            }
        }
        invalidate();
    }
}
