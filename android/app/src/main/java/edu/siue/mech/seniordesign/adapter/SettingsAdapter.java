package edu.siue.mech.seniordesign.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import edu.siue.mech.seniordesign.R;
import edu.siue.mech.seniordesign.fragment.SettingsBluetoothFragment;
import edu.siue.mech.seniordesign.fragment.SettingsCalibrateFragment;

public class SettingsAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments;
    private Context context;

    public SettingsAdapter(FragmentManager fm, Context context) {
        super(fm);
        registeredFragments = new SparseArray<>();
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (SettingsPage.values()[position]) {
            case CALIBRATE:
                return SettingsCalibrateFragment.newInstance();
            case BLUETOOTH:
                return SettingsBluetoothFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return SettingsPage.values().length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SettingsPage page = SettingsPage.getFromOrdinal(position);
        if (page != null) {
            return page.getTitle(context);
        }
        return null;
    }

    public Fragment getRegisteredFragmentAt(int position) {
        return registeredFragments.get(position);
    }

    //===============================================================
    // Page Values
    //===============================================================

    public enum SettingsPage {
        CALIBRATE(R.string.calibrate),
        BLUETOOTH(R.string.bluetooth);

        private int titleResource;

        SettingsPage(int titleResource) {
            this.titleResource = titleResource;
        }

        public String getTitle(Context context) {
            return context.getString(titleResource);
        }

        public static SettingsPage getFromOrdinal(int position) {
            if (position < 0 || position > SettingsPage.values().length) {
                return null;
            }
            return SettingsPage.values()[position];
        }

        public static SettingsPage getFromName(String name) {
            for (SettingsPage settingsPage : SettingsPage.values()) {
                if (settingsPage.name().equals(name)) {
                    return settingsPage;
                }
            }
            return CALIBRATE; //Default value
        }
    }
}

