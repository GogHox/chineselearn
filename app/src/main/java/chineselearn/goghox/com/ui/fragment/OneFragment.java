package chineselearn.goghox.com.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import chineselearn.goghox.com.global.AppData;
import chineselearn.goghox.com.R;
import chineselearn.goghox.com.bean.CourseBean;
import chineselearn.goghox.com.global.Server;
import chineselearn.goghox.com.ui.activity.ReadingActivity;

/**
 * Created by GogHox on 2018/2/14.
 */

public class OneFragment extends Fragment {
    private static final String TAG = "OneFragment";

    @BindView(R.id.rw) RecyclerView rw;
    @BindView(R.id.srl) SwipeRefreshLayout srl;
    @BindView(R.id.toolbar) Toolbar toolbar;
    Handler mHandler = new Handler();
    private MyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        ButterKnife.bind(this, view);

        initToolbar();
        initListView();
        initSwipeRefresh();

        srl.setRefreshing(true);
        getDataFromServer();

        return view;
    }

    private void getDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = Server.run(Server.host + "/get_course");
                    JSONArray ja = new JSONArray(res);
                    for (int i = 0; i < ja.length(); i++){
                        final CourseBean courseBean = new Gson().fromJson(ja.getJSONObject(i).toString(), CourseBean.class);
                        Log.i(TAG, "run: " + courseBean.toString());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppData.courseBeans.add(courseBean);
                                mAdapter.notifyDataSetChanged();
                                srl.setRefreshing(false);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                            srl.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();

    }

    private void initToolbar() {
        toolbar.setTitle("学习");
        toolbar.setTitleTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Subhead_Inverse);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(Color.WHITE);
    }

    private void initSwipeRefresh() {
        srl.setDistanceToTriggerSync(200);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               getDataFromServer();
            }
        });
    }

    private void initListView() {
        // 准备数据: 有关数据读取，可以在子线程中进行
        AppData.setData();
        rw.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rw.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));
        mAdapter = new MyAdapter(getContext(), AppData.courseBeans, R.layout.item_course);
        rw.setAdapter(mAdapter);

    }
    class MyAdapter extends RecyclerView.Adapter {
        private final Context mContext;
        private final ArrayList<CourseBean> mList;
        private final int mItemLayout;

        public MyAdapter(Context ctx, ArrayList<CourseBean> dataList, @LayoutRes int itemLayout){
            this.mContext = ctx;
            this.mList = dataList;
            this.mItemLayout = itemLayout;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder = new Holder(View.inflate(getContext(), mItemLayout, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((Holder) holder).tvTitle.setText(mList.get(position).getTitle());
            ((Holder) holder).tvDescription.setText(mList.get(position).getTitle());
            //((Holder) holder).ivImg.setImageResource(mList.get(position).imgRes);
            Glide.with(getContext()).load(mList.get(position).getImg_url()).into(((Holder) holder).ivImg);
            ((Holder) holder).root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进入子界面
                    Intent intent = new Intent(getActivity(), ReadingActivity.class);
                    intent.setAction(String.valueOf(position));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
        class Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_title) TextView tvTitle;
            @BindView(R.id.tv_description) TextView tvDescription;
            @BindView(R.id.iv_img) ImageView ivImg;
            @BindView(R.id.ll_root) View root;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppData.courseBeans = null;
        AppData.contents = null;
    }
}
