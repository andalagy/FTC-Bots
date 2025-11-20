package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotConstants;

/** Controls a simple gate/bucket servo for scoring ARTIFACTS. */
public class GateSubsystem {
    private final Servo gateServo;

    public GateSubsystem(HardwareMap hardwareMap) {
        gateServo = hardwareMap.get(Servo.class, RobotConstants.GATE_SERVO_NAME);
    }

    public void open() {
        gateServo.setPosition(RobotConstants.GATE_OPEN);
    }

    public void close() {
        gateServo.setPosition(RobotConstants.GATE_CLOSED);
    }

    /** Quickly opens then closes the gate. Call periodically in loop to allow time to move. */
    public void dump() {
        open();
    }
}
