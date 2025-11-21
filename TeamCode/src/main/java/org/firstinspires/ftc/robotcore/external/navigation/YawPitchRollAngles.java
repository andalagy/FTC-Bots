package org.firstinspires.ftc.robotcore.external.navigation;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class YawPitchRollAngles {
    private double yaw;
    private double pitch;
    private double roll;

    public YawPitchRollAngles(double yaw, double pitch, double roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public double getYaw(RevHubOrientationOnRobot.AngleUnit unit) {
        if (unit == RevHubOrientationOnRobot.AngleUnit.DEGREES) {
            return Math.toDegrees(yaw);
        }
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setAngles(double yaw, double pitch, double roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }
}
