package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RobotConstants;

/**
 * Stub vision subsystem for detecting the OBELISK motif pattern.
 * Replace fake detection with your preferred vision pipeline (e.g., EasyOpenCV).
 */
public class VisionSubsystem {

    public enum DetectedMotif {
        MOTIF_A,
        MOTIF_B,
        MOTIF_C
    }

    public VisionSubsystem(HardwareMap hardwareMap) {
        // TODO: Initialize camera and pipeline using hardwareMap.get(WebcamName.class, RobotConstants.WEBCAM_NAME)
    }

    /**
     * Fake detection placeholder. Replace with real vision logic.
     */
    public DetectedMotif detectMotif() {
        // TODO: return live result from pipeline
        return DetectedMotif.MOTIF_A;
    }
}
