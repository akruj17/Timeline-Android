package com.myfirstapp.myapplicationtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import java.util.Comparator;

/**
 * Created by KUMUD on 10/9/2016.
 */

public class BackgroundImageView extends ImageView {

    public boolean isChecked = false;
    public BackgroundImage backgroundImage;

    public BackgroundImageView(Context context) {
        super(context);
    }

    public void isChecked(boolean checked){
        isChecked = checked;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        if(isChecked) {
            super.onDraw(canvas);
            Bitmap checkmark = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_circle_black_48dp);
            int checkwidth = checkmark.getWidth();
            int checkheight = checkmark.getHeight();
            int x = getWidth();
            int y = getHeight();
            canvas.drawBitmap(checkmark, ((x / 2) - (checkwidth / 2)), ((y / 2) - (checkheight / 2)), new Paint());
            //canvas.drawColor(0x58686868);
        }
    }

}
