package edu.siue.mech.seniordesign;


import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import edu.siue.mech.seniordesign.command.OrientationManager;

public class DesignApplication extends Application implements ApplicationLifecycle.ApplicationLifecycleListener {

    private static final String TAG = DesignApplication.class.getSimpleName();

    OrientationManager orientationManager;
    BluetoothConnection connection;

    @Override
    public void onCreate(){
        super.onCreate();
        registerActivityLifecycleCallbacks(new ApplicationLifecycle(this));
        connection = new BluetoothConnection(listener);
        orientationManager = new OrientationManager(this, orientationListener);
    }

    private BluetoothConnection.BTConnectionListener listener = new BluetoothConnection.BTConnectionListener() {
        @Override
        public void onDeviceNotFound() {
            Log.d(TAG, "Device not found");
            Toast.makeText(getApplicationContext(), "Device NOT FOUND", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDeviceConnectFail() {
            Log.d(TAG, "Device connect fail");
            Toast.makeText(getApplicationContext(), "Device CONNECT FAIL", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDeviceConnected(BluetoothSocket socket) {
            Log.d(TAG, "Device connected");
            Toast.makeText(getApplicationContext(), "Device CONNECTED", Toast.LENGTH_LONG).show();

            byte command = 109;
            byte[] wut = new byte[0];
            int modulus = (byte) ((command + wut.length) % 256);
            byte[] packet = new byte[6];
            byte b1 = 0;

            packet[0] = 62;
            packet[1] = ((byte) (command & 0xFF));
            packet[2] = ((byte) (wut.length & 0xFF));
            packet[3] = ((byte) (modulus & 0xFF));
            try {
                socket.getOutputStream().write(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDeviceDisconnected() {
            Log.d(TAG, "Device disconnected");
            Toast.makeText(getApplicationContext(), "Device disconnected", Toast.LENGTH_LONG).show();
        }
    };

    private OrientationManager.OrientationListener orientationListener = new OrientationManager.OrientationListener() {
        @Override
        public void onOrientationChanged(final float yaw, final float pitch, final float roll) {
            Log.d(TAG, String.format(" x: %.2f , y:%.2f, z: %.2f", yaw, pitch, roll));
        }
    };

    @Override
    public void onAppForegrounded(Activity activity) {
        orientationManager.start();
    }

    @Override
    public void onAppBackgrounded(Activity activity) {
        orientationManager.stop();
    }
}
