package com.qualcomm.robotcore.util;

public class ElapsedTime {
    private long startTime = System.nanoTime();

    public void reset() {
        startTime = System.nanoTime();
    }

    public double seconds() {
        return (System.nanoTime() - startTime) / 1_000_000_000.0;
    }
}
