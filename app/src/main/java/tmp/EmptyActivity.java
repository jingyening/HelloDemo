package tmp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.StringUtils;
import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;
import com.bruce.jing.hello.demo.widget.view.OnlineAvatarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmptyActivity extends AppCompatActivity{


    private static final String TAG = "EmptyActivity";


    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, EmptyActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HelloDemo_MainActivity","EmptyActivity onResume");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("HelloDemo_MainActivity","EmptyActivity onWindowFocusChanged hasFocus = "+hasFocus);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP){
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
