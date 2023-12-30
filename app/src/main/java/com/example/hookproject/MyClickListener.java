package com.example.hookproject;

import android.view.View;
import android.widget.Toast;

public class MyClickListener implements View.OnClickListener {

    private View.OnClickListener onClickListener;

    public MyClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        //替换系统点击事件时，添加一个toast
        Toast.makeText(v.getContext(), "系统OnClickListener已经被我替换啦", Toast.LENGTH_SHORT).show();

        onClickListener.onClick(v);
    }
}
