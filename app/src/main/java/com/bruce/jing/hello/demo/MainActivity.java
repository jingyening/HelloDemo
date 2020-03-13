package com.bruce.jing.hello.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.bruce.jing.hello.demo.adapter.BaseRecyclerViewAdapter2;
import com.bruce.jing.hello.demo.adapter.SimpleItemAdapter;
import com.bruce.jing.hello.demo.util.system.DeviceUtil;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tmp.TmpTestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BaseRecyclerViewAdapter2.OnItemClickListener{

    private static final String TAG = "HelloDemo_MainActivity";




    private RecyclerView mRecyclerView;
    private SimpleItemAdapter mAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static final String[] sData = {
          "java",
          "test",
          "widget",
          "view",
          "animation"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                Log.d(TAG,"### debug performance:queueIdle");
                return false;
            }
        });
        DeviceUtil.setStatusBarTransparent(this);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rlv_mainPage);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
        };
        mRecyclerView.addItemDecoration(decoration);
        initData();
    }

    private void initData() {
        List<String> data = Arrays.asList(sData);
        mAdapter = new SimpleItemAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "### debug performance: onResume!!!!!" );
    }

    @Override
    public void onClick(View v) {

    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
        }
    };

    @Override
    public void onItemClick(int position, View itemView) {
        switch (position) {
            case 0:

                break;
            case 1:
                TmpTestActivity.launch(this);


//                intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);GIT
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);

//                MultiScreenAdapterActivity.launch(this);
//                EmptyActivity.launch(this);
                Log.d(TAG, "onItemClick() called with: position = [" + position + "], itemView = [" + itemView + "]");
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.example.lenovo.test","com.example.lenovo.test.component.TestService"));
//                intent.setAction("com.example.lenovo.test.component.TestService.bind");
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(intent);
//                }
//                bindService(intent, conn, BIND_AUTO_CREATE);
                break;
            case 2:

                break;
            case 3:
                ViewDemoActivity.launch(this);
                break;
            case 4:

                break;
            default:

                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.d(TAG,"onWindowFocusChanged stack = "+Log.getStackTraceString(new Throwable()));
    }
}
