package chineselearn.goghox.com.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import chineselearn.goghox.com.R;
import chineselearn.goghox.com.ui.activity.TestActivity;

/**
 * Created by GogHox on 2018/2/14.
 */

public class ThreeFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initToolbar();
        return view;
    }

    private void initView(View rootView) {
        (rootView.findViewById(R.id.btn_word)).setOnClickListener(this);
        (rootView.findViewById(R.id.btn_double)).setOnClickListener(this);
        (rootView.findViewById(R.id.btn_more)).setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar.setTitle("模拟测试");
        toolbar.setTitleTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Subhead_Inverse);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(Color.WHITE);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), TestActivity.class);
        switch (v.getId()){
            case R.id.btn_word:
                intent.putExtra(TestActivity.PAGE_TAG, TestActivity.FIRST_PAGE);
                break;
            case R.id.btn_double:
                intent.putExtra(TestActivity.PAGE_TAG, TestActivity.SECOND_PAGE);
                break;
            case R.id.btn_more:
                intent.putExtra(TestActivity.PAGE_TAG, TestActivity.THREE_PAGE);
                break;
        }
        startActivity(intent);
    }
}
