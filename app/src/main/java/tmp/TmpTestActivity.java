package tmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.jing.hello.demo.BaseActivity;
import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.fragment.EmojiFragment;
import com.bruce.jing.hello.demo.fragment.WTFragmentOp;
import com.bruce.jing.hello.demo.util.StringUtils;
import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;
import com.bruce.jing.hello.demo.widget.view.OnlineAvatarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TmpTestActivity extends BaseActivity implements View.OnClickListener{


    private static final String TAG = "TmpTestActivity";

    private Object mObject = new Object();
    TextView textView;

    Fragment mFragment = new EmojiFragment();

    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,TmpTestActivity.class);
        if(!(context instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.setStatusBarTransparent(this);
        setContentView(R.layout.activity_tmp_test);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int width = metrics.widthPixels;
//        int height = metrics.heightPixels;
//        final float density = metrics.density;
//        int dpi = metrics.densityDpi;
//        Log.d("brucetest","DisplayMetrics = "+ metrics.toString());
//        float dimenValue = getResources().getDimension(R.dimen.test_dimen_value);
//        Log.d("brucetest","dimenValue = "+dimenValue);
        final ImageView iv = (ImageView) findViewById(R.id.imageView);

        iv.setOnClickListener(this);

        JLogUtil.d("brucetest"," b = "+ Build.BRAND +" v = "+Build.VERSION.SDK_INT+" "+Build.MODEL);
        JLogUtil.d("brucetest", StringUtils.toString(Build.class));
        JLogUtil.d("brucetest",getFilesDir().getAbsolutePath());
        testSharePreferenceCreate();

        long time = System.currentTimeMillis();
        String date = new Date().toString();
        JLogUtil.d("brucetest","time = "+time);
        JLogUtil.d("brucetest","date = "+date);

        List<Object> data = new ArrayList<>();
        for (int i = 0; i < 20; i++ ){
            data.add(new Object());
        }

        OnlineAvatarView avatarView2 = (OnlineAvatarView) findViewById(R.id.onlineAvatarView2);
        avatarView2.setData(data);

        textView = (TextView) findViewById(R.id.textView);
        TextView songName = (TextView) findViewById(R.id.tv_player_songname);
        songName.setText("隐形的纪念 (the hidden version)");



    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HelloDemo_MainActivity","TmpTestActivity onResume");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("HelloDemo_MainActivity","TmpTestActivity onWindowFocusChanged hasFocus = "+hasFocus);
    }

    private static final String TEST_TEXT = "ksjdo说的可否接fjn%%*dsl@@fj、ds测试  12121532圣诞节佛is电脑的你佛山的减肥你受老师的经费都是  是放假哦I小聪明分配我I小品剧似的  学成绩哦is等果果  545 11 87 8 2148132 送豆腐街的身份减速带范德萨 是地府地方弄额我fin分";
    private static final String TEST_TEXT2 = "";
    private void testStaticLayout() {
        String substring = TEST_TEXT2;
        TextPaint mLrcPaint = new TextPaint();
        mLrcPaint.setAntiAlias(true);
        float mCurrentTextSize = 38F;
        mLrcPaint.setTextSize(mCurrentTextSize);
        mLrcPaint.setTextAlign(Paint.Align.LEFT);
        int width = 700;
        Layout.Alignment align = Layout.Alignment.ALIGN_NORMAL;
        StaticLayout staticLayout1 = new StaticLayout(substring, mLrcPaint, width, align, 1.2f, 0f, false);
        int lineCount = staticLayout1.getLineCount();
        if (lineCount > 2) {
            int secondLineEnd = staticLayout1.getLineEnd(2);
            Log.d(TAG,"secondLineEnd = "+secondLineEnd);
            substring = substring.substring(0, secondLineEnd);
        }
        StaticLayout staticLayout = new StaticLayout(substring, mLrcPaint, width, align, 1.2f, 0f, false);
        Log.d(TAG,"staticLayout height = "+staticLayout.getHeight());

    }

    private void testSharePreferenceCreate() {
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                SharedPreferences sp01 = getSharedPreferences("/test01/001",Context.MODE_PRIVATE);
//                sp01.edit().putBoolean("ffffff",true).commit();
//                SharedPreferences sp03 = getSharedPreferences("/test01/002",Context.MODE_PRIVATE);
//                sp03.edit().putInt("ABCD",123).commit();
//                SharedPreferences sp02 = getSharedPreferences("/test02/001",Context.MODE_PRIVATE);
//                sp02.edit().putString("dfdfsa","ok").commit();
//                JLogUtil.d("brucetest","write share over");
//            }
//        }.start();
    }

    private boolean isScaleUp;

    private void scaleUpPhoto(ImageView view) {
//        Matrix matrix = new Matrix();
//        int dw = view.getDrawable().getIntrinsicWidth();
//        int dh = view.getDrawable().getIntrinsicHeight();
//
//        matrix.setTranslate(-dw/2,-dh/2);
//        matrix.postScale(1.2f, 1.2f, dw / 2, dh / 2);
//        view.setImageMatrix(matrix);
        view.setScaleX(1.2f);
        view.setScaleY(1.2f);
        isScaleUp = true;
    }

    private void scaleDownPhoto(ImageView view) {
//        Matrix matrix = new Matrix(view.getImageMatrix());
//        int dw = view.getDrawable().getIntrinsicWidth();
//        int dh = view.getDrawable().getIntrinsicHeight();
//        matrix.setTranslate(-dw/2,-dh/2);
//        matrix.postScale(1f, 1f, dw / 2, dh / 2);
//        view.setImageMatrix(matrix);
        view.setScaleX(1f);
        view.setScaleY(1f);
        isScaleUp = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
//                TestLock.testAwaitAndCountDown();
//                JLogUtil.d(TmpTestActivity.class,Integer.toBinaryString(-38));

//                TmpTestActivity2.launch(this,1);
//                testStaticLayout();

                WTFragmentOp instance = WTFragmentOp.getInstance(this);
                instance.submitOnMainThreadSafely(this, new WTFragmentOp.AbsCommitAction(){
                    @Override
                    public void commitFragmentTransaction() {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment emoji_fragment = fm.findFragmentByTag("emoji_fragment");
                        if(emoji_fragment == null){
                            ft.add(R.id.thumb111, mFragment,"emoji_fragment");
                        }else{
                            ft.show(emoji_fragment);
                        }
                        ft.commitAllowingStateLoss();
                        fm.executePendingTransactions();
                    }
                }, WTFragmentOp.OpType.PENDING);

                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TmpTestActivity3.launch(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(mFragment);
        WTFragmentOp instance = WTFragmentOp.getInstance(this);
        WTFragmentOp.AbsCommitAction absCommitAction = instance.generateCommitAction(fm, ft);
        instance.submitOnMainThreadSafely(this, absCommitAction, WTFragmentOp.OpType.PENDING);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG,"onRestoreInstanceState");
    }

    @Override
    public void onRemainActionOperate(List<WTFragmentOp.CommitAction> remainAction) {
        super.onRemainActionOperate(remainAction);
        Log.d(TAG,"onRemainActionOperate remainAction = "+remainAction.size());
        for (WTFragmentOp.CommitAction commitAction : remainAction) {
            WTFragmentOp.getInstance(this).submitOnMainThreadSafely(this,(WTFragmentOp.AbsCommitAction) commitAction, WTFragmentOp.OpType.PENDING);
        }
    }
}
