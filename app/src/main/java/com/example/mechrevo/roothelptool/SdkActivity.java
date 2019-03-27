package com.example.mechrevo.roothelptool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class SdkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk);

        ///登陆 account是账号，token是需要和服务器
        LoginInfo loginInfo = new LoginInfo("account","aaa");
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                //sp
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        };
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(new RequestCallback<LoginInfo>() {

            @Override
            public void onSuccess(LoginInfo param) {

            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });

    }





}
