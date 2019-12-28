package com.bruce.jing.hello.demo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.bruce.jing.hello.demo.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * -------------------------------------
 * 作者：bruce jing
 * -------------------------------------
 * 时间：2018/10/25 下午9:35
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
public class FragmentContainerActivity extends AppCompatActivity {

    public static final String FRAGMENT_CLASS_NAME = "fragment_class_name";
    public static final String FRAGMENT_DATA = "fragment_data";
    private Fragment fragment = null;
    public static void startFragment(Context context, Class fragment, Bundle bundle) {
        Intent intent = new Intent(context, FragmentContainerActivity.class);
        if(bundle != null) {
            intent.putExtra(FRAGMENT_DATA, bundle);
        }
        intent.putExtra(FRAGMENT_CLASS_NAME, fragment.getCanonicalName());
        context.startActivity(intent);
    }

    public static void startFragment(Activity context, Class fragment, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, FragmentContainerActivity.class);
        if(bundle != null) {
            intent.putExtra(FRAGMENT_DATA, bundle);
        }
        intent.putExtra(FRAGMENT_CLASS_NAME, fragment.getCanonicalName());
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        String fragmentClassName = getIntent().getStringExtra(FRAGMENT_CLASS_NAME);
        Bundle bundleExtra = getIntent().getBundleExtra(FRAGMENT_DATA);
        try {
            fragment = (Fragment) Class.forName(fragmentClassName).newInstance();
            if(bundleExtra != null) {
                fragment.setArguments(bundleExtra);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.rl, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (fragment instanceof Fragment) {
                Fragment f = (Fragment) fragment;

            }
        }

        return super.onKeyDown(keyCode, event);
    }

    protected Fragment getFragment() {
        if (fragment != null) {
            return fragment;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            //if (requestCode == SubWhiteVideosFragment.REQUEST_CODE_COMMENT) {
            fragment.onActivityResult(requestCode, resultCode, data);
            //}
        }
    }


}
