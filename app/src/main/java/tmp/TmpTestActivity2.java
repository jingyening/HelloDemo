package tmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.adapter.SimpleItemAdapter;
import com.bruce.jing.hello.demo.java.concurrent.lock.TestLock;
import com.bruce.jing.hello.demo.util.StringUtils;
import com.bruce.jing.hello.demo.util.log.JLogUtil;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

public class TmpTestActivity2 extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "MainActivity";

    private Object mObject = new Object();

    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,TmpTestActivity2.class);
        context.startActivity(intent);
    }

    public static void launch(Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context,TmpTestActivity2.class);
        context.startActivityForResult(intent,requestCode);
    }

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_test2);
        recyclerView = findViewById(R.id.rlv_test_scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        List<String> data = new ArrayList();
        data.add("TEST!!!");
        SimpleItemAdapter adapter = new SimpleItemAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                setResult(Activity.RESULT_OK, new Intent());
                break;
            default:
                break;
        }
    }
}
