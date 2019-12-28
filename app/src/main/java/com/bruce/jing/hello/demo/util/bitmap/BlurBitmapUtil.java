package com.bruce.jing.hello.demo.util.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import com.bruce.jing.hello.demo.util.log.JLogUtil;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;


public class BlurBitmapUtil {
    private static final String TAG = "BlurBitmapUtil";

    private static final float BITMAP_SCALE = 1.0f;

    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        blurScript.setRadius(blurRadius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        rs.destroy();

        return outputBitmap;
    }

    //羽化
    public static Bitmap feather(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        int ratio = w > h ? h * 32768 / w : w * 32768 / h;
        int r, g, b, color;

        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        float Size = 0.5f;
        int cx = w >> 1;
        int cy = h >> 1;
        int max = cx * cx + cy * cy;
        int min = (int) (max * (1 - Size));
        int diff = max - min;

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                color = oldPx[x * h + y];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);

                int dx = cx - x;
                int dy = cy - y;
                if (w > h) {
                    dx = (dx * ratio) >> 15;
                } else {
                    dy = (dy * ratio) >> 15;
                }
                int distSq = dx * dx + dy * dy;
                float v = ((float) distSq / diff) * 255;
                r = (int) (r + (v));
                g = (int) (g + (v));
                b = (int) (b + (v));
                //检查各点像素值是否超出范围
                r = (r > 255 ? 255 : (r < 0 ? 0 : r));
                g = (g > 255 ? 255 : (g < 0 ? 0 : g));
                b = (b > 255 ? 255 : (b < 0 ? 0 : b));
                newPx[x * h + y] = Color.rgb(r, g, b);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    private static Bitmap getBackground(View view, int x, int y, int width, int height) {
        long s = System.currentTimeMillis();
        JLogUtil.d(TAG, "getbackground start");
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bmp = view.getDrawingCache();
        if (bmp != null) {
            long l = System.currentTimeMillis() - s;
            JLogUtil.d(TAG, "getbackground end" + l);
            return Bitmap.createBitmap(bmp, x, y, width, height);
        }
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.eraseColor(Color.BLACK);

        return bmp;
    }

    public static Drawable getDefaultBlurDrawable(View view, int x, int y, int width, int height) {
        return getBlurDrawable(view, x, y, width, height, 0);
    }

    public static Drawable getBlurDrawable(View view, int x, int y, int width, int height, @DrawableRes int id) {
        Bitmap bitmap = getBackground(view, x, y, width, height);
        Bitmap blurBitmap = blurBitmap(view.getContext(), bitmap, 8);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(view.getResources(), blurBitmap);
        if (id == 0) {
            return bitmapDrawable;
        }
        Drawable[] array = new Drawable[2];
        array[0] = bitmapDrawable;
        array[1] = ContextCompat.getDrawable(view.getContext(), id);
        LayerDrawable la = new LayerDrawable(array);
        la.setLayerInset(0, 0, 0, 0, 0);
        la.setLayerInset(1, 0, 0, 0, 0);
        return la;
    }

}