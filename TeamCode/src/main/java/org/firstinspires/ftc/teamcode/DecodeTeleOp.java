package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.RobotConstants;

@TeleOp(name = "Decode TeleOp", group = "Main")
public class DecodeTeleOp extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() throws InterruptedException {
        // grab the drive motors that already exist in the configuration screen
        frontLeft = hardwareMap.get(DcMotor.class, RobotConstants.FRONT_LEFT_NAME);
        frontRight = hardwareMap.get(DcMotor.class, RobotConstants.FRONT_RIGHT_NAME);
        backLeft = hardwareMap.get(DcMotor.class, RobotConstants.BACK_LEFT_NAME);
        backRight = hardwareMap.get(DcMotor.class, RobotConstants.BACK_RIGHT_NAME);

        // flip the left side so pushing the stick forward actually drives forward
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // hold position when the driver lets go instead of coasting away
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("TeleOp ready — waiting for start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // read the sticks in plain english: forward/back, strafe, and rotation
            double drive = -gamepad1.left_stick_y; // FTC maps forward as negative, so flip it
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            // blend the three motions into the four wheels like a shopping cart with side wheels
            double denominator = Math.max(Math.abs(drive) + Math.abs(strafe) + Math.abs(turn), 1.0);
            double frontLeftPower = (drive + strafe + turn) / denominator;
            double backLeftPower = (drive - strafe + turn) / denominator;
            double frontRightPower = (drive - strafe - turn) / denominator;
            double backRightPower = (drive + strafe - turn) / denominator;

            // send power out; all math is normalized so nothing ever tops ±1
            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            // quick dashboard so the driver knows what the robot thinks it's doing
            telemetry.addData("Drive", "%.2f", drive);
            telemetry.addData("Strafe", "%.2f", strafe);
            telemetry.addData("Turn", "%.2f", turn);
            telemetry.update();
        }
    }
}
