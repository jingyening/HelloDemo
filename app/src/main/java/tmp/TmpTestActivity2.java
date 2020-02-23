package tmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.adapter.SimpleItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class TmpTestActivity2 extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "MainActivity";

    private Object mObject = new Object();

    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,TmpTestActivity2.class);
        if(!(context instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
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
        recyclerView = (RecyclerView) findViewById(R.id.rlv_test_scroll);
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
