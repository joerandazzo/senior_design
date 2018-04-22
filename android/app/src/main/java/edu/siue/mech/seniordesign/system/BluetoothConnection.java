package edu.siue.mech.seniordesign.system;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnection {
    private static final String TAG = BluetoothConnection.class.getSimpleName();
    private static final UUID ID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter bluetoothAdapter;
    BTConnectionListener listener;
    ConnectThread connectThread;

    public interface BTConnectionListener {
        void onDeviceNotFound();
        void onDeviceConnectFail();
        void onDeviceConnected(BluetoothSocket socket);
        void onDeviceDisconnected();
    }

    public BluetoothConnection(BTConnectionListener listener) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.listener = listener;
    }

    public void connect() {
        BluetoothDevice device = getBaseCam();
        if (device == null) {
            this.listener.onDeviceNotFound();
        }

        //disconnect if open
        disconnect();

        //create connection
        connectThread = new ConnectThread(device, new ConnectListener() {
            @Override
            public void onConnectSuccess(BluetoothSocket socket) {
                listener.onDeviceConnected(socket);
            }

            @Override
            public void onConnectFail() {
                listener.onDeviceConnectFail();
            }
        });

        //actually connect
        connectThread.start();
    }

    public void disconnect(){
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
    }

    public void write(byte[] packet){
        if(connectThread == null || connectThread.socket == null || !connectThread.socket.isConnected()){
            return;
        }

        connectThread.write(packet);
    }

    private BluetoothDevice getBaseCam() {
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null) {
            return null;
        }

        BluetoothDevice foundDevice = null;
        for (BluetoothDevice device : pairedDevices) {
            Log.d(TAG, device.getName() + device.getAddress());
            if (device.getAddress().contains("21:13:01:93:79")) {
                Log.d(TAG, "FOUND");
                foundDevice = device;
            }
        }
        return foundDevice;
    }

    public interface ConnectListener {
        void onConnectSuccess(BluetoothSocket socket);

        void onConnectFail();
    }

    class ConnectThread extends Thread {
        final BluetoothSocket socket;
        ConnectListener connectListener;

        public ConnectThread(BluetoothDevice device, ConnectListener listener) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            this.connectListener = listener;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createInsecureRfcommSocketToServiceRecord(ID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            socket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                connectListener.onConnectFail();
                return;
            }
            connectListener.onConnectSuccess(socket);
        }

        public synchronized void write(byte[] packet){
            try {
                if(socket == null || socket.getOutputStream() == null){
                    Log.d(TAG, "Tried to send data but there is no socket, check connection");
                    return;
                }
                socket.getOutputStream().write(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
            listener.onDeviceDisconnected();
        }
    }
}
