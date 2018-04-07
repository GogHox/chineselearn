package chineselearn.goghox.com.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import chineselearn.goghox.com.R;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * Created by GogHox on 2018/2/14.
 */

public class TwoFragment extends Fragment {
    public WebView webView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initToolbar();
        return view;
    }

    private void initToolbar() {
        toolbar.setTitle("在线报名");
        toolbar.setTitleTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Subhead_Inverse);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(Color.WHITE);
    }
    private void initView(View rootView) {
        webView = (WebView)rootView.findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  // 自适应屏幕
        settings.setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://gd.cltt.org/Web/SignUpOnLine/OnlineSign.aspx");
    }
}
