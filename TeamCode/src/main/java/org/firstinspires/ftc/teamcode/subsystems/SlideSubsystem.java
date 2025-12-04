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
    public enum SlidePreset {
        INTAKE,
        LOW,
        HIGH,
        MAX
    }

    private final DcMotor leftSlide;
    private final DcMotor rightSlide;
    private int targetPositionTicks = RobotConstants.SLIDE_INTAKE;

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

    public void goToPreset(SlidePreset preset) {
        switch (preset) {
            case LOW:
                moveToPosition(RobotConstants.SLIDE_LOW);
                break;
            case HIGH:
                moveToPosition(RobotConstants.SLIDE_HIGH);
                break;
            case MAX:
                moveToPosition(RobotConstants.SLIDE_MAX);
                break;
            case INTAKE:
            default:
                moveToPosition(RobotConstants.SLIDE_INTAKE);
                break;
        }
    }

    public void goToIntake() {
        goToPreset(SlidePreset.INTAKE);
    }

    public void goToLow() {
        goToPreset(SlidePreset.LOW);
    }

    public void goToHigh() {
        goToPreset(SlidePreset.HIGH);
    }

    public void goToMax() {
        goToPreset(SlidePreset.MAX);
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
        } else {
            targetPositionTicks = (int) avgPosition; // handoff to manual sets new hold position
        }

        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftSlide.setPower(requestedPower);
        rightSlide.setPower(requestedPower);
    }

    public void stop() {
        leftSlide.setPower(0);
        rightSlide.setPower(0);
    }

    public int getAveragePosition() {
        return (leftSlide.getCurrentPosition() + rightSlide.getCurrentPosition()) / 2;
    }

    public boolean isAtTarget() {
        return Math.abs(getAveragePosition() - targetPositionTicks) <= RobotConstants.SLIDE_POSITION_TOLERANCE;
    }

    public int getTargetPosition() {
        return targetPositionTicks;
    }

    private void moveToPosition(int targetTicks) {
        targetPositionTicks = enforceLimits(targetTicks);

        leftSlide.setTargetPosition(targetPositionTicks);
        rightSlide.setTargetPosition(targetPositionTicks);

        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // FTC SDK runs a built-in P controller in RUN_TO_POSITION; tune power + target values above
        leftSlide.setPower(RobotConstants.SLIDE_POWER);
        rightSlide.setPower(RobotConstants.SLIDE_POWER);
    }

    private int enforceLimits(int desiredTicks) {
        if (desiredTicks > RobotConstants.SLIDE_MAX) {
            return RobotConstants.SLIDE_MAX;
        }
        if (desiredTicks < RobotConstants.SLIDE_INTAKE) {
            return RobotConstants.SLIDE_INTAKE;
        }
        return desiredTicks;
    }
}
