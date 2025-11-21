package com.qualcomm.robotcore.hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class IMU {
    public static class Parameters {
        public final RevHubOrientationOnRobot orientationOnRobot;

        public Parameters(RevHubOrientationOnRobot orientationOnRobot) {
            this.orientationOnRobot = orientationOnRobot;
        }
    }

    private final YawPitchRollAngles angles = new YawPitchRollAngles(0, 0, 0);

    public boolean initialize(Parameters parameters) {
        return true;
    }

    public YawPitchRollAngles getRobotYawPitchRollAngles() {
        return angles;
    }
}
