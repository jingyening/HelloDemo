package com.bruce.jing.hello.demo.widget.view.animation;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;

import static com.bruce.jing.hello.demo.widget.view.animation.FrameAnimationHelper.RepeatMode.MODE_INFINITE;
import static com.bruce.jing.hello.demo.widget.view.animation.FrameAnimationHelper.RepeatMode.MODE_ONCE;

/**
 * use View play Frame Animation with lots of pic,
 * avoids OOM and ANR
 * 参考资料：
 * address:https://github.com/yuyashuai/SilkyAnimation
 */
final class FrameAnimationHelper {


    public static final int MIN_FRAME_INTERVAL = 10;

    /**
     * 缓存的图片
     */
    private final SparseArray<Bitmap> mBitmapCache;
    private View mRenderView;
    /**
     * 存储图片的所有路径
     */
    private List<String> mPathList;
    private RepeatMode mode = MODE_ONCE;
    /**
     * 是否从asset中读取资源
     */
    private boolean isAssetResource = false;
    private AssetManager mAssetManager;
    private final String TAG = "FrameAnimationHelper";
    private Matrix mDrawMatrix;
    private int mScaleType;
    private Context mContext;
    /**
     * total frames.
     */
    private int mTotalCount;

    /**
     * handler of the thread that in charge of loading bitmap.
     */
    private Handler mDecodeHandler;

    /**
     * time interval between two frames.
     */
    private int mFrameInterval = MIN_FRAME_INTERVAL;

    private int mDuration = 300;
    /**
     * number of frames resides in memory. real cache count
     */
    private int mCacheCount = 3;

    /**
     * 是否支持inBitmap
     */
    private boolean mSupportInBitmap = true;

    /**
     * pass cache count
     */
    private int mPassCacheCount = 5;
    /**
     * callback of animation state.
     */
    private AnimationStateListener mAnimationStateListener;


    /**
     * start animation command.
     */
    private final int CMD_START_ANIMATION = -1;

    /**
     * stop animation command.
     */
    private final int CMD_STOP_ANIMATION = -2;


    FrameAnimationHelper(View view) {
        mBitmapCache = new SparseArray<>();
        mRenderView = view;
        mContext = view.getContext();
        mDrawMatrix = new Matrix();
        mScaleType = SCALE_TYPE_FIT_CENTER;
    }

    public enum RepeatMode {
        /**
         * Repeat the animation once.
         */
        MODE_ONCE(1),
        /**
         * Repeat the animation indefinitely.
         */
        MODE_INFINITE(2);

        RepeatMode(int value) {

        }
    }

    /**
     * 给定的matrix
     */
    private final int SCALE_TYPE_MATRIX = 0;
    /**
     * 完全拉伸，不保持原始图片比例，铺满
     */
    public static final int SCALE_TYPE_FIT_XY = 1;

    /**
     * 保持原始图片比例，整体拉伸图片至少填充满X或者Y轴的一个
     * 并最终依附在视图的上方或者左方
     */
    public static final int SCALE_TYPE_FIT_START = 2;

    /**
     * 保持原始图片比例，整体拉伸图片至少填充满X或者Y轴的一个
     * 并最终依附在视图的中心
     */
    public static final int SCALE_TYPE_FIT_CENTER = 3;

    /**
     * 保持原始图片比例，整体拉伸图片至少填充满X或者Y轴的一个
     * 并最终依附在视图的下方或者右方
     */
    public static final int SCALE_TYPE_FIT_END = 4;

    /**
     * 将图片置于视图中央，不缩放
     */
    public static final int SCALE_TYPE_CENTER = 5;

    /**
     * 整体缩放图片，保持原始比例，将图片置于视图中央，
     * 确保填充满整个视图，超出部分将会被裁剪
     */
    public static final int SCALE_TYPE_CENTER_CROP = 6;

    /**
     * 整体缩放图片，保持原始比例，将图片置于视图中央，
     * 确保X或者Y至少有一个填充满屏幕
     */
    public static final int SCALE_TYPE_CENTER_INSIDE = 7;

    /**
     * 第一帧动画的偏移量
     */
    private int startOffset = 0;

