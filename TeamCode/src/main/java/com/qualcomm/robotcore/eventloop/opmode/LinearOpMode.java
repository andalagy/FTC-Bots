package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Telemetry;

public abstract class LinearOpMode {
    protected final HardwareMap hardwareMap = new HardwareMap();
    protected final Telemetry telemetry = new Telemetry();
    protected final Gamepad gamepad1 = new Gamepad();
    protected final Gamepad gamepad2 = new Gamepad();

    public abstract void runOpMode() throws InterruptedException;

    public void waitForStart() {
        // no-op stub
    }

    public boolean opModeIsActive() {
        return false;
    }

    public boolean isStopRequested() {
        return false;
    }

    public void sleep(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
}
