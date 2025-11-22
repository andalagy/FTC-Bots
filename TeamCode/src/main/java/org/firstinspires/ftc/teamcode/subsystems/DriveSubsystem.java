package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.RobotConstants;

/**
 * Drives the mecanum drivetrain and keeps track of heading for field-centric control.
 * The IMU gives us robot yaw so we can rotate joystick input into field space.
 * Use resetHeading() whenever the driver's sense of "forward" no longer matches the bot.
 */
public class DriveSubsystem {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final IMU imu;

    private double headingOffset = 0; // little offset knob so we can re-zero during TeleOp

    public DriveSubsystem(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, RobotConstants.FRONT_LEFT_NAME);
        frontRight = hardwareMap.get(DcMotor.class, RobotConstants.FRONT_RIGHT_NAME);
        backLeft = hardwareMap.get(DcMotor.class, RobotConstants.BACK_LEFT_NAME);
        backRight = hardwareMap.get(DcMotor.class, RobotConstants.BACK_RIGHT_NAME);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(IMU.class, RobotConstants.IMU_NAME);
        // tell the hub how it's actually bolted on so yaw numbers aren't wacky ->
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                LogoFacingDirection.UP, UsbFacingDirection.FORWARD));
        imu.initialize(parameters);
    }

    /**
     * Drive helper for field-centric mecanum math.
     * We rotate the joystick vector by the robot heading so pushing forward always heads up-field.
     * Rotation input is blended in and everything gets normalized so no wheel commands exceed ±1.
     */
    public void drive(double x, double y, double rotation, boolean slowMode) {
        double heading = getHeadingRadians();
        // rotate the joystick vector by the robot heading so controls stay field oriented
        double rotatedX = x * Math.cos(-heading) - y * Math.sin(-heading);
        double rotatedY = x * Math.sin(-heading) + y * Math.cos(-heading);

        double denominator = Math.max(Math.abs(rotatedY) + Math.abs(rotatedX) + Math.abs(rotation), 1.0);
        double frontLeftPower = (rotatedY + rotatedX + rotation) / denominator;
        double backLeftPower = (rotatedY - rotatedX + rotation) / denominator;
        double frontRightPower = (rotatedY - rotatedX - rotation) / denominator;
        double backRightPower = (rotatedY + rotatedX - rotation) / denominator;

        double scale = slowMode ? RobotConstants.SLOW_SPEED : RobotConstants.NORMAL_SPEED;
        frontLeft.setPower(frontLeftPower * scale);
        frontRight.setPower(frontRightPower * scale);
        backLeft.setPower(backLeftPower * scale);
        backRight.setPower(backRightPower * scale);
    }

    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /** sets whatever yaw we're at as the new "zero" for field-centric stuff. */
    public void resetHeading() {
        headingOffset = getRawHeadingRadians();
    }

    /** quick helper for telemetry so drivers can see field heading in degrees. */
    public double getHeadingDegrees() {
        return Math.toDegrees(getHeadingRadians());
    }

    /** current heading in radians with the offset hack applied */
    private double getHeadingRadians() {
        return getRawHeadingRadians() - headingOffset;
    }

    /** raw yaw from the IMU, in radians. */
    private double getRawHeadingRadians() {
        return imu.getRobotYawPitchRollAngles().getYaw(RevHubOrientationOnRobot.AngleUnit.RADIANS);
    }
}
