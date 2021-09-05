package com.example.timbroapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {

    private Paint paint;
    private Integer colorCircle = Color.parseColor("#da4747");

    public DrawView(Context context, AttributeSet attr){
        super(context, attr);
        //this.setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        int radius;
        radius = 50;
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        paint.setColor(colorCircle);

        // draw circle
        canvas.drawCircle(x - 60, 60, radius, paint);
    }

    public void colorCircle(Integer color) {
        colorCircle = color;
        invalidate();
    }

    private void updateCircle(Canvas canvas, Integer color) {

    }

    public void test() {
        invalidate();
    }
}