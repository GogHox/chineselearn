package chineselearn.goghox.com.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import chineselearn.goghox.com.global.Utils;

/**
 * Created by GogHox on 2018/3/10.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter{

    private static final int SINGLE_TEXT_VIEW_ID = 100;
    private final Activity mActivity;
    private List<String> data;

    public MyRecyclerAdapter(Activity activity, List<String> data){
        this.mActivity = activity;
        this.data = data;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建布局文件
        RelativeLayout rl = new RelativeLayout(mActivity);
        TextView textView = new TextView(mActivity);
        textView.setId(SINGLE_TEXT_VIEW_ID);
        textView.setTextColor(Color.parseColor("#888888"));
        //textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, countGridSize() / 2 - 50));                        // 设置textview大小
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));                        // 设置textview大小
        textView.setPadding(0, 20, 0, 20);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);  // 设置字体大小
        textView.setGravity(Gravity.CENTER);                         // 设置内容全部居中
        rl.addView(textView);

        RecyclerView.ViewHolder viewHolder = new MyViewHolder(rl);
        return viewHolder;
    }

    private int countGridSize() {
        int screenWidthDp = Utils.getScreenWidthDp(mActivity);
        return (screenWidthDp) / 10;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tvSingleTextView.setText(data.get(position));

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tvSingleTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSingleTextView = itemView.findViewById(SINGLE_TEXT_VIEW_ID);
        }
    }
}