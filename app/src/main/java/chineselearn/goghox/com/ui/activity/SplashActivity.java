package chineselearn.goghox.com.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import chineselearn.goghox.com.MainActivity;
import chineselearn.goghox.com.R;

/**
 * Created by GogHox on 2018/3/17.
 */

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout mRl;
    private ImageView mIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_splash_enter, 0);

        initView();
        setAction();
    }

    private void setAction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, 2000);
    }

    private void initView() {
        mRl = new RelativeLayout(this);
        mRl.setBackgroundColor(Color.WHITE);
        mIv = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(250, 250);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mIv.setLayoutParams(params);
        mIv.setImageResource(R.mipmap.ic_launcher);
        mRl.addView(mIv);
        setContentView(mRl);
    }
}
