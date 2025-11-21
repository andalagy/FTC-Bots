package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotConstants;

/** little trap door buddy for flinging cargo out of the bucket ★ */
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

    /** quick dump flick; call in a loop so the servo actually gets time to move. */
    public void dump() {
        open();
    }
}
