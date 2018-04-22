package edu.siue.mech.seniordesign;


import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import edu.siue.mech.seniordesign.activity.ActivityApplicationListener;
import edu.siue.mech.seniordesign.basecam.StabilizationManager;
import edu.siue.mech.seniordesign.system.ApplicationLifecycle;
import edu.siue.mech.seniordesign.system.BluetoothConnection;
import edu.siue.mech.seniordesign.system.OrientationManager;

public class GimbalApplication extends Application implements ApplicationLifecycle.ApplicationLifecycleListener, ActivityApplicationListener {

    private static final String TAG = GimbalApplication.class.getSimpleName();

    StabilizationManager stabilizationManager;
    OrientationManager orientationManager;
    BluetoothConnection connection;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ApplicationLifecycle(this));
        connection = new BluetoothConnection(listener);
        orientationManager = new OrientationManager(this, orientationListener);
        stabilizationManager = new StabilizationManager();
        stabilizationManager.setBluetooth(connection);
    }

    private BluetoothConnection.BTConnectionListener listener = new BluetoothConnection.BTConnectionListener() {
        @Override
        public void onDeviceNotFound() {
            Log.d(TAG, "Device not found");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Device NOT FOUND", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDeviceConnectFail() {
            Log.d(TAG, "Device connect fail");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Device CONNECT FAIL", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDeviceConnected(BluetoothSocket socket) {
            Log.d(TAG, "Device connected");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Device CONNECTED", Toast.LENGTH_LONG).show();
                }
            });
            orientationManager.start();
        }

        @Override
        public void onDeviceDisconnected() {
            Log.d(TAG, "Device disconnected");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Device disconnected", Toast.LENGTH_LONG).show();
                }
            });
            orientationManager.stop();
        }
    };

    private OrientationManager.OrientationListener orientationListener = new OrientationManager.OrientationListener() {
        @Override
        public void onOrientationChanged(final float yaw, final float pitch, final float roll) {
//            Log.d(TAG, String.format(" x: %.2f , y:%.2f, z: %.2f", yaw, pitch, roll));

            stabilizationManager.sendOrientation(yaw, pitch, roll);
        }
    };

    @Override
    public void onConnectBT() {
        Toast.makeText(getApplicationContext(), "Attempting to connect", Toast.LENGTH_SHORT).show();
        connection.connect();
    }

    @Override
    public void onDisconnectBT() {
        Toast.makeText(getApplicationContext(), "Attempting to disconnect", Toast.LENGTH_LONG).show();
        connection.disconnect();
    }

    @Override
    public void turnMotorsOn() {
        Toast.makeText(getApplicationContext(), "Attempting to turn on motors", Toast.LENGTH_LONG).show();
        stabilizationManager.turnOnMotors();
    }

    @Override
    public void turnMotorsOff() {
        Toast.makeText(getApplicationContext(), "Attempting to turn off motors", Toast.LENGTH_LONG).show();
        stabilizationManager.turnOffMotors();
    }

    @Override
    public void onAppForegrounded(Activity activity) {
        orientationManager.start();
    }

    @Override
    public void onAppBackgrounded(Activity activity) {
        orientationManager.stop();
    }
}
