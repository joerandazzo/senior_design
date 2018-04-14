package edu.siue.mech.seniordesign.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.siue.mech.seniordesign.R;

public class SettingsCalibrateFragment extends Fragment {

    public static SettingsCalibrateFragment newInstance() {
        return new SettingsCalibrateFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_bluetooth, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        initUI();
    }

    private void initUI(){
        View view = getView();

    }
}
