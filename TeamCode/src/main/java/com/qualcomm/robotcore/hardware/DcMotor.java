package com.qualcomm.robotcore.hardware;

public class DcMotor extends DcMotorSimple {
    public enum ZeroPowerBehavior {
        BRAKE,
        FLOAT
    }

    public enum RunMode {
        STOP_AND_RESET_ENCODER,
        RUN_WITHOUT_ENCODER,
        RUN_USING_ENCODER,
        RUN_TO_POSITION
    }

    private double power;
    private int targetPosition;
    private ZeroPowerBehavior zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
    private RunMode mode = RunMode.RUN_WITHOUT_ENCODER;

    public void setZeroPowerBehavior(ZeroPowerBehavior behavior) {
        this.zeroPowerBehavior = behavior;
    }

    public ZeroPowerBehavior getZeroPowerBehavior() {
        return zeroPowerBehavior;
    }

    public void setMode(RunMode mode) {
        this.mode = mode;
    }

    public RunMode getMode() {
        return mode;
    }

    public void setTargetPosition(int position) {
        this.targetPosition = position;
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPower() {
        return power;
    }

    public boolean isBusy() {
        return false;
    }

    public int getCurrentPosition() {
        return 0;
    }
}
