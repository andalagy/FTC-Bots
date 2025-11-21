package com.qualcomm.hardware.rev;

public class RevHubOrientationOnRobot {
    public enum LogoFacingDirection {
        UP, DOWN, LEFT, RIGHT, FORWARD, BACKWARD
    }

    public enum UsbFacingDirection {
        UP, DOWN, LEFT, RIGHT, FORWARD, BACKWARD
    }

    public enum AngleUnit {
        RADIANS,
        DEGREES
    }

    private final LogoFacingDirection logoFacingDirection;
    private final UsbFacingDirection usbFacingDirection;

    public RevHubOrientationOnRobot(LogoFacingDirection logoFacingDirection, UsbFacingDirection usbFacingDirection) {
        this.logoFacingDirection = logoFacingDirection;
        this.usbFacingDirection = usbFacingDirection;
    }

    public LogoFacingDirection getLogoFacingDirection() {
        return logoFacingDirection;
    }

    public UsbFacingDirection getUsbFacingDirection() {
        return usbFacingDirection;
    }
}
