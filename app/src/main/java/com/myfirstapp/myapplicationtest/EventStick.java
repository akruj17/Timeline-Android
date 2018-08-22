package com.myfirstapp.myapplicationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by KUMUD on 8/25/2016.
 */
public class EventStick extends View {

    Paint brush;
    int color = 0xff303f9f;

    public EventStick(Context context)
    {
        super(context);
    }

    public void onDraw(Canvas c) {
        brush = new Paint();
        brush.setColor(color);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeWidth(10);
        int x = getWidth();
        int y = getHeight();
        c.drawLine(x / 2, y, x / 2, 0, brush);
    }
}