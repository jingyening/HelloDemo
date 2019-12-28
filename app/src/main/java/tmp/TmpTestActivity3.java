package tmp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.bruce.jing.hello.demo.R;

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
