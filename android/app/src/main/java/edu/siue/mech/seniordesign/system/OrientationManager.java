package edu.siue.mech.seniordesign.system;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Surface;
import android.view.WindowManager;

public class OrientationManager implements SensorEventListener {

    private Context context;
    private final SensorManager sensorManager;
    private final Sensor rotationSensor;
    private int lastAccuracy;
    private OrientationListener listener;
    private HandlerThread sensorThread;
    private Handler sensorHandler;

    public interface OrientationListener {
        void onOrientationChanged(float yaw, float pitch, float roll);
    }

    public OrientationManager(Context context, OrientationListener listener){
        this.context = context;
        this.sensorManager = (SensorManager)context.getSystemService(Activity.SENSOR_SERVICE);
        this.rotationSensor =sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.listener = listener;
        sensorThread = new HandlerThread("Sensor thread", Thread.MAX_PRIORITY);
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());
    }

    public void start(){
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME, sensorHandler);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
        sensorThread.quitSafely();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(lastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE){
            return;
        }
        if(sensorEvent.sensor == rotationSensor){
            updateOrientation(sensorEvent.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(lastAccuracy != accuracy){
            lastAccuracy = accuracy;
        }
    }

    //https://developer.android.com/guide/topics/sensors/sensors_position.html
    private void updateOrientation(float[] rotationVector){
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        final int worldAxisForDeviceAxisX;
        final int worldAxisForDeviceAxisY;

        // Remap the axes as if the device screen was the instrument panel,
        // and adjust the rotation matrix for the device orientation.
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        switch (windowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                break;
            case Surface.ROTATION_90:
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                break;
            case Surface.ROTATION_180:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                break;
            case Surface.ROTATION_270:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                break;
        }

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                worldAxisForDeviceAxisY, adjustedRotationMatrix);

        // Transform rotation matrix into azimuth/pitch/roll
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        // Convert radians to degrees
        float yaw = orientation[0] * -57; //x
        float pitch = orientation[1] * -57; //y
        float roll = orientation[2] * -57; //z
        listener.onOrientationChanged(yaw, pitch, roll);
    }

}
