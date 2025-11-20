package org.firstinspires.ftc.teamcode;

/**
 * Central place for tuning hardware names, directions, power limits, and mechanism setpoints.
 * Update values here to match your robot configuration.
 */
public class RobotConstants {
    // Motor names in the Robot Configuration
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

    // Drive speed scales
    public static final double NORMAL_SPEED = 0.9;   // Full power scale for teleop
    public static final double SLOW_SPEED = 0.4;     // Precision mode when holding a bumper

    // Slide target positions (encoder ticks). Tune for your robot.
    public static final int SLIDE_INTAKE = 0;
    public static final int SLIDE_LOW = 850;      // Example values
    public static final int SLIDE_HIGH = 1600;
    public static final int SLIDE_MAX = 1900;     // Keep below mechanical limit

    // Slide motion settings
    public static final double SLIDE_POWER = 1.0;
    public static final double SLIDE_HOLD_POWER = 0.05; // Small holding power for manual modes

    // Gate servo positions (0-1). Tune for your hardware.
    public static final double GATE_CLOSED = 0.15;
    public static final double GATE_OPEN = 0.65;

    // Vision defaults
    public enum Motif {
        MOTIF_A, MOTIF_B, MOTIF_C
    }
}
