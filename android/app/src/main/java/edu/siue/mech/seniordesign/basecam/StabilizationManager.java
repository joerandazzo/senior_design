package edu.siue.mech.seniordesign.basecam;


import android.bluetooth.BluetoothSocket;
import android.os.Debug;
import android.os.Handler;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import java.io.IOException;

import edu.siue.mech.seniordesign.system.BluetoothConnection;

public class StabilizationManager {
    private static final String TAG = StabilizationManager.class.getSimpleName();
    BluetoothConnection connection;
    public StabilizationManager(){
    }

    public void setBluetooth(BluetoothConnection connection){
        this.connection = connection;
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

        sendData(CMD.CONTROL.value, values);
    }

    public void sendData(final int command, final byte[] values){
        final byte[] packet = getPacket(command, values);
        connection.write(packet);
    }

    //Packet definition on page 3 of serial API docs
    private static byte[] getPacket(int commandId, byte[] values){
        if(commandId == 77 || commandId == 109) {  //Construct MOTORS_OFF/ON Command
            byte[] packet = new byte[6];
            byte[] dataSize = new byte[0];
            int modulus = (byte) ((commandId + dataSize.length) % 256);
            packet[0] = 62; //Start character
            packet[1] = ((byte) (commandId & 0xFF)); //Command ID (Unsigned)
            packet[2] = ((byte) (dataSize.length & 0xFF)); //Data size (Unsigned)
            packet[3] = ((byte) (modulus & 0xFF)); //Header checksum (Unsigned)
            return packet;
        } else if (commandId == 67) { //Construct CONTROL Command
            byte[] packet = new byte[18];
            int modulus = (byte) ((commandId + values.length) % 256);
            packet[0] = 62; //Start character
            packet[1] = ((byte) (commandId & 0xFF)); //Command ID (Unsigned)
            packet[2] = ((byte) (values.length & 0xFF)); //Data size (Unsigned)
            packet[3] = ((byte) (modulus & 0xFF)); //Header checksum (Unsigned)

            byte checksum = 0;
            for (int i=4; i<values.length+4; i++){
                packet[i] = values[i-4];
                checksum += values[i-4];
            }

            packet[17] = (byte)((checksum % 256) & 0xFF);

            return packet;
        } else { // If we get an unrecognized command, we shut the motors off -> not sure this is the best option
            byte[] packet = new byte[6];
            byte[] dataSize = new byte[0];
            int modulus = (byte) ((77 + dataSize.length) % 256);
            packet[0] = 62; //Start character
            packet[1] = ((byte) (commandId & 0xFF)); //Command ID (Unsigned)
            packet[2] = ((byte) (dataSize.length & 0xFF)); //Data size (Unsigned)
            packet[3] = ((byte) (modulus & 0xFF)); //Header checksum (Unsigned)
            return packet;
        }
    }
}
