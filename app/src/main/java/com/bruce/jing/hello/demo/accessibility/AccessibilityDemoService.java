package com.bruce.jing.hello.demo.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.bruce.jing.hello.demo.R;
import com.bruce.jing.hello.demo.util.log.JLogUtil;

import java.util.List;

/**
 * https://blog.csdn.net/dd864140130/article/details/51794318
 */
public class AccessibilityDemoService extends AccessibilityService {

    private static final String TAG = "AccessibilityDemoServic";
    /**
     * 同花顺
     */
    public static final String PKG_NAME_TONGHUASUN = "com.hexin.plat.android";
    /**
     * 微信
     */
    public static final String PKG_NAME_WECHAT = "com.tencent.mm";
    /**
     * 企业微信
     */
    public static final String PKG_NAME_WEWORK = "com.tencent.wework";

    /**
     * com.hexin.plat.android:id/button_container
     */
    public static final String BUY = "买 入";
    /**
     * com.hexin.plat.android:id/button_container
     */
    public static final String SELL = "卖 出";




    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String pkgName = event.getPackageName() == null ? "null" : event.getPackageName().toString();
        int eventType = event.getEventType();
        AccessibilityNodeInfo source = event.getSource();
        String className = event.getClassName() == null ? "null" : event.getClassName().toString();
        List<CharSequence> text = event.getText();
        // AccessibilityOperator封装了辅助功能的界面查找与模拟点击事件等操作
        JLogUtil.d(TAG, "onAccessibilityEvent eventType: " + eventType + " pkgName: " + pkgName + " ,className = " + className);
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                handleViewClick(event);
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (PKG_NAME_WECHAT.equals(pkgName)) {
                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                        getPacket();
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                        openPacket();
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                        close();
                    }
                }

                break;

        }
    }

    private void handleViewClick(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                Log.d(TAG, "handleViewClick content = " + content);
                if (content.contains(BUY) || content.contains(SELL)) {
                    JLogUtil.d(TAG, "handleViewClick 卖出 买入");
                    AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> button_container = rootInActiveWindow.findAccessibilityNodeInfosByViewId("button_container");
                    JLogUtil.d(TAG, "handleViewClick button_container size = " + button_container.size());
                    if (!button_container.isEmpty()) {

                        for (int i = 0; i < button_container.size(); i++) {
                            AccessibilityNodeInfo accessibilityNodeInfo = button_container.get(i);
                        }
                    }


                    JLogUtil.d(TAG, "handleViewClick process = " + Process.myPid() + ", " + Process.myTid() + " , " + Process.myUid());

                    Toast.makeText(this, "先看规则再交易!!!", Toast.LENGTH_LONG).show();
                    // 获取WindowManager服务
                    final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

                    // 设置LayoutParam
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                    }
                    layoutParams.format = PixelFormat.RGBA_8888;
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = 500;
                    layoutParams.x = 0;
                    layoutParams.y = 300;
//                    layoutParams.flags = ;

                    // 将悬浮窗控件添加到WindowManager
                    final ViewGroup group = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_rules, null, false);
                    windowManager.addView(group, layoutParams);


                    final GestureDetector detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
                        @Override
                        public boolean onDown(MotionEvent e) {
                            return false;
                        }

                        @Override
                        public void onShowPress(MotionEvent e) {

                        }

                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return false;
                        }

                        @Override
                        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                            return false;
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                            group.setVisibility(View.GONE);
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            return false;
                        }
                    });
                    final View view = group.findViewById(R.id.sv_content);
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            view.setVisibility(View.GONE);
                            return true;
                        }
                    });
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JLogUtil.d(TAG,"handleViewClick onClick");
                        }
                    });
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            JLogUtil.d(TAG,"handleViewClick onTouch");
                            return detector.onTouchEvent(event);
                        }
                    });



                }
            }
        }
    }


    /**
     * 处理通知栏信息
     * <p>
     * 如果是微信红包的提示信息,则模拟点击
     *
     * @param event
     */
    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                Log.d(TAG, "handleNotification content = " + content);
                if (content.contains("[微信红包]") || content.contains("企业微信红包") || content.contains("已连接到")) {
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                            Log.d(TAG, "handleNotification send open");
                        } catch (PendingIntent.CanceledException e) {
                            JLogUtil.e(TAG, "pendingIntent.send exception");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 关闭红包详情界面,实现自动返回聊天窗口
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void close() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            //为了演示,直接查看了关闭按钮的id
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("@id/ez");
            nodeInfo.recycle();
            for (AccessibilityNodeInfo item : infos) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 模拟点击,拆开红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            //为了演示,直接查看了红包控件的id
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("@id/b9m");
            nodeInfo.recycle();
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 模拟点击,打开抢红包界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo node = recycle(rootNode);

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        AccessibilityNodeInfo parent = node.getParent();
        while (parent != null) {
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            parent = parent.getParent();
        }

    }

    /**
     * 递归查找当前聊天窗口中的红包信息
     * <p>
     * 聊天窗口中的红包都存在"领取红包"一词,因此可根据该词查找红包
     *
     * @param node
     */
    public AccessibilityNodeInfo recycle(AccessibilityNodeInfo node) {
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                if ("领取红包".equals(node.getText().toString())) {
                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    recycle(node.getChild(i));
                }
            }
        }
        return node;
    }


    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return super.onKeyEvent(event);
    }


    @Override
    protected void onServiceConnected() {
        JLogUtil.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{PKG_NAME_TONGHUASUN/*, PKG_NAME_WECHAT, PKG_NAME_WEWORK, "com.android.settings"*/};
        serviceInfo.notificationTimeout = 100;
        setServiceInfo(serviceInfo);
    }


    private boolean checkStealFeature1(String service) {
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }

        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    private boolean enabled(String name) {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = am.getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo info : installedAccessibilityServiceList) {
            JLogUtil.d(TAG, "all -->" + info.getId());
            if (name.equals(info.getId())) {
                return true;
            }
        }
        return false;
    }


}
