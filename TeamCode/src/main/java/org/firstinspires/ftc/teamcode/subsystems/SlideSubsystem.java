package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RobotConstants;

/**
 * Manages the dual slide motors so they move together and stop at safe heights.
 * Preset helpers move to common scoring heights, while manualControl lets the driver trim with a stick.
 * Encoder checks keep the slides inside the intake-to-max window to protect the rigging.
 */
public class SlideSubsystem {
    private final DcMotor leftSlide;
    private final DcMotor rightSlide;

    public SlideSubsystem(HardwareMap hardwareMap) {
        leftSlide = hardwareMap.get(DcMotor.class, RobotConstants.LEFT_SLIDE_NAME);
        rightSlide = hardwareMap.get(DcMotor.class, RobotConstants.RIGHT_SLIDE_NAME);

        // flip one side because the slides are mirrored in the real world ->
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        rightSlide.setDirection(DcMotorSimple.Direction.FORWARD);

        leftSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void goToIntake() {
        moveToPosition(RobotConstants.SLIDE_INTAKE);
    }

    public void goToLow() {
        moveToPosition(RobotConstants.SLIDE_LOW);
    }

    public void goToHigh() {
        moveToPosition(RobotConstants.SLIDE_HIGH);
    }

    public void goToMax() {
        moveToPosition(RobotConstants.SLIDE_MAX);
    }

    public void stop() {
        leftSlide.setPower(0);
        rightSlide.setPower(0);
    }

    public boolean isBusy() {
        return leftSlide.isBusy() || rightSlide.isBusy();
    }

    /**
     * Manual override for the driver stick with simple software end stops.
     * Adds a tiny holding power when centered so the slides do not drift back down.
     */
    public void manualControl(double input) {
        double avgPosition = getAveragePosition();
        double requestedPower = input * RobotConstants.SLIDE_POWER;

        // lock out movement past the soft limits
        if ((input > 0 && avgPosition >= RobotConstants.SLIDE_MAX) ||
                (input < 0 && avgPosition <= RobotConstants.SLIDE_INTAKE)) {
            requestedPower = 0;
        }

        // small deadband with a gentle hold near where we left the slides
        if (Math.abs(input) < 0.05) {
            if (avgPosition > RobotConstants.SLIDE_INTAKE + 10) {
                requestedPower = RobotConstants.SLIDE_HOLD_POWER;
            } else {
                requestedPower = 0;
            }
        }

        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftSlide.setPower(requestedPower);
        rightSlide.setPower(requestedPower);
    }

    public int getAveragePosition() {
        return (leftSlide.getCurrentPosition() + rightSlide.getCurrentPosition()) / 2;
    }

    private void moveToPosition(int targetTicks) {
        leftSlide.setTargetPosition(targetTicks);
        rightSlide.setTargetPosition(targetTicks);

        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftSlide.setPower(RobotConstants.SLIDE_POWER);
        rightSlide.setPower(RobotConstants.SLIDE_POWER);
    }
}
