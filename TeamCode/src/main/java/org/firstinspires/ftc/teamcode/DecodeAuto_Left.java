package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem.SlidePreset;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem.DetectedMotif;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem.BackdropTarget;

/**
 * Left-side auto that drives off the line, scores the preload, and parks based on the detected motif.
 * Uses encoder + IMU helpers for straighter paths without Road Runner.
 */
@Autonomous(name = "Decode Auto Left", group = "Main")
public class DecodeAuto_Left extends LinearOpMode {

    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private SlideSubsystem slides;
    private GateSubsystem gate;
    private VisionSubsystem vision;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        slides = new SlideSubsystem(hardwareMap);
        gate = new GateSubsystem(hardwareMap);
        vision = new VisionSubsystem(hardwareMap, telemetry);

        gate.close();
        vision.start();

        // update telemetry during init so drivers see the live motif
        DetectedMotif detectedMotif = DetectedMotif.MOTIF_A;
        while (!isStarted() && !isStopRequested()) {
            detectedMotif = vision.getCurrentMotif();
            vision.applyCameraControls();
            telemetry.addData("Detected Motif", detectedMotif);
            telemetry.addData("Vision camera", vision.getCameraStatus());
            telemetry.update();
        }

        waitForStart();
        if (isStopRequested()) {
            vision.stop();
            return;
        }

        drive.resetHeading();
        detectedMotif = vision.getCurrentMotif();
        vision.useAprilTags();

        // 1. leave the Launch Line
        drive.driveStraightWithHeading(20, 0.5, 0, this);

        // 2. slide up while strafing toward the left spike mark/backdrop
        slides.goToPreset(SlidePreset.HIGH);
        drive.strafeWithHeading(-8, 0.5, 0, this);
        while (opModeIsActive() && !slides.isAtTarget()) {
            telemetry.addData("Step", "Raising slides");
            telemetry.addData("Slide pos", slides.getAveragePosition());
            telemetry.addData("Tag seen?", vision.getBackdropTarget(detectedMotif) != null);
            telemetry.update();
            idle();
        }

        // 3. bump forward to scoring position and dump
        drive.driveStraightWithHeading(8, 0.35, 0, this);

        // Correct lateral offset using the backdrop tag if visible
        BackdropTarget target = vision.getBackdropTarget(detectedMotif);
        if (target != null) {
            double strafe = Math.max(-10, Math.min(10, target.getLateralInches()));
            drive.strafeWithHeading(strafe, 0.35, 0, this);
        }
        gate.open();
        sleep(600);
        gate.close();

        // 4. drop slides and back away
        slides.goToPreset(SlidePreset.INTAKE);
        drive.driveStraightWithHeading(-8, 0.4, 0, this);

        // 5. park based on motif
        if (detectedMotif == DetectedMotif.MOTIF_A) {
            drive.strafeWithHeading(-16, 0.5, 0, this); // left parking zone
        } else if (detectedMotif == DetectedMotif.MOTIF_C) {
            drive.strafeWithHeading(16, 0.5, 0, this); // right parking zone
        } // motif B stays put

        // confirm parking column/tag alignment
        target = vision.getBackdropTarget(detectedMotif);
        telemetry.addData("Parking tag", target != null ? target.tagId : "none");
        telemetry.update();

        drive.stop();
        vision.stop();
    }
}
