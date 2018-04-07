package chineselearn.goghox.com.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import chineselearn.goghox.com.R;

/**
 * 这是一个RecyclerView的item封闭式分隔线，
 * 类似与表格，可左右画线
 * Created by GogHox on 2018/3/9.
 */

public class MyDividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private static final String TAG = "DividerItem";
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private final Context mContext;

    private Drawable mDivider;

    private final Rect mBounds = new Rect();

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     *
     * @param context     Current context, it will be used to access resources.
     */
    public MyDividerItemDecoration(Context context) {
        mContext = context;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "DividerItemDecoration. Please set that attribute all call setDrawable()");
        }
        a.recycle();
        //setOrientation(orientation);
    }


    /**
     * Sets the {@link Drawable} for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }


        // 同时画两个方向的线
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();  // 200
        }

        // TODO 设置上下分隔的线
        mDivider = ContextCompat.getDrawable(mContext,R.drawable.shape_divider_line_vertical);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDivider.getIntrinsicHeight();  // 底部 - 线高度
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);

            if(i == 0){  // 画顶部的线
                int oneTop = mBounds.top + Math.round(child.getTranslationY());
                int oneBottom = oneTop + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, oneTop, right, oneBottom);
                //mDivider.setBounds(left, mBounds.top, right, mBounds.top  + mDivider.getIntrinsicHeight());
                mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        // TODO 设置左右分隔的线
        mDivider = ContextCompat.getDrawable(mContext,R.drawable.shape_divider_line_horizontal);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            final int right = mBounds.right + Math.round(child.getTranslationX());
            final int left = right - mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
            if(i == 0){
                int oneLeft = mBounds.left + Math.round(child.getTranslationX());
                int oneRight = oneLeft + mDivider.getIntrinsicWidth();
                mDivider.setBounds(oneLeft, top, oneRight, bottom);
                mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        /*
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }*/
    }

}
