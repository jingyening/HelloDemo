package tmp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.StringUtils;
import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;
import com.bruce.jing.hello.demo.widget.view.OnlineAvatarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TmpTestActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "MainActivity";

    private Object mObject = new Object();


    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,TmpTestActivity.class);
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
        final ImageView iv = findViewById(R.id.imageView);

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

        OnlineAvatarView avatarView2 = findViewById(R.id.onlineAvatarView2);
        avatarView2.setData(data);



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

                TmpTestActivity2.launch(this,1);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TmpTestActivity3.launch(this);
    }
}
