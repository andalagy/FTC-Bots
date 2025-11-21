package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RobotConstants;

/**
 * slide babysitter: tells both motors where to park and hopes they listen ☆
 * tweak the ticks in RobotConstants whenever the real robot complains.
 */
public class SlideSubsystem {
    private final DcMotor leftSlide;
    private final DcMotor rightSlide;

    public SlideSubsystem(HardwareMap hardwareMap) {
        leftSlide = hardwareMap.get(DcMotor.class, RobotConstants.LEFT_SLIDE_NAME);
        rightSlide = hardwareMap.get(DcMotor.class, RobotConstants.RIGHT_SLIDE_NAME);

        // flip one side because my slides are mirrored in the real world ->
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

    private void moveToPosition(int targetTicks) {
        leftSlide.setTargetPosition(targetTicks);
        rightSlide.setTargetPosition(targetTicks);

        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftSlide.setPower(RobotConstants.SLIDE_POWER);
        rightSlide.setPower(RobotConstants.SLIDE_POWER);
    }
}
