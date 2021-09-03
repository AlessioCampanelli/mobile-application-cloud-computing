package com.example.timbroapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {

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
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.RED);
        canvas.drawPaint(paint);
        paint.setColor(Color.parseColor("#da4747"));

        // draw circle
        canvas.drawCircle(x - 60, 60, radius, paint);

        // draw text
            /*paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            canvas.drawText("Title: " + stampings.get(n_timbro).getTitle(), 30, 60, paint);
            canvas.drawText("Address: " + stampings.get(n_timbro).getAddress(), 30, 120, paint);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+2"));
            Date start_time = new java.util.Date(Integer.parseInt(stampings.get(n_timbro).getStartTime())*1000L);
            String format_start_time = sdf.format(start_time);
            canvas.drawText("Start Time: " + format_start_time, 30, 180, paint);
            Date end_time = new java.util.Date(Integer.parseInt(stampings.get(n_timbro).getEndTime())*1000L);
            String format_end_time = sdf.format(end_time);
            canvas.drawText("End Time: " + format_end_time, 30, 240, paint);*/

    }

    public void test() {
        invalidate();
    }
}