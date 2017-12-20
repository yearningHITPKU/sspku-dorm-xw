package com.xw.sspku_dormselect.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xw.sspku_dormselect.R;
import com.xw.sspku_dormselect.app.SelectDormApp;

/**
 * Created by xw on 2017/12/15.
 */

public class CheckInPageFragment extends Fragment {

    private TextView btnCohabitS;
    private TextView btnCohabitD;
    private TextView btnCohabitT;
    private TextView btnCohabitF;
    private ImageView ivSelected;

    private TextView cohabitDes;
    private LinearLayout llCohabitD;
    private LinearLayout llCohabitT;
    private LinearLayout llCohabitF;

    private int itemWidth;

    public  CheckInPageFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in,container,false);

        initView(view);
        initAction();

        changeType(0);

        return view;
    }

    private void initView(View view) {
        btnCohabitS = (TextView) view.findViewById(R.id.btn_cohabit_s);
        btnCohabitD = (TextView) view.findViewById(R.id.btn_cohabit_d);
        btnCohabitT = (TextView) view.findViewById(R.id.btn_cohabit_t);
        btnCohabitF = (TextView) view.findViewById(R.id.btn_cohabit_f);
        ivSelected = (ImageView) view.findViewById(R.id.iv_selected);

        cohabitDes = (TextView) view.findViewById(R.id.txt_cohabit_des);
        llCohabitD = (LinearLayout) view.findViewById(R.id.ll_cohabit_d);
        llCohabitT = (LinearLayout) view.findViewById(R.id.ll_cohabit_t);
        llCohabitF = (LinearLayout) view.findViewById(R.id.ll_cohabit_f);

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        itemWidth = wm.getDefaultDisplay().getWidth() / 4;
    }

    private void initAction() {
        btnCohabitS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeType(0);
                ((SelectDormApp)(getActivity().getApplication())).choosenType = 0;
            }
        });

        btnCohabitD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeType(1);
                ((SelectDormApp)(getActivity().getApplication())).choosenType = 1;
            }
        });

        btnCohabitT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeType(2);
                ((SelectDormApp)(getActivity().getApplication())).choosenType = 2;
            }
        });

        btnCohabitF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeType(3);
                ((SelectDormApp)(getActivity().getApplication())).choosenType = 3;
            }
        });
    }

    private void changeType(int cohabitNum) {
        cohabitDes.setVisibility(cohabitNum > 0 ? View.VISIBLE : View.GONE);
        llCohabitD.setVisibility(cohabitNum > 0 ? View.VISIBLE : View.GONE);
        llCohabitT.setVisibility(cohabitNum > 1 ? View.VISIBLE : View.GONE);
        llCohabitF.setVisibility(cohabitNum > 2 ? View.VISIBLE : View.GONE);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivSelected.getLayoutParams();
        lp.leftMargin = itemWidth * cohabitNum + itemWidth / 2 - 35;
        ivSelected.setLayoutParams(lp);
    }
}
