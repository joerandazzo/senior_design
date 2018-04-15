package edu.siue.mech.seniordesign.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.siue.mech.seniordesign.GimbalApplication;
import edu.siue.mech.seniordesign.R;

public class MainActivity extends AppCompatActivity {

    ActivityApplicationListener applicationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        applicationListener = (GimbalApplication)getApplication();
    }

    private void initUI(){
        findViewById(R.id.ivSettings).setOnClickListener(onClickListener);
        findViewById(R.id.btnConnect).setOnClickListener(onClickListener);
        findViewById(R.id.btnDisconnect).setOnClickListener(onClickListener);
        findViewById(R.id.btnTurnMotorsOn).setOnClickListener(onClickListener);
        findViewById(R.id.btnTurnMotorsOff).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btnConnect:
                    connectToBluetooth();
                    break;
                case R.id.btnDisconnect:
                    disconnectToBluetooth();
                    break;
                case R.id.btnTurnMotorsOn:
                    turnMotorsOn();
                    break;
                case R.id.btnTurnMotorsOff:
                    turnMotorsOff();
                    break;
                case R.id.ivSettings:
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void connectToBluetooth(){
       applicationListener.onConnectBT();
    }

    private void disconnectToBluetooth(){
        applicationListener.onDisconnectBT();
    }

    private void turnMotorsOn(){
        applicationListener.turnMotorsOn();
    }

    private void turnMotorsOff(){
        applicationListener.turnMotorsOff();
    }

}
