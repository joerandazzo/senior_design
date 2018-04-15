package edu.siue.mech.seniordesign.basecam;


//From serial API documentations, page 4
public enum CMD {
    CONTROL(67),
    MOTORS_ON(77),
    MOTORS_OFF(109);

    int value;
    CMD(int value){
        this.value = value;
    }
}
