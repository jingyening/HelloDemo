package com.bruce.jing.hello.demo.fragment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.bruce.jing.hello.demo.BaseActivity;
import com.bruce.jing.hello.demo.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 用于较为安全的提交fragment，避免saveState后抛异常或者restore后fragment丢失的情况。
 *
 * @author bruce jing
 * @date 2019/12/25
 *  使用示例1如下：
 *  final WTFragmentOp wtFragmentOp = WTFragmentOp.getInstance(this);
 *         wtFragmentOp.submitOnMainThreadSafely(this, new WTFragmentOp.CommitAction() {
 *             @Override
 *             public void commitFragmentTransaction() {
 *                 FragmentManager fm = getSupportFragmentManager();
 *                 FragmentTransaction ft = fm.beginTransaction();
 *                 Fragment fragment = new Fragment();
 *                 Fragment fragment2 = new Fragment();
 *                 ft.add(fragment,"test");
 *                 ft.replace(R.id.test,fragment2);
 *                 ft.remove(fragment2);
 *                 ft.commit();
 *                 fm.executePendingTransactions();
 *
 *             }
 *
 *             @Override
 *             public String getIdentify() {
 *                 return "如果失败，不需要再次执行";
 *             }
 *
 *             @Override
 *             public String getActionName() {
 *                 return "测试test";
 *             }
 *         },OpType.ABANDON);
 *
 * 使用示例2如下：
 *         final WTFragmentOp wtFragmentOp = WTFragmentOp.getInstance(this);
 *         FragmentManager fm = getSupportFragmentManager();
 *         FragmentTransaction ft = fm.beginTransaction();
 *         Fragment fragment = new Fragment();
 *         Fragment fragment2 = new Fragment();
 *         ft.add(fragment,"test");
 *         ft.replace(R.id.test,fragment2);
 *         ft.remove(fragment2);
 *         WTFragmentOp.CommitAction commitAction = wtFragmentOp.generateCommitAction(fm, ft);
 *         wtFragmentOp.submitOnMainThreadSafely(this,commitAction,OpType.FORCE);
 *
 */
