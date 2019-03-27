package com.example.mechrevo.roothelptool.divider;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecycleGridDivider extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(1, 1, 1, 1);
    }
}
