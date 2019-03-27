package com.example.mechrevo.roothelptool.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Chao  2018/6/7 on 14:43
 * description
 */

public class DividerDecotation extends RecyclerView.ItemDecoration {
    private int orientation = -1;
    private Drawable divider;
    private int padding;
    private float size = 0;
    private int color = -1;
    private Rect rectF = new Rect();
    private Context context;

    public DividerDecotation(Context context) {
        this.context = context;
        initData();
    }

    public DividerDecotation(Context context, float size) {
        this.context = context;
        this.size = dip2px(size);
        initData();
    }

    public DividerDecotation(Context context, float size, int padding) {
        this.context = context;
        this.size = dip2px(size);
        this.padding = dip2px(padding);
        initData();
    }

    public DividerDecotation(Context context, float size, int padding, int color) {
        this.context = context;
        this.size = dip2px(size);
        this.padding = dip2px(padding);
        this.color = color;
        initData();
    }

    private void initData() {
        if (size == 0) {
            size = dip2px(1);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //super.onDraw(c, parent, state);
        init(parent);
        c.save();
        if (orientation == LinearLayoutManager.VERTICAL) {
            int left = parent.getPaddingLeft() + padding;
            int right = parent.getWidth() - parent.getPaddingRight() - padding;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View childAt = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();
                int top = (int) (childAt.getBottom() + layoutParams.bottomMargin + ViewCompat.getTranslationY(childAt));
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            int top = parent.getPaddingTop() + padding;
            int bottom = parent.getHeight() - parent.getPaddingBottom() - padding;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View childAt = parent.getChildAt(i);
                parent.getLayoutManager().getDecoratedBoundsWithMargins(childAt, rectF);
//                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();//使用Google默认分割线的代码，下面获取左右导致无法显示，但上下不使用默认分割线的代码，因为默认的会高度充满
//                int left = (int) (childAt.getLeft() + layoutParams.leftMargin + ViewCompat.getTranslationX(childAt));
//                int right = left + divider.getIntrinsicWidth();
                final int right = rectF.right + Math.round(childAt.getTranslationX());
                final int left = right - divider.getIntrinsicWidth();
                divider.setBounds(left, top, right, childAt.getHeight() - padding);
                divider.draw(c);
            }
        } else if (orientation == 3) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View childAt = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();
                RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
                int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                boolean endView = (i + 1) % spanCount == 0;
                if (!endView) {//竖线
                    int left = childAt.getRight() + layoutParams.rightMargin;
                    int right = left + divider.getIntrinsicWidth();
                    int top = childAt.getTop() + layoutParams.topMargin + padding;
                    int bottom = childAt.getBottom() - layoutParams.bottomMargin + divider.getIntrinsicHeight() - padding;
                    divider.setBounds(left, top, right, bottom);
                    divider.draw(c);
                }
                int left = childAt.getLeft() + layoutParams.leftMargin + padding;
                int right = childAt.getRight() - layoutParams.rightMargin + divider.getIntrinsicWidth() - padding;
                int top = childAt.getBottom() + layoutParams.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
        c.restore();
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        init(parent);
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
        } else if (orientation == 3) {
            outRect.set(0, 0, divider.getIntrinsicWidth(), divider.getIntrinsicHeight());
        }
        //super.getItemOffsets(outRect, view, parent, state);
    }

    private void init(RecyclerView parent) {
        if (orientation == -1) {
            divider = new GradientDrawable();
            ((GradientDrawable) divider).setSize((int) size, (int) size);
            if (color != -1) {
                ((GradientDrawable) divider).setColor(color);
            }
            //divider = ContextCompat.getDrawable(parent.getContext(), R.drawable.item_divider);
            ((GradientDrawable) divider).setCornerRadius(dip2px(5));
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                orientation = 3;
            } else if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    orientation = LinearLayoutManager.VERTICAL;
                } else if (manager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    orientation = LinearLayoutManager.HORIZONTAL;
                }
            }
        }
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}