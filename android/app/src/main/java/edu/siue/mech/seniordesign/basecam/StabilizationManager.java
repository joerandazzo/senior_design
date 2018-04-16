package edu.siue.mech.seniordesign.basecam;


import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class StabilizationManager {
    private static final String TAG = StabilizationManager.class.getSimpleName();
    BluetoothSocket socket;

    public StabilizationManager(){
    }

    public void setBTSocket(BluetoothSocket socket){
        this.socket = socket;
    }

    public void turnOnMotors(){
        sendData(CMD.MOTORS_ON.value);
    }

    public void turnOffMotors(){
        sendData(CMD.MOTORS_OFF.value);
    }

    public void sendOrientation(float yaw, float pitch, float roll){

    }

    public void sendData(int command, byte[] values){
        if(socket == null){
            Log.d(TAG, "Tried to send data but there is no socket, check connection");
            return;
        }

        final byte[] packet = getPacket(command, values);
        try {
            socket.getOutputStream().write(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Packet definition on page 3 of serial API docs
    private static byte[] getPacket(int commandId, byte[] values){
        byte[] packet = new byte[6];
        byte[] dataSize = new byte[0];
        int modulus = (byte) ((commandId + dataSize.length) % 256);

        if(commandId == 77) {  //Construct MOTORS_OFF Command
            packet[0] = 62; //Start character
            packet[1] = ((byte) (commandId & 0xFF)); //Command ID (Unsigned)
            packet[2] = ((byte) (dataSize.length & 0xFF)); //Data size (Unsigned)
            packet[3] = ((byte) (modulus & 0xFF)); //Header checksum (Unsigned)
        } else if (commandId == 109) { //Construct MOTORS_ON Command
            packet[0] = 62; //Start character
            packet[1] = ((byte) (commandId & 0xFF)); //Command ID (Unsigned)
            packet[2] = ((byte) (dataSize.length & 0xFF)); //Data size (Unsigned)
            packet[3] = ((byte) (modulus & 0xFF)); //Header Checksum (Unsigned)
        }
        // packet[4] = ((byte) (values.length & 0xFF));
        //packet[5] = ??
        return packet;
    }
}
