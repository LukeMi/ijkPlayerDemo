package com.example.videostream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayoutRes());
        ButterKnife.bind(this);
        getIntentData();
        initData();
        initView();
    }

    protected void getIntentData() {

    }

    protected void initData() {

    }

    protected void initView() {

    }

    abstract int bindLayoutRes()  ;


}
