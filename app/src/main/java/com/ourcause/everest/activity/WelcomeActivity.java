package com.ourcause.everest.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;

import com.ourcause.everest.R;

public class WelcomeActivity extends AppCompatActivity {

    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mContentView = findViewById(R.id.frmMain);

        doneFullScreen();

        autoRedirect();
    }

    //实现全屏效果
    private void doneFullScreen() {
        //隐藏工具条
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //给视图添加属性， 达到隐藏状态条和全屏效果
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE   //状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏
                     | View.SYSTEM_UI_FLAG_FULLSCREEN                       //Activity全屏显示，且状态栏被隐藏覆盖掉
                     | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                     | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                     | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION           //效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                     | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);                //隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键
    }

    //自动跳转至主页面
    public void autoRedirect(){

        new Handler().postDelayed(new Runnable(){

            public void run() {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }, 3000);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
