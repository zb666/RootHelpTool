package com.example.mechrevo.roothelptool.block;

import android.content.Context;
import android.util.Log;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;

public class AppBlockCanaryContext extends BlockCanaryContext {
    @Override
    public String provideQualifier() {
        return "unKnown";
    }

    @Override
    public int provideBlockThreshold() {
        return 1000;
    }

    @Override
    public boolean displayNotification() {
        return true;
    }

    @Override
    public void onBlock(Context context, BlockInfo blockInfo) {
        super.onBlock(context, blockInfo);
        Log.d("BobBlock",blockInfo.toString());
    }
}
