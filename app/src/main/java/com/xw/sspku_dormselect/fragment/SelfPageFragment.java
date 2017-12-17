package com.xw.sspku_dormselect.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xw.sspku_dormselect.R;

/**
 * Created by momian on 2017/12/15.
 */

public class SelfPageFragment extends Fragment {

    public  SelfPageFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_self,container,false);
        return view;
    }
}
