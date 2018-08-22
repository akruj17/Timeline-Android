package com.myfirstapp.myapplicationtest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by KUMUD on 8/19/2016.
 */
public class BackgroundImage {

    private int width;
    private int height;
    private int scaledHeight;
    private int scaledWidth;
    private Context context;
    private Bitmap picture;
    private int color;

    public BackgroundImage(Context c, int width, int height, Bitmap picture)
    {
        context = c;
        this.width = width;
        this.height = height;
        this.picture = picture;
    }

    public int getBackgroundImageWidth(){
        return scaledWidth;
    }

    public int getBackgroundImageHeight(){
        return scaledHeight;
    }

    public BackgroundImageView createBackgroundImage(int layoutHeight)
    {
        double scaleFactor = (double)height / layoutHeight;
        scaledHeight = (int)(height / scaleFactor);
        scaledWidth = (int)(width / scaleFactor);

        Bitmap scaleOriginalImage = Bitmap.createScaledBitmap(picture, scaledWidth, scaledHeight, true);
        Bitmap result = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(result);

        Paint paint = new Paint();
        canvas.drawBitmap(scaleOriginalImage, 0, 0, paint);

        int[] colors = {0x00000000, 0xcc000000, 0x00000000};
        float[] positions = {0, 0.5f, 1};
        Shader mShader = new LinearGradient(0, (scaledHeight/2), scaledWidth, (scaledHeight/2), colors, positions, Shader.TileMode.CLAMP);
        paint.setShader(mShader);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0,0, scaledWidth, scaledHeight, paint);
        paint.setXfermode(null);

        BackgroundImageView image = new BackgroundImageView(context);
        BitmapDrawable resultImage = new BitmapDrawable(Resources.getSystem(), result);
        image.backgroundImage = this;
        image.setBackground(resultImage);
        image.setAdjustViewBounds(true);

        scaleOriginalImage.recycle();

        Palette palette = Palette.from(result).generate();
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch != null) {
            color = swatch.getRgb();
        }
        else {
            swatch = palette.getDarkVibrantSwatch();
            if (swatch != null) {
                color = swatch.getRgb();
            }
            else {
                color = 0xFF000000;
            }
        }

        LinearLayout.LayoutParams r = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        r.setMargins(-100, 0, 0, 0);
        image.setLayoutParams(r);
        return image;
    }

    public int getColor(){
        return color;
    }

}
