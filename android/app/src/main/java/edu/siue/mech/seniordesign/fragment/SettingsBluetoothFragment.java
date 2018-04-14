package edu.siue.mech.seniordesign.fragment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;

import edu.siue.mech.seniordesign.R;

public class SettingsBluetoothFragment extends Fragment {

    private static final String TAG = SettingsBluetoothFragment.class.getSimpleName();
    BluetoothAdapter bluetoothAdapter;

    public static SettingsBluetoothFragment newInstance() {
        return new SettingsBluetoothFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_bluetooth, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initUI();
    }

    @Override
    public void onResume(){
        super.onResume();
        getPairedDevice();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void initUI(){
        View view = getView();

    }

    private void getPairedDevice(){
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices == null){
            return;
        }

        BluetoothDevice foundDevice = null;
        for(BluetoothDevice device : pairedDevices){
            Log.d(TAG, device.getName() + device.getAddress());
            if(device.getAddress().contains("21:13:01:93:79")){
                Log.d(TAG, "FOUND");
                foundDevice = device;
            }
        }
        if(foundDevice != null){
            ((ImageView)getActivity().findViewById(R.id.ivStatus)).setImageResource(R.drawable.shape_circle_green);
            ((TextView)getActivity().findViewById(R.id.tvName)).setText(foundDevice.getName());
        }else{
            ((ImageView)getActivity().findViewById(R.id.ivStatus)).setImageResource(R.drawable.shape_circle_red);
            ((TextView)getActivity().findViewById(R.id.tvName)).setText("Not found");
        }
    }

}
