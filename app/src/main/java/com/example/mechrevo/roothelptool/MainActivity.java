package com.example.mechrevo.roothelptool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "OppoTest";
    private TextView tvcheckSuperuserApk;
    private TextView tvcheckRootPathSU;
    private TextView tvcheckRootWhichSU;
    private TextView tvexecuteCommand;
    private TextView tvcheckGetRootAuth;
    private TextView tvcheckAccessRootData;
    private TextView tvcheckBusybox;
    private TextView tvwriteFile;
    private TextView tvreadFile;
    private TextView isDeviceRooted;
    private MainPresenter mainPresenter;
    private WaveBezierViewTwo waveBezierViewTwo;
    private WaveBezierViewone waveBezierViewone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppo);

        mainPresenter = new MainPresenter(this);
        mainPresenter.startWork();

        tvcheckSuperuserApk = findViewById(R.id.tvcheckSuperuserApk);
        tvcheckRootPathSU = findViewById(R.id.tvcheckRootPathSU);
        tvcheckRootWhichSU = findViewById(R.id.tvcheckRootWhichSU);
        tvexecuteCommand = findViewById(R.id.tvexecuteCommand);
        tvcheckGetRootAuth = findViewById(R.id.tvcheckGetRootAuth);
        tvcheckBusybox = findViewById(R.id.tvcheckBusybox);
        tvcheckAccessRootData = findViewById(R.id.tvcheckAccessRootData);
        tvwriteFile = findViewById(R.id.tvwriteFile);
        tvreadFile = findViewById(R.id.tvreadFile);
        isDeviceRooted = findViewById(R.id.isDeviceRooted);

        tvexecuteCommand.setOnClickListener(this);
        tvcheckGetRootAuth.setOnClickListener(this);
        tvcheckBusybox.setOnClickListener(this);
        tvcheckAccessRootData.setOnClickListener(this);
        tvwriteFile.setOnClickListener(this);
        tvreadFile.setOnClickListener(this);
        tvcheckSuperuserApk.setOnClickListener(this);
        tvcheckRootPathSU.setOnClickListener(this);
        tvcheckRootWhichSU.setOnClickListener(this);
        isDeviceRooted.setOnClickListener(this);

        int[] fun = {0,1,2,3,4,5,6};
        int[] fun2 = {1,1,1,1,1};
        // 从src 数组的 第二位开始进行拷贝，赋值给des数组的第1-3位
        System.arraycopy(fun,2,fun2,1,3);

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        MyView myView = findViewById(R.id.myview);
        myView.startAnim();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.isDeviceRooted:
                boolean deviceRooted = isDeviceRooted();
                TextView textView = (findViewById(R.id.isDeviceRooted));
                textView.setText(deviceRooted ? "是" : "否");
                startActivity(new Intent(this,GuideActivity.class));
                finish();
                break;
            case R.id.tvcheckSuperuserApk:
                tvcheckSuperuserApk.setText(isTrue(checkSuperuserApk()));
                break;
            case R.id.tvcheckRootPathSU:
                tvcheckRootPathSU.setText(isTrue(checkRootPathSU()));
                break;
            case R.id.tvcheckRootWhichSU:
                tvcheckRootWhichSU.setText(isTrue(checkRootWhichSU()));
                break;
            case R.id.tvcheckBusybox:
                tvcheckBusybox.setText(isTrue(checkBusybox()));
                break;
            case R.id.tvcheckAccessRootData:
                tvcheckAccessRootData.setText(isTrue(checkAccessRootData()));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.detach();
    }

    String isTrue(boolean isChecked) {
        return isChecked ? "是" : "否";
    }

    public static boolean isDeviceRooted() {
        if (checkDeviceDebuggable()) {
            return true;
        }//check buildTags
        if (checkSuperuserApk()) {
            return true;
        }//Superuser.apk
        //if (checkRootPathSU()){return true;}//find su in some path
        //if (checkRootWhichSU()){return true;}//find su use 'which'
        if (checkBusybox()) {
            return true;
        }//find su use 'which'
        if (checkAccessRootData()) {
            return true;
        }//find su use 'which'
        if (checkGetRootAuth()) {
            return true;
        }//exec su

        return false;
    }



    public static boolean checkSuperuserApk() {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                Log.i(LOG_TAG, "/system/app/Superuser.apk exist");
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean checkDeviceDebuggable() {
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            Log.i(LOG_TAG, "buildTags=" + buildTags);
            return true;
        }
        return false;
    }

    //写文件
    public static Boolean writeFile(String fileName, String message) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //读文件
    public static String readFile(String fileName) {
        File file = new File(fileName);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            while ((len = fis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            String result = new String(bos.toByteArray());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized boolean checkAccessRootData() {
        try {
            Log.i(LOG_TAG, "to write /data");
            String fileContent = "test_ok";
            Boolean writeFlag = writeFile("/data/su_test", fileContent);
            if (writeFlag) {
                Log.i(LOG_TAG, "write ok");
            } else {
                Log.i(LOG_TAG, "write failed");
            }

            Log.i(LOG_TAG, "to read /data");
            String strRead = readFile("/data/su_test");
            Log.i(LOG_TAG, "strRead=" + strRead);
            if (fileContent.equals(strRead)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized boolean checkBusybox() {
        try {
            Log.i(LOG_TAG, "to exec busybox df");
            String[] strCmd = new String[]{"busybox", "df"};
            ArrayList<String> execResult = executeCommand(strCmd);
            if (execResult != null) {
                Log.i(LOG_TAG, "execResult=" + execResult.toString());
                return true;
            } else {
                Log.i(LOG_TAG, "execResult=null");
                return false;
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        }
    }

    public static synchronized boolean checkGetRootAuth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            Log.i(LOG_TAG, "to exec su");
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            Log.i(LOG_TAG, "exitValue=" + exitValue);
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> executeCommand(String[] shellCmd) {
        String line = null;
        ArrayList<String> fullResponse = new ArrayList<String>();
        Process localProcess = null;
        try {
            Log.i(LOG_TAG, "to shell exec which for find su :");
            localProcess = Runtime.getRuntime().exec(shellCmd);
        } catch (Exception e) {
            return null;
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(localProcess.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
        try {
            while ((line = in.readLine()) != null) {
                Log.i(LOG_TAG, "–> Line received: " + line);
                fullResponse.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "–> Full response was: " + fullResponse);
        return fullResponse;
    }

    public static boolean checkRootWhichSU() {
        String[] strCmd = new String[]{"/system/xbin/which", "su"};
        ArrayList<String> execResult = executeCommand(strCmd);
        if (execResult != null) {
            Log.i(LOG_TAG, "execResult=" + execResult.toString());
            return true;
        } else {
            Log.i(LOG_TAG, "execResult=null");
            return false;
        }
    }

    public static boolean checkRootPathSU() {
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    Log.i(LOG_TAG, "find su in : " + kSuSearchPaths[i]);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void toast(String toast){
        Toast.makeText(this,toast,Toast.LENGTH_SHORT).show();
    }
}
