package com.myfirstapp.myapplicationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by KUMUD on 7/22/2016.
*/
public class YearLine extends View {

    Paint brush;

    public YearLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas){
        brush = new Paint();
        brush.setColor(Color.argb(255, 58, 56, 56));
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeWidth(10);
        int x = getWidth();
        int y = getHeight();
        canvas.drawLine(0, y/2, x, y/2, brush);
    }
}
