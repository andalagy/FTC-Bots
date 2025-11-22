package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RobotConstants;

/**
 * fake eyeballs for guessing the OBELISK motif until a real pipeline shows up
 * rip this out for EasyOpenCV or whatever once i pick get real camera manager
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
