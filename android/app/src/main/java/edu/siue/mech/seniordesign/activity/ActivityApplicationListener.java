package edu.siue.mech.seniordesign.activity;


public interface ActivityApplicationListener{
    void onConnectBT();
    void onDisconnectBT();

    void turnMotorsOn();
    void turnMotorsOff();
}
