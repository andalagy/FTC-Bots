package org.firstinspires.ftc.robotcontroller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcEventLoopIdle;
import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.ftccommon.Restarter;
import com.qualcomm.ftccommon.UpdateUI;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.eventloop.opmode.AnnotatedOpModeRegistrar;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;
import com.qualcomm.robotcore.util.Dimmer;

/**
 * Minimal Robot Controller activity that wires the FTC SDK service to the app module
 * so annotated OpModes from the TeamCode library can be discovered and run.
 */
public class RobotControllerActivity extends Activity {

    private FtcRobotControllerService robotControllerService;
    private UpdateUI updateUI;
    private UpdateUI.Callback uiCallback;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (!(service instanceof FtcRobotControllerService.FtcRobotControllerBinder)) {
                return;
            }
            robotControllerService = ((FtcRobotControllerService.FtcRobotControllerBinder) service).getService();
            updateUI.setControllerService(robotControllerService);
            setupRobot();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            robotControllerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dimmer dimmer = new Dimmer(this);
        updateUI = new UpdateUI(this, dimmer);

        TextView deviceName = findViewById(R.id.textDeviceName);
        TextView networkStatus = findViewById(R.id.textNetworkStatus);
        TextView robotStatus = findViewById(R.id.textRobotStatus);
        TextView gamepad1 = findViewById(R.id.textGamepad1);
        TextView gamepad2 = findViewById(R.id.textGamepad2);
        TextView opMode = findViewById(R.id.textOpMode);
        TextView errorMessage = findViewById(R.id.textErrorMessage);

        updateUI.setTextViews(deviceName, networkStatus, new TextView[]{gamepad1, gamepad2}, robotStatus, opMode, errorMessage);
        uiCallback = updateUI.new Callback();
        updateUI.setRestarter(new Restarter() {
            @Override
            public void requestRestart() {
                restartRobot();
            }
        });

        Intent controllerServiceIntent = new Intent(this, FtcRobotControllerService.class);
        startService(controllerServiceIntent);
        bindService(controllerServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupRobot() {
        if (robotControllerService == null) {
            return;
        }
        OpModeRegister register = manager -> AnnotatedOpModeRegistrar.register(manager);
        HardwareFactory hardwareFactory = new HardwareFactory(this);
        FtcEventLoop eventLoop = new FtcEventLoop(hardwareFactory, register, uiCallback, this);
        FtcEventLoopIdle idleLoop = new FtcEventLoopIdle(hardwareFactory, register, uiCallback, this);
        robotControllerService.setCallback(uiCallback);
        robotControllerService.setupRobot(eventLoop, idleLoop, () -> { });
    }

    private void restartRobot() {
        if (robotControllerService != null) {
            robotControllerService.shutdownRobot();
            setupRobot();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (robotControllerService != null) {
            robotControllerService.shutdownRobot();
            robotControllerService = null;
        }
        unbindService(serviceConnection);
    }
}
