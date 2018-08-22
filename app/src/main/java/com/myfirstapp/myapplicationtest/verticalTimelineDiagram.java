package com.myfirstapp.myapplicationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by KUMUD on 10/21/2016.
 */

public class verticalTimelineDiagram extends View {

    private Paint brush;
    public int color;
    public int startYear;
    public int endYear;
    public int actualYear;
    public String yearString;


    public verticalTimelineDiagram(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public void setUp(int start, int end, int year, int color)
    {
        startYear = start;
        endYear = end;
        this.color = color;
        actualYear = year;
        yearString = Integer.toString(actualYear);
        invalidate();
    }

    protected void onDraw(Canvas canvas){
        brush = new Paint();
        brush.setColor(color);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeWidth(40);
        int x = getWidth();
        int y = getHeight();
        float lineStartPoint = (0.15f) * y;
        float lineEndPoint = (0.85f) * y;
        canvas.drawLine(0, lineStartPoint, 0, lineEndPoint, brush);

        int innerRange = actualYear-startYear;
        int outerRange = endYear-startYear;
        float position;
        if(outerRange != 0)
        {
           position = (float)innerRange / outerRange;
        }
        else
        {
            position = 0f;
        }
        position = (position * (lineEndPoint-lineStartPoint)) + lineStartPoint;
        brush.setStrokeWidth(10);
        canvas.drawLine(0, position, (0.70f*x), position, brush);

        canvas.drawLine((0.70f*x), position, x, position-(0.06f*y), brush);
        canvas.drawLine((0.70f*x), position, x, position+(0.06f*y), brush);

        brush.setStrokeWidth(20);
        canvas.drawLine(x, position-(0.055f*y), x, 0, brush);
        canvas.drawLine(x, position+(0.055f*y), x, y, brush);

        //canvas.drawLine((0.12f*x), (0.055f*y), x, (0.055f*y), brush);
        //canvas.drawLine((0.12f*x), (0.945f*y), x, (0.945f*y), brush);

        brush.setStrokeWidth(2);
        brush.setTextSize(20);
        brush.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(yearString, (0.40f*x), position-15, brush);


    }
}
