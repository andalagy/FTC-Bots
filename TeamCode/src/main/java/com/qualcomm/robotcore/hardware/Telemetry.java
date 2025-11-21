package com.qualcomm.robotcore.hardware;

import java.util.ArrayList;
import java.util.List;

public class Telemetry {
    private final List<String> lines = new ArrayList<>();

    public void addLine(String line) {
        lines.add(line);
    }

    public void addData(String caption, Object value) {
        lines.add(caption + ": " + value);
    }

    public void update() {
        // nothing to do in stub
    }

    public List<String> getLines() {
        return lines;
    }
}
