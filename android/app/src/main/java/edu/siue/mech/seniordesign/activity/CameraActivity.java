package edu.siue.mech.seniordesign.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import edu.siue.mech.seniordesign.R;
import edu.siue.mech.seniordesign.adapter.SettingsAdapter;
import edu.siue.mech.seniordesign.ui.SlidingTabLayout;

public class CameraActivity extends AppCompatActivity {

    private ViewPager vpSettings;
    private SettingsAdapter settingsAdapter;
    private SlidingTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUI();
    }

    private void initUI() {
        settingsAdapter = new SettingsAdapter(getSupportFragmentManager(), this);
        vpSettings = findViewById(R.id.vpSettings);
        vpSettings.setAdapter(settingsAdapter);
        vpSettings.setOffscreenPageLimit(SettingsAdapter.SettingsPage.values().length - 1);
        tabLayout = findViewById(R.id.stHeader);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setCustomTabView(R.layout.custom_tab_item, R.id.tvHeaderTitle, android.R.color.white);
        tabLayout.setViewPager(vpSettings);

    }
}
