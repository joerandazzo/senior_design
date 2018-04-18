package edu.siue.mech.seniordesign.basecam;


import android.bluetooth.BluetoothSocket;
import android.support.v4.app.INotificationSideChannel;
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
        byte [] values = new byte[1]; //Garbage values so we don't get a compile error
        sendData(CMD.MOTORS_ON.value, values);
    }

    public void turnOffMotors(){
        byte [] values = new byte[1]; //Garbage values so we don't get a compile error
        sendData(CMD.MOTORS_OFF.value, values);
    }

    public void sendOrientation(float yaw, float pitch, float roll){
        //We are dealing with 1 degree increments, so truncate fractional portion off
        short yaw_i = (short)yaw;
        short pitch_i = (short)pitch;
        short roll_i = (short)roll;

        // Speeds set to 0 so they are controlled by board. Idea is that the time increments are
        // small enough that it might not be noticeable
        byte [] values = new byte[]{(byte)(02 & 0xFF), //Control code for MODE_ANGLE
                (byte)((roll_i & 0xFF00)>>8), (byte)(roll_i & 0x00FF), 0x00,  0x00, //Roll angle, split into two byte component, and two bytes for speed
                (byte)((pitch_i & 0xFF00)>>8), (byte)(pitch_i & 0x00FF), 0x00, 0x00, //Pitch angle, split into two byte components, and two bytes for speed
                (byte)((yaw_i & 0xFF00)>>8), (byte)(yaw_i & 0x00FF), 0x00, 0x00}; //Yaw angle, split into two byte components, and two bytes for speed
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
        } else if (commandId == 67) { //Construct CONTROL Command
            packet[0] = 62; //Start character
            packet[1] = ((byte) (commandId & 0xFF)); //Command ID (Unsigned)
            packet[2] = ((byte) (dataSize.length & 0xFF)); //Data size (Unsigned)
            packet[3] = ((byte) (modulus & 0xFF)); //Header checksum (Unsigned)
            //packet[4] = CONTROL_MODE (Unsigned)
            //packet[5] = first byte in ROLL_SPEED (Signed)
            //packet[6] = second "    "  "     "      "
            //packet[7] = first byte in ROLL_ANGLE (Signed)
            //packet[8] = second "    "   "    "      "
            //packet[9] = first byte in PITCH_SPEED (Signed)
            //packet[10] = second "    "  "     "      "
            //packet[11] = first byte in PITCH_ANGLE (Signed)
            //packet[12] = second "    "   "    "      "
            //packet[13] = first byte in YAW_SPEED (Signed)
            //packet[14] = second "    "  "     "      "
            //packet[15] = first byte in YAW_ANGLE (Signed)
            //packet[16] = second "    "   "    "      "
            //packet[17] = body checksum (unsigned)

        }
        // packet[4] = ((byte) (values.length & 0xFF));
        //packet[5] = ??
        return packet;
    }
}
