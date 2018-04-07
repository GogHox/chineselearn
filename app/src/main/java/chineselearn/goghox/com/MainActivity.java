package chineselearn.goghox.com;

import android.Manifest;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import chineselearn.goghox.com.ui.fragment.OneFragment;
import chineselearn.goghox.com.ui.fragment.ThreeFragment;
import chineselearn.goghox.com.ui.fragment.TwoFragment;
import chineselearn.goghox.com.ui.view.BottomBar;
import chineselearn.goghox.com.ui.view.BottomBarTab;

public class MainActivity extends AppCompatActivity {

    ArrayList<Fragment> mFragmentList = new ArrayList<>();  // 所有fragment的集合
    String[] mFragmentTag = {"OneFragment", "TwoFragment", "ThreeFragment"};
    Fragment mCurrentFragment;  // 记录当前显示的fragment
    private FragmentManager mFm;

    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;

    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initBottomBar();
        initData();

        // 初始化科大讯飞
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5a966c62");
        // 请求权限
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void initBottomBar() {
        bottomBar.addItem(new BottomBarTab(this, R.drawable.learn, "学习"));
        bottomBar.addItem(new BottomBarTab(this, R.drawable.find, "报名"));
        bottomBar.addItem(new BottomBarTab(this, R.drawable.me, "测试"));

        bottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                switchFragment(mFragmentList.get(position), mFragmentTag[position]);
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private void initData() {
        // 初始化fragment
        Fragment oneFragment = new OneFragment();
        Fragment twoFragment = new TwoFragment();
        Fragment threeFragment = new ThreeFragment();

        // 将fragment加入到集合中
        mFragmentList.add(oneFragment);
        mFragmentList.add(twoFragment);
        mFragmentList.add(threeFragment);

        mCurrentFragment = mFragmentList.get(0);
        // 初始化首次进入时的Fragment显示
        mFm = getSupportFragmentManager();
        FragmentTransaction transaction = mFm.beginTransaction();
        transaction.add(R.id.fl_content, mCurrentFragment, mFragmentTag[0]);
        transaction.commitAllowingStateLoss();
    }

    // 转换Fragment
    void switchFragment(Fragment to, String tag) {
        if (mCurrentFragment != to) {
            FragmentTransaction transaction = mFm.beginTransaction();
            if (!to.isAdded()) {
                // 没有添加过: 隐藏当前的Fragment，添加新的，并显示
                transaction.hide(mCurrentFragment).add(R.id.fl_content, to, tag).show(to);
            } else {
                transaction.hide(mCurrentFragment).show(to);
            }
            mCurrentFragment = to;
            transaction.commitAllowingStateLoss();
        }
    }

    // 当activity非正常销毁时被调用
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // 重置Fragment，防止内存不足时导致Fragment重叠
        updateFragment(outState);
    }

    private void updateFragment(Bundle outState) {
        mFm = getSupportFragmentManager();
        if (outState == null) {
            // 内存被清理了
            FragmentTransaction transaction = mFm.beginTransaction();
            Fragment oneFragment = new OneFragment();
            transaction.add(R.id.fl_content, oneFragment, mFragmentTag[0]).commitAllowingStateLoss();
        } else {
            mFm.beginTransaction()
                    .show(mFm.findFragmentByTag(mFragmentTag[0]))
                    .hide(mFm.findFragmentByTag(mFragmentTag[1]))
                    .hide(mFm.findFragmentByTag(mFragmentTag[2]))
                    .commitAllowingStateLoss();

        }
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mCurrentFragment.getTag() == mFragmentTag[1]) {
            if (keyCode == KeyEvent.KEYCODE_BACK && ((TwoFragment) mFm.findFragmentByTag(mFragmentTag[1])).webView.canGoBack()) {
                ((TwoFragment) mFm.findFragmentByTag(mFragmentTag[1])).webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static long lastClickBackTime = 0;
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastClickBackTime < 2000){
            //super.onBackPressed();
            finish();
        }else{
            Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
            lastClickBackTime = currentTime;
        }
    }
}
