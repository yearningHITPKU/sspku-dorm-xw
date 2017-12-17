package com.xw.sspku_dormselect;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private Fragment selfFragment;
    private Fragment checkInFragment;
    private LinearLayout selfTab;
    private ImageView selfIcon;
    private TextView selfTxt;
    private LinearLayout checkInTab;
    private ImageView checkInIcon;
    private TextView checkInTxt;
    private int nowIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
        initAction();

        selectTab(0);

        getSupportFragmentManager().beginTransaction().hide(checkInFragment).show(selfFragment).commit();

    }

    private void initView() {

        selfFragment = getSupportFragmentManager().findFragmentById(R.id.fg_self);
        checkInFragment = getSupportFragmentManager().findFragmentById(R.id.fg_check_in);
        selfTab = (LinearLayout) findViewById(R.id.btn_f);
        selfIcon = (ImageView) findViewById(R.id.img_icon_f);
        selfTxt = (TextView) findViewById(R.id.txt_title_f);
        checkInTab = (LinearLayout) findViewById(R.id.btn_s);
        checkInIcon = (ImageView) findViewById(R.id.img_icon_s);
        checkInTxt = (TextView) findViewById(R.id.txt_title_s);
    }

    private void initAction() {
        selfTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(0);
            }
        });

        checkInTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(1);
            }
        });
    }

    private void selectTab(int index) {

        if(nowIndex == index){
            return;
        }

        nowIndex = index;
        selfIcon.setImageDrawable(getResources().getDrawable(index == 0 ? R.drawable.icon_self_cllick : R.drawable.icon_self));
        selfTxt.setTextColor(Color.parseColor(index == 0 ? "#1296db" : "#333333"));
        checkInIcon.setImageDrawable(getResources().getDrawable(index == 1 ? R.drawable.icon_check_in_click : R.drawable.icon_check_in));
        checkInTxt.setTextColor(Color.parseColor(index == 1 ? "#1296db" : "#333333"));
        getSupportFragmentManager().beginTransaction().hide(index == 0 ? checkInFragment : selfFragment).show(index == 0 ? selfFragment : checkInFragment).commit();
    }
}
