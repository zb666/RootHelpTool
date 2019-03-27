package com.example.mechrevo.roothelptool;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.mechrevo.roothelptool.divider.DividerDecotation;
import com.example.mechrevo.roothelptool.divider.RecycleGridDivider;
import io.agora.rtc.Constants;
import io.agora.rtc.IAudioEffectManager;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {
    private RtcEngine mRtcEngine;
    private int mChannelId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        RecyclerView recyclerView = findViewById(R.id.rv_guide);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(i + "");
        }
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.ic_drawable));
        int grayColor = getResources().getColor(R.color.colorLittleGray);
        recyclerView.addItemDecoration(new DividerDecotation(this, 1, 0, grayColor));
        MyAdapter myAdapter = new MyAdapter();
        myAdapter.setNewData(list);
        recyclerView.setAdapter(myAdapter);

        findViewById(R.id.tvJoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChannelId = mRtcEngine.joinChannel(null, "demoChannel", "Extra OptionalData", 0);
            }
        });

        findViewById(R.id.tvLevel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRtcEngine.leaveChannel();
                //根据CallId 对此次通话进行评分
                String callId = mRtcEngine.getCallId();
                mRtcEngine.rate(callId, 5, "this is the awesome call");
            }
        });

        findViewById(R.id.tvRecord).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    //开始录音
                    mRtcEngine.startAudioRecording("path/to/file", 2);
                } else {
                    //结束录音
                    mRtcEngine.stopAudioRecording();
                }
                return false;
            }
        });

        //HashMap + 双链表
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("1", "1");
        linkedHashMap.put("2", "2");
        linkedHashMap.put("3", "3");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("11", "11");
        hashMap.put("22", "22");
        hashMap.put("33", "33");

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), new IRtcEngineEventHandler() {
                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    super.onJoinChannelSuccess(channel, uid, elapsed);
                }

                @Override
                public void onLeaveChannel(RtcStats stats) {
                    super.onLeaveChannel(stats);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mRtcEngine == null) return;
        //设置频道模式为通信
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        //加入通信频道
        int id = 0;
        IAudioEffectManager effectManager = mRtcEngine.getAudioEffectManager();
        effectManager.preloadEffect(id++, "path/to/effetc1");
// 可以加载多个音效
        effectManager.preloadEffect(id++, "path/to/effect2");

// 播放一个音效
        effectManager.playEffect(
                0,                         // 要播放的音效 id
                "path/to/effect1",         // 播放文件的路径
                -1,                        // 播放次数，-1 代表无限循环
                0.0,                       // 改变音效的空间位置，0表示正前方
                100,                       // 音量，取值 0 ~ 100， 100 代表原始音量
                100,                       // 是否令远端也能听到音效的声音
                true// 是否令远端也能听到音效的声音
        );
    }

    @Override
    public void onClick(View v) {

    }

    class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_guide);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

        }

        @Override
        public int getItemCount() {
            return 8;
        }
    }
}
