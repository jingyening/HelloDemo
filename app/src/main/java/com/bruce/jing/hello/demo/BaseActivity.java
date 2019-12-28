package com.bruce.jing.hello.demo;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.bruce.jing.hello.demo.fragment.WTFragmentOp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author bruce jing
 * @date 2019/12/25
 */
public class BaseActivity extends AppCompatActivity implements WTFragmentOp.FragmentOpCallback {

    /**
     * 定义生命周期状态
     */
    public static final int CREATE = 0;
    public static final int START= 1;
    public static final int RESUME = 2;
    public static final int PAUSE = 3;
    public static final int STOP = 4;
    public static final int DESTROY = 5;


    @IntDef({CREATE,START,RESUME,PAUSE,STOP,DESTROY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LifeCycleState {

    }
    private @LifeCycleState int mLifeState;

    /**
     *  是否执行了saveState
     */
    private boolean mSaveState =false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mLifeState = CREATE;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifeState = START;
        mSaveState = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifeState = RESUME;
        mSaveState = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLifeState = PAUSE;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifeState = STOP;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifeState = DESTROY;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSaveState = true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSaveState = false;
    }

    public final @LifeCycleState int getLifeCycleState(){
        return mLifeState;
    }


    public final boolean isSaveState(){
        return mSaveState;
    }



    @Override
    public void onRemainActionOperate(List<WTFragmentOp.CommitAction> remainAction) {

    }
}
