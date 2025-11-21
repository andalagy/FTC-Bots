package org.firstinspires.ftc.teamcode;

/**
 * scribble pad for hardware names, directions, and the latest "feels right" numbers ☆
 * flip things here whenever the wiring or vibes change and hope future-me remembers.
 */
public class RobotConstants {
    // motor names as i typed them into the Robot Configuration screen ->
    public static final String FRONT_LEFT_NAME = "frontLeft";
    public static final String FRONT_RIGHT_NAME = "frontRight";
    public static final String BACK_LEFT_NAME = "backLeft";
    public static final String BACK_RIGHT_NAME = "backRight";

    public static final String INTAKE_NAME = "intakeMotor";
    public static final String LEFT_SLIDE_NAME = "leftSlide";
    public static final String RIGHT_SLIDE_NAME = "rightSlide";

    public static final String GATE_SERVO_NAME = "gateServo";
    public static final String IMU_NAME = "imu";
    public static final String WEBCAM_NAME = "Webcam 1";

    // drive speed scales
    public static final double NORMAL_SPEED = 0.9;   // full power scale for teleop ☆
    public static final double SLOW_SPEED = 0.4;     // precision mode when holding a bumper

    // slide target positions (encoder ticks). tune for your robot.
    public static final int SLIDE_INTAKE = 0;
    public static final int SLIDE_LOW = 850;      // rough guess, tweak as needed
    public static final int SLIDE_HIGH = 1600;
    public static final int SLIDE_MAX = 1900;     // don't let the slides yeet themselves

    // slide motion settings
    public static final double SLIDE_POWER = 1.0;
    public static final double SLIDE_HOLD_POWER = 0.05; // tiny holding power for manual modes :)

    // gate servo positions (0-1). tune for your hardware.
    public static final double GATE_CLOSED = 0.15;
    public static final double GATE_OPEN = 0.65;

    // vision defaults
    public enum Motif {
        MOTIF_A, MOTIF_B, MOTIF_C
    }
}
