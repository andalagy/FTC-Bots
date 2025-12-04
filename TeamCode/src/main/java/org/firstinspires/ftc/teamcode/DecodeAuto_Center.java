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

/**
 * Center-side auto with the same subsystems as TeleOp.
 * Drives forward to score the preload, branches parking based on the detected motif.
 */
@Autonomous(name = "Decode Auto Center", group = "Main")
public class DecodeAuto_Center extends LinearOpMode {
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

        DetectedMotif detectedMotif = DetectedMotif.MOTIF_B;
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

        // 1. drive straight to the backdrop
        drive.driveStraightWithHeading(26, 0.55, 0, this);

        // 2. raise slides and score
        slides.goToPreset(SlidePreset.HIGH);
        while (opModeIsActive() && !slides.isAtTarget() && !slides.isFaulted()) {
            telemetry.addData("Step", "Raising slides");
            slides.addTelemetry(telemetry);
            telemetry.update();
            idle();
        }
        if (slides.isFaulted()) {
            telemetry.addData("Slide fault", slides.getFaultReason());
            telemetry.update();
            return;
        }

        gate.open();
        sleep(600);
        gate.close();

        // 3. retract and park according to the motif
        slides.goToPreset(SlidePreset.INTAKE);
        drive.driveStraightWithHeading(-6, 0.45, 0, this);
        if (detectedMotif == DetectedMotif.MOTIF_A) {
            drive.strafeWithHeading(-12, 0.45, 0, this);
        } else if (detectedMotif == DetectedMotif.MOTIF_C) {
            drive.strafeWithHeading(12, 0.45, 0, this);
        }

        drive.stop();
        vision.stop();
    }
}
