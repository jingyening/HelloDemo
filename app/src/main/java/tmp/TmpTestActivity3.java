package tmp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.java.concurrent.lock.TestLock;
import com.bruce.jing.hello.demo.util.StringUtils;
import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;

public class TmpTestActivity3 extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "MainActivity";

    private Object mObject = new Object();

    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,TmpTestActivity3.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_test3);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                break;
            default:
                break;
        }
    }
}
