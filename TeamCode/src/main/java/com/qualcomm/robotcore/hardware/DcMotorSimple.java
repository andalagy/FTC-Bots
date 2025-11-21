package com.qualcomm.robotcore.hardware;

public class DcMotorSimple {
    public enum Direction {
        FORWARD,
        REVERSE
    }

    private Direction direction = Direction.FORWARD;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
