package com.susyimes.funbox.psmaster;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class DrawView extends View {

    Paint paint;

    float startX = 0;
    float startY = 0;

    /**
     * 用来保存绘制的路径
     */
    ArrayList<PathModel> paths;

    private int color;
    private int width = 8;
    private Canvas canvas;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化画笔
        color = getResources().getColor(R.color.purple_500);
        paint = new Paint();
        paint.setStrokeWidth(width);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paths = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        /**
         * 循环绘制集合里面的路径
         */
        for (int i = 0; i < paths.size(); i++) {
            PathModel p = paths.get(i);
            //每次绘制路径都需要设置画笔的宽度，颜色
            paint.setStrokeWidth(p.getWidth());
            paint.setColor(p.getColor());
            canvas.drawPath(p.getPath(), paint);
        }
    }
    Path path;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当手指按下的时候开始记录绘制路径
                path = new Path();
                PathModel p = new PathModel(paint.getColor(),((int) paint.getStrokeWidth()),path);

                paths.add(p);
                startX = event.getX();
                startY = event.getY();
                path.moveTo(startX, startY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        //刷新绘制画布
        invalidate();
        return true;
    }
    /**
     * 撤回
     */
    public void goBack() {
        //移除集合中最后一条路径 也就是刚才画的一条
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    /**
     * 清空画布
     */
    public void clear() {
        //清空集合中所有路径
        paths.clear();
        invalidate();
    }

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    /**
     * 设置画笔宽度
     *
     * @param width
     */
    public void setPaintWidth(int width) {
        paint.setStrokeWidth(width);
    }


    public Bitmap viewGetImage() {
        for (int i = 0; i < paths.size(); i++) {
            PathModel p = paths.get(i);
            //每次绘制路径都需要设置画笔的宽度，颜色
            paint.setStrokeWidth(p.getWidth());
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(p.getColor());
            canvas.drawPath(p.getPath(), paint);
        }
        this.setDrawingCacheEnabled(true);
        this.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //this.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        // 把一个View转换成图片
        return loadBitmapFromView(this);
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        //c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

}