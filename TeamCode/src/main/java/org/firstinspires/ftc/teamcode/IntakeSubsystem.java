package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RobotConstants;

/** ball suck/release gadget so i remember which trigger does what ☆ */
public class IntakeSubsystem {
    private final DcMotor intakeMotor;

    public IntakeSubsystem(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, RobotConstants.INTAKE_NAME);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void intakeIn() {
        intakeMotor.setPower(1.0);
    }

    public void intakeOut() {
        intakeMotor.setPower(-1.0);
    }

    public void stop() {
        intakeMotor.setPower(0);
    }
}
