package tmp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bruce.jing.hello.demo.R;

import androidx.appcompat.app.AppCompatActivity;

public class EmptyActivity extends AppCompatActivity {


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
