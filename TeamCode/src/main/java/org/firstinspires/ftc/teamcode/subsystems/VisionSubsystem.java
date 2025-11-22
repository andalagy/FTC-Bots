package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Placeholder vision helper so the rest of the robot code can stay stable while we pick a pipeline.
 * Right now it only reports a hardcoded motif, but the structure is ready for EasyOpenCV or TFOD later.
 * The hardware map and camera name are here so swapping in a real detector is a single drop-in change.
 */
public class VisionSubsystem {

    public enum DetectedMotif {
        MOTIF_A,
        MOTIF_B,
        MOTIF_C
    }

    public VisionSubsystem(HardwareMap hardwareMap) {
        // TODO: wire up the camera + pipeline once we pick one (hardwareMap.get(WebcamName.class, RobotConstants.WEBCAM_NAME)) :)
    }

    /**
     * make-believe detection until the camera does something smarter
     */
    public DetectedMotif detectMotif() {
        // todo: return the live result from whatever pipeline replaces this
        return DetectedMotif.MOTIF_A;
    }
}
