package com.example.mechrevo.roothelptool.dialogfm;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.mechrevo.roothelptool.R;

import java.util.HashMap;

public class BaseDialogFragment extends DialogFragment {

    private static HashMap<String, BaseDialogFragment> mDialogFragment = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(1, R.style.DialogCustomeStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        View inflateView = inflater.inflate(R.layout.dialog_fm, container);
        return inflateView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.tvCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleDialog();
            }
        });
        ImageView imageView = view.findViewById(R.id.ivLoading);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    public static void showDialog(AppCompatActivity activity) {
        showDialog(activity, BaseDialogFragment.class, null);
    }

    public static void showDialog(AppCompatActivity activity, Class<? extends BaseDialogFragment> dialogClass, Bundle args) {
        try {
            BaseDialogFragment baseDialogFragment = dialogClass.newInstance();
            mDialogFragment.put(dialogClass.getName(), baseDialogFragment);
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(baseDialogFragment, dialogClass.getName());
            //当用户需要离开当前的界面的时候
            // 只能在Activity存储状态之前调用，如果在存储状态之后进行调用，就会跑出异常，如果允许
            // 状态丢失，那么可以用commitAllowState()
            fragmentTransaction.commitAllowingStateLoss();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void cancleDialog() {
        String name = BaseDialogFragment.class.getName();
        BaseDialogFragment baseDialogFragment = mDialogFragment.get(name);
        if (baseDialogFragment != null) {
            baseDialogFragment.dismissAllowingStateLoss();
            mDialogFragment.remove(name);
        }

    }

}