public final class WTFragmentOp implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "WTFragmentOp";

    private static volatile WTFragmentOp sInstance;

    private Context mContext;
    private WeakHashMap<BaseActivity, ArrayList<CommitAction>> mPendingActions = new WeakHashMap<>();

    private Handler mHandler = new Handler(Looper.getMainLooper());



    public static WTFragmentOp getInstance(Context context) {
        if (sInstance == null) {
            synchronized (WTFragmentOp.class) {
                if (sInstance == null) {
                    sInstance = new WTFragmentOp(context);
                }
            }
        }
        return sInstance;
    }

    private WTFragmentOp(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 在主线程提交commit action，不会丢弃commitAction
     * @param activity
     * @param commitAction
     * @return
     */
    public boolean submitOnMainThreadSafely(@NonNull BaseActivity activity, @NonNull AbsCommitAction commitAction) {
        return submitOnMainThreadSafely(activity,commitAction,OpType.PENDING);
    }

    /**
     * 在主线程提交commit action
     * @param activity
     * @param commitAction
     * @param opType
     * 如果为abandon，activity处于saveState后，直接丢弃当前action;
     * 如果为pending，activity处于saveState后，保存当前action到队列中;
     * 如果为force，activity处于saveState后，仍然提交
     * @return 是否完成提交，true完成，false没有完成
     */
    public boolean submitOnMainThreadSafely(@NonNull BaseActivity activity, @NonNull AbsCommitAction commitAction,OpType opType) {
        if (activity == null) {
            Log.d(TAG, "submitOnMainThreadSafely activity is null");
            return false;
        }
        if (commitAction == null) {
            Log.d(TAG, "submitOnMainThreadSafely commitAction is null");
            return false;
        }
        CommonUtil.assertMainThread();
//        if(Looper.myLooper() != Looper.getMainLooper()){
//            throw new RuntimeException("You must commit fragment on main thread");
//        }

        if(activity.isSaveState() ) {
            if (opType == OpType.ABANDON) {
                Log.d(TAG, "submitOnMainThreadSafely commitAction is abandon");
                return false;
            }
            if(opType == OpType.PENDING){
                ArrayList<CommitAction> actionList = mPendingActions.get(activity);
                if (actionList == null) {
                    actionList = new ArrayList<>();
                    mPendingActions.put(activity,actionList);
                }
                actionList.add(commitAction);
                Log.d(TAG, "submitOnMainThreadSafely commitAction is pending");
                return false;
            }
        }
        commitAction.commitFragmentTransaction();
        return true;
    }

    /**
     * 创建一个CommitAction
     * 提交方式为：提交当前事务，同时执行FragmentManager中所有pending的action
     * @param fm
     * @param ft
     * @return
     */
    public AbsCommitAction generateCommitAction(final FragmentManager fm, final FragmentTransaction ft){
        if(fm == null){
            Log.d(TAG,"generateSyncSingleCommitAction FragmentManager is null");
            return null;
        }
        if(ft == null){
            Log.d(TAG,"generateSyncSingleCommitAction FragmentTransaction is null");
            return null;
        }
        return new AbsCommitAction() {
            @Override
            public void commitFragmentTransaction() {
                ft.commitAllowingStateLoss();
                fm.executePendingTransactions();
            }

            @Override
            public String getIdentify() {
                return "";
            }

            @Override
            public String getActionName() {
                return "";
            }
        };

    }

    /**
     * 创建一个CommitAction，
     * 提交方式为：只提交当前的事务，不管之前FragmentManager中pending的事务
     * @param ft
     * @return
     */
    public AbsCommitAction generateCommitSingleAction(final FragmentTransaction ft){
        if(ft == null){
            throw new IllegalArgumentException("generate SyncSingleCommitAction FragmentTransaction is null!!!");
        }
        return new AbsCommitAction() {
            @Override
            public void commitFragmentTransaction() {
                ft.commitNowAllowingStateLoss();
            }

            @Override
            public String getIdentify() {
                return "";
            }

            @Override
            public String getActionName() {
                return "";
            }
        };
    }

    /**
     * 生成一个pop action
     * pop方式：将pop提交，同时执行FragmentManager中所有pending的action
     * @param fm
     * @return
     */
    public CommitAction generatePopAction(final FragmentManager fm){
        if(fm == null){
            throw new IllegalArgumentException("generate PopAction FragmentManager is null");
        }
        return new AbsCommitAction() {
            @Override
            public void commitFragmentTransaction() {
                fm.popBackStack();
                fm.executePendingTransactions();
            }

            @Override
            public String getIdentify() {
                return "";
            }

            @Override
            public String getActionName() {
                return "";
            }
        };
    }

    /**
     * 生成一个pop action
     * pop方式：将pop提交，不管FragmentManager中已存在pending的action
     * @param fm
     * @return
     */
    public CommitAction generatePopSingle(final FragmentManager fm){
        if(fm == null){
            throw new IllegalArgumentException("generate PopActionSingle FragmentManager is null");
        }
        return new AbsCommitAction() {
            @Override
            public void commitFragmentTransaction() {
                fm.popBackStackImmediate();
            }

            public String getIdentify() {
                return "";
            }

            public String getActionName() {
                return "";
            }
        };
    }

//    public void syncCommitAll(final FragmentManager fm, final FragmentTransaction ft){
//        if(fm == null){
//            Log.d(TAG,"syncCommitAll FragmentManager is null");
//            return ;
//        }
//        if(ft == null){
//            Log.d(TAG,"syncCommitAll FragmentTransaction is null");
//            return;
//        }
//        fm.executePendingTransactions();
//        ft.commitNow();
//    }
//
//    public void syncCommitSingle(final FragmentManager fm, final FragmentTransaction ft){
//        if(fm == null){
//            Log.d(TAG,"syncCommitSingle FragmentManager is null");
//            return ;
//        }
//        if(ft == null){
//            Log.d(TAG,"syncCommitSingle FragmentTransaction is null");
//            return;
//        }
//        ft.commitNow();
//    }

    /**
     *
     * @param activity
     * @return
     */
    public List<CommitAction> getPendingActions(BaseActivity activity) {
        ArrayList<CommitAction> commitActions = mPendingActions.get(activity);
        if(commitActions != null){
            return new ArrayList<>(commitActions);
        }
        return commitActions;
    }

    /**
     * 清除所有的pending action
     */
    public void clearPendingAction(){
        mPendingActions.clear();
    }

    public void clearPendingAction(BaseActivity activity){
        mPendingActions.remove(activity);
    }



    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(final Activity activity) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (activity instanceof BaseActivity) {
                    ArrayList<CommitAction> commitActions = mPendingActions.remove(activity);
                    if (commitActions != null && !commitActions.isEmpty()) {
                        ((BaseActivity) activity).onRemainActionOperate(new ArrayList<CommitAction>(commitActions));
                    }
                }
            }
        });
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mPendingActions.remove(activity);
    }

    /**
     * fragment的commit action
     */
   public interface CommitAction {

        /**
         * fragment 提交的具体实现
         */
        void commitFragmentTransaction();

    }


    /**
     * 如果不想重写getIdentify方法和getActionName方法的，可以实现此类
     */
    public static abstract class AbsCommitAction implements CommitAction{
        private String mActionName;
        private long mCreateTime;
        public AbsCommitAction(){
            this("");
        }
        public AbsCommitAction(String actionName){
            mActionName = actionName;
            mCreateTime = SystemClock.elapsedRealtime();
        }

        /**
         * 当前提交的标识，非唯一，用于提交失败时，可以根据标识来决定处理逻辑
         * @return
         */
        public String getIdentify() {
            return null;
        }

        /**
         * 当前提交的name，保留字段
         *
         * @return
         */
        public String getActionName() {
            return mActionName;
        }

        /**
         * action的创建时间，是开机时间不是自然时间
         * @return
         */
        public long getCreateTime(){
            return mCreateTime;
        }
    }

    /**
     * Fragment操作的回调类
     */
    public interface FragmentOpCallback{
        /**
         * （因为activity生命周期原因导致）未执行的CommitAction
         * @param remainAction
         */
        void onRemainActionOperate(List<CommitAction> remainAction);
    }


    public enum OpType{
        ABANDON,
        PENDING,
        FORCE;

    }

}
