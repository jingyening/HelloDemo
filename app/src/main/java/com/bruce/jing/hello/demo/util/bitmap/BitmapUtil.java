package com.bruce.jing.hello.demo.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.bruce.jing.hello.demo.util.log.JLogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * todo 搞清楚bitmap option的各个属性；
 *
 */
public class BitmapUtil {

    public static final String MIME_TYPE_VIDEO_PREFIX = "video";
    public static final String MIME_TYPE_IMAGE_PREFIX = "image";

    public static final String MIME_TYPE_PNG = "image/png";
    public static final String MIME_TYPE_JPG = "image/jpeg";
    public static final String MIME_TYPE_GIF = "image/gif";


    private static final String TAG = "BitmapUtil";

    public static void compressBitmap(){


    }

    private boolean compressBitmapFile(String filePath,int dstWidth, String targetFilePath) {
        boolean result = false;
        Bitmap targetBmp = compressBitmap(filePath,dstWidth);
        if(targetBmp == null){
            return result;
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(targetFilePath);
            result = targetBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!targetBmp.isRecycled()) {
            targetBmp.recycle();
        }

        return result;
    }

    public static Bitmap compressBitmap(String filePath, int dstMaxWidth){
        if (TextUtils.isEmpty(filePath)) {
            JLogUtil.d(TAG, "compressPhoto filepath empty");
            return null;
        }


        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
        if(bitmap == null){//无法decode，return
            return bitmap;
        }
        int width = option.outWidth;
        String mimeType = option.outMimeType;

        if (width <= dstMaxWidth) {//图片
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            return bmp;
        }

        if (TextUtils.isEmpty(mimeType)) {
            JLogUtil.d(TAG, "compressPhoto mimetype null");
            return null;
        }

        if (!MIME_TYPE_JPG.equals(mimeType) && !MIME_TYPE_PNG.equals(mimeType)) {
            JLogUtil.d(TAG, "compressPhoto mimetype not jpg or png");
            return null;
        }


        int scale = width / dstMaxWidth;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = scale;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);

        int dstHeight = (int) ((float) dstMaxWidth / bitmap.getWidth() * bitmap.getHeight());
        Bitmap targetBmp = Bitmap.createScaledBitmap(bmp, dstMaxWidth, dstHeight, true);
        if(bmp != null && !bmp.isRecycled()){
            bmp.recycle();
        }
        return  targetBmp;
    }



    public static void recycleBitmap(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }

    }
}