    @IntDef({SCALE_TYPE_FIT_XY, SCALE_TYPE_FIT_START, SCALE_TYPE_FIT_CENTER, SCALE_TYPE_FIT_END,
            SCALE_TYPE_CENTER, SCALE_TYPE_CENTER_CROP, SCALE_TYPE_CENTER_INSIDE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScaleType {

    }



    /**
     * 设置是否支持inBitmap，支持inBitmap会非常显著的改善内存抖动的问题
     * 因为存在bitmap复用的问题，当设置支持inBitmap时，请务必保证帧动画
     * 所有的图片分辨率和颜色位数完全一致。默认为true。
     *
     * @param support
     * @see <a href="google">https://developer.android.com/reference/android/graphics/BitmapFactory.Options.html#inBitmap</a>
     */
    public void setSupportInBitmap(boolean support) {
        this.mSupportInBitmap = support;
    }

    /**
     * 通过assets资源转换pathList
     *
     * @param assetsPath assets resource path, must be a directory
     * @return if assets  does not exist return a empty list
     */
    List<String> getPathList(String assetsPath) {
        AssetManager assetManager = mContext.getAssets();
        try {
            String[] assetFiles = assetManager.list(assetsPath);
            if (assetFiles.length == 0) {
                Log.e(TAG, "no file in this asset directory");
                return new ArrayList<>(0);
            }
            //转换真实路径
            for (int i = 0; i < assetFiles.length; i++) {
                assetFiles[i] = assetsPath + File.separator + assetFiles[i];
            }
            List<String> mAssertList = Arrays.asList(assetFiles);
            isAssetResource = true;
            setAssetManager(assetManager);
            return mAssertList;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

    /**
     * 通过File资源转换pathList
     *
     * @param file the resources directory
     * @return if file does not exist return a empty list
     */
    private List<String> getPathList(File file) {
        List<String> list = new ArrayList<>();
        if (file != null) {
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                for (File mFrameFile : files) {
                    list.add(mFrameFile.getAbsolutePath());
                }
            } else if (!file.exists()) {
                Log.e(TAG, "file doesn't exists");
            } else {
                Log.e(TAG, "file isn't a directory");
            }
        } else {
            Log.e(TAG, "file is null");
        }
        isAssetResource = false;
        return list;
    }

    void initPathList(List<String> pathList) {
        this.mPathList = pathList;
        if (mPathList == null) {
            throw new NullPointerException("pathList is null. ensure you have configured the resources correctly");
        }
        mCacheCount = mPassCacheCount;
        if (mCacheCount > mPathList.size()) {
            mCacheCount = mPathList.size();
        }
        Collections.sort(pathList);
    }

    /**
     * start animation
     *
     * @param file the resources directory,
     */
    public void start(File file) {
        if (isDrawing) {
            stop();
        }
        initPathList(getPathList(file));
        start(0);
    }


    /**
     * start animation
     *
     * @param filePathList the file resources path list
     */
    public void startWithFilePathList(List<String> filePathList) {
        if (isDrawing) {
            stop();
        }
        isAssetResource = false;
        initPathList(filePathList);
        start(0);
    }

    /**
     * start animation
     *
     * @param filePathList the file resources path list
     */
    public void startWithFilePathList(List<String> filePathList, int position) {
        if (isDrawing) {
            stop();
        }
        isAssetResource = false;
        initPathList(filePathList);
        start(position);
    }

    /**
     * start animation
     *
     * @param assetsPathList the file resources path list
     */
    public void startWithAssetsPathList(List<String> assetsPathList, int position) {
        if (isDrawing) {
            stop();
        }
        isAssetResource = true;
        initPathList(assetsPathList);
        start(position);
    }

    /**
     * start animation
     *
     * @param assetsPathList the file resources path list
     */
    public void startWithAssetsPathList(List<String> assetsPathList) {
        if (isDrawing) {
            stop();
        }
        isAssetResource = true;
        initPathList(assetsPathList);
        start(0);
    }


    /**
     * start animation ,if you call this directly, you must initial the resources
     */
    public void start() {
        if (isDrawing) {
            stop();
        }
        start(0);
    }

    /**
     * start animation ,if you call this directly, you must initial the resources
     *
     * @param position start offset
     */
    public void start(int position) {
        mInBitmapFlag = 0;
        mReuseBitmap = null;
        if (isDrawing) {
            stop();
        }
        startOffset = position;
        if (mPathList == null) {
            throw new NullPointerException("the frame list is null. did you have configured the resources? if not please call start(file) or start(assetsPath)");
        }
        if (mPathList.isEmpty()) {
            Log.e(TAG, "pathList is empty, nothing to display. ensure you have configured the resources correctly. check you file or assets directory ");
            return;
        }
        if (startOffset >= mPathList.size()) {
            throw new IndexOutOfBoundsException("invalid startOffset index " + position + ", size is " + mPathList.size());
        }
        //从文件中读取
        if (!isAssetResource) {
            File file = new File(mPathList.get(0));
            if (!file.exists()) {
                return;
            }
        }
        mTotalCount = mPathList.size();
        mFrameInterval = Math.max(mDuration / mTotalCount, MIN_FRAME_INTERVAL);
        startDecodeThread();
    }

    private void setAssetManager(AssetManager assetManager) {
        this.mAssetManager = assetManager;
    }


    void setDuration(int duration) {
        if (mDuration > 0) {
            mDuration = duration;
        }
    }

    /**
     * 给定绘制bitmap的matrix不能和设置ScaleType同时起作用
     *
     * @param matrix 绘制bitmap时应用的matrix
     */
    public void setMatrix(@NonNull Matrix matrix) {
        if (matrix == null) {
            throw new NullPointerException("matrix can not be null");
        }
        mDrawMatrix = matrix;
        mScaleType = SCALE_TYPE_MATRIX;
    }

    public void stop() {
        if (!isDrawing()) {
            return;
        }
        stopAnim();
    }

    void setScaleType(int type) {
        if (type < SCALE_TYPE_FIT_XY || type > SCALE_TYPE_CENTER_INSIDE) {
            throw new IllegalArgumentException("Illegal ScaleType");
        }
        if (mScaleType != type) {
            mScaleType = type;
        }
    }

    private int mLastFrameWidth = -1;
    private int mLastFrameHeight = -1;
    private int mLastFrameScaleType = -1;
    private int mLastSurfaceWidth;
    private int mLastSurfaceHeight;

    /**
     * 根据ScaleType配置绘制bitmap的Matrix
     *
     * @param bitmap
     */
    private void configureDrawMatrix(Bitmap bitmap) {
        final int srcWidth = bitmap.getWidth();
        final int dstWidth = mRenderView.getWidth();
        final int srcHeight = bitmap.getHeight();
        final int dstHeight = mRenderView.getHeight();
        final boolean nothingChanged =
                srcWidth == mLastFrameWidth
                        && srcHeight == mLastFrameHeight
                        && mLastFrameScaleType == mScaleType
                        && mLastSurfaceWidth == dstWidth
                        && mLastSurfaceHeight == dstHeight;
        if (nothingChanged) {
            return;
        }
        mLastFrameScaleType = mScaleType;
        mLastFrameHeight = bitmap.getHeight();
        mLastFrameWidth = bitmap.getWidth();
        mLastSurfaceHeight = mRenderView.getHeight();
        mLastSurfaceWidth = mRenderView.getWidth();
        if (mScaleType == SCALE_TYPE_MATRIX) {
            return;
        } else if (mScaleType == SCALE_TYPE_CENTER) {
            mDrawMatrix.setTranslate(
                    Math.round((dstWidth - srcWidth) * 0.5f),
                    Math.round((dstHeight - srcHeight) * 0.5f));
        } else if (mScaleType == SCALE_TYPE_CENTER_CROP) {
            float scale;
            float dx = 0, dy = 0;
            //按照高缩放
            if (dstHeight * srcWidth > dstWidth * srcHeight) {
                scale = (float) dstHeight / (float) srcHeight;
                dx = (dstWidth - srcWidth * scale) * 0.5f;
            } else {
                scale = (float) dstWidth / (float) srcWidth;
                dy = (dstHeight - srcHeight * scale) * 0.5f;
            }
            mDrawMatrix.setScale(scale, scale);
            mDrawMatrix.postTranslate(dx, dy);
        } else if (mScaleType == SCALE_TYPE_CENTER_INSIDE) {
            float scale;
            float dx;
            float dy;
            //小于dst时不缩放
            if (srcWidth <= dstWidth && srcHeight <= dstHeight) {
                scale = 1.0f;
            } else {
                scale = Math.min((float) dstWidth / (float) srcWidth,
                        (float) dstHeight / (float) srcHeight);
            }
            dx = Math.round((dstWidth - srcWidth * scale) * 0.5f);
            dy = Math.round((dstHeight - srcHeight * scale) * 0.5f);

            mDrawMatrix.setScale(scale, scale);
            mDrawMatrix.postTranslate(dx, dy);
        } else {
            RectF srcRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF dstRect = new RectF(0, 0, mRenderView.getWidth(), mRenderView.getHeight());
            mDrawMatrix.setRectToRect(srcRect, dstRect, MATRIX_SCALE_ARRAY[mScaleType - 1]);
        }
    }

    private static final Matrix.ScaleToFit[] MATRIX_SCALE_ARRAY = {
            Matrix.ScaleToFit.FILL,
            Matrix.ScaleToFit.START,
            Matrix.ScaleToFit.CENTER,
            Matrix.ScaleToFit.END
    };

    void setCacheCount(int count) {
        mPassCacheCount = count;
    }

    public void setRepeatMode(RepeatMode mode) {
        this.mode = mode;
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public long getFrameInterval(){
        return mFrameInterval;
    }

    public void setAnimationStateListener(AnimationStateListener animationStateListener) {
        this.mAnimationStateListener = animationStateListener;
    }

    /**
     * Animation状态监听
     */
    public interface AnimationStateListener {
        /**
         * 动画开始
         */
        void onFrameAnimStart();

        /**
         * 动画结束
         */
        void onFrameAnimFinish();

        /**
         * 异常中止
         * @param position
         */
        void onFrameAnimUnexpectedStop(int position);
    }




    private int position;
    private boolean isDrawing = false;
//        private Thread drawThread;


//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//            if (isDrawing) {
//                stopAnim();
//                if (mUnexceptedListener != null) {
//                    mUnexceptedListener.onUnexceptedStop(getCorrectPosition());
//                }
//            }
//        }

    private long mLastFrameDrawTime = 0;
    /**
     * 绘制
     */
    public void drawBitmap(Canvas canvas) {
        //当循环播放时，获取真实的position
        if(mDecodeHandler == null){
            return;
        }
        if (canvas == null) {
            return;
        }

        if (mode == MODE_INFINITE && position >= mTotalCount) {
            position = position % mTotalCount;
        }
        if (position >= mTotalCount) {
            mDecodeHandler.sendEmptyMessage(CMD_STOP_ANIMATION);
//                clearSurface();
            return;
        }
        if (mBitmapCache.get(position, null) == null) {
            Log.e(TAG, "get bitmap in position: " + position + " is null");
//            stopAnim();
            return;
        }

        final Bitmap currentBitmap = mBitmapCache.get(position);

//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        configureDrawMatrix(currentBitmap);
        canvas.drawBitmap(currentBitmap, mDrawMatrix, null);
        Log.d(TAG, "drawBitmap: this = "+this+";  position = "+ position +" mFrameInterval = "+mFrameInterval);

        if(mLastFrameDrawTime == 0){
            mLastFrameDrawTime = System.currentTimeMillis();
            mDecodeHandler.sendEmptyMessage(position);
            position++;
        }else if(System.currentTimeMillis() - mLastFrameDrawTime >= mFrameInterval){
            mLastFrameDrawTime = System.currentTimeMillis();
            mDecodeHandler.sendEmptyMessage(position);
            position++;
        }
    }

//        private void clearSurface() {
//            try {
//                if (mCanvas != null) {
//                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    private void startAnim() {
        if (mAnimationStateListener != null) {
            mAnimationStateListener.onFrameAnimStart();
        }
        isDrawing = true;
        position = startOffset;
    }


    private void stopAnim() {
        if (!isDrawing) {
            return;
        }
        isDrawing = false;
        position = 0;
        mBitmapCache.clear();
//            clearSurface();
        if (mDecodeHandler != null) {
            mDecodeHandler.sendEmptyMessage(CMD_STOP_ANIMATION);
        }
        if (mAnimationStateListener != null) {
            mAnimationStateListener.onFrameAnimFinish();
        }
        mReuseBitmap = null;
        mLastFrameDrawTime = 0;
    }

    /**
     * decode线程
     */
    private void startDecodeThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();

                mDecodeHandler = new Handler(Looper.myLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == CMD_STOP_ANIMATION) {
                            decodeBitmap(CMD_STOP_ANIMATION);
                            getLooper().quit();
                            mDecodeHandler = null;
                            return;
                        }
                        decodeBitmap(msg.what);
                    }
                };
                decodeBitmap(CMD_START_ANIMATION);
                Looper.loop();
            }
        }.start();
    }

    /**
     * in bitmap，避免频繁的GC
     */
    private Bitmap mReuseBitmap = null;
    /**
     * 作为一个标志位来标志是否应该初始化或者更新inBitmap，
     * 因为SurfaceView的双缓存机制，不能绘制完成直接就覆盖上一个bitmap
     * 此时surfaceView还没有post上一帧的数据，导致覆盖bitmap之后出现显示异常
     */
    private int mInBitmapFlag = 0;

    /**
     * 传入inBitmap时的decode参数
     */
    private BitmapFactory.Options mOptions;

    /**
     * 根据不同指令 进行不同操作，
     * 根据position的位置来缓存position后指定数量的图片
     *
     * @param position 小于0时，为handler发出的命令. 大于0时为当前帧
     */
    private void decodeBitmap(int position) {
        if (position == CMD_START_ANIMATION) {
            //初始化存储
            if (mSupportInBitmap) {
                mOptions = new BitmapFactory.Options();
                mOptions.inMutable = true;
                mOptions.inSampleSize = 1;
            }
            for (int i = startOffset; i < mCacheCount + startOffset; i++) {
                int putPosition = i;
                if (putPosition > mTotalCount - 1) {
                    putPosition = putPosition % mTotalCount;
                }
                mBitmapCache.put(putPosition, decodeBitmapReal(mPathList.get(putPosition)));
            }
            startAnim();
        } else if (position == CMD_STOP_ANIMATION) {
            stopAnim();
        } else if (mode == MODE_ONCE) {
            if (position + mCacheCount <= mTotalCount - 1) {
                //由于surface的双缓冲，不能直接复用上一帧的bitmap，因为上一帧的bitmap可能还没有post
                updateReuseBitmap(position);
                mBitmapCache.put(position + mCacheCount, decodeBitmapReal(mPathList.get(position + mCacheCount)));
            }
            //循环播放
        } else if (mode == MODE_INFINITE) {
            //由于surface的双缓冲，不能直接复用上一帧的bitmap，上一帧的bitmap可能还没有post
            updateReuseBitmap(position);
            //播放到尾部时，取mod
            if (position + mCacheCount > mTotalCount - 1) {
                mBitmapCache.put((position + mCacheCount) % mTotalCount, decodeBitmapReal(mPathList.get((position + mCacheCount) % mTotalCount)));
            } else {
                mBitmapCache.put(position + mCacheCount, decodeBitmapReal(mPathList.get(position + mCacheCount)));
            }
        }
    }

    /**
     * 根据position的位置找一个用来复用的inBitmap
     *
     * @param position
     */
    private void updateReuseBitmap(int position) {
        if (!mSupportInBitmap) {
            mBitmapCache.remove(position);
            return;
        }
        mInBitmapFlag++;
        if (mInBitmapFlag > 1) {
            int writePosition = position - 1;
            //得到正确的position
            if (writePosition < 0) {
                writePosition = mTotalCount + writePosition;
            }
            mReuseBitmap = mBitmapCache.get(writePosition);
            mBitmapCache.remove(writePosition);
        }
    }

    /**
     * 根据不同的情况，选择不同的加载方式
     *
     * @param path
     * @return
     */
    private Bitmap decodeBitmapReal(String path) {
        if (mReuseBitmap != null) {
            mOptions.inBitmap = mReuseBitmap;
        }
        if (isAssetResource) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mAssetManager.open(path), null, mOptions);
                return bitmap;
            } catch (IOException e) {
                stop();
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Problem decoding into existing bitmap") && mSupportInBitmap) {
                    Log.e(TAG, "Make sure the resolution of all images is the same, if not call 'setSupportInBitmap(false)'.\n but this will lead to frequent gc ");
                }
                throw e;
            }
        } else {
            return BitmapFactory.decodeFile(path, mOptions);
        }
        return null;
    }

}
