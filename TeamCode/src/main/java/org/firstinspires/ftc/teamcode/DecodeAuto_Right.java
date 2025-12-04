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
 * Right-side auto that uses encoder + IMU helpers for multi-segment scoring.
 * Includes a simple one-cycle branch and mirrored variant for the opposite wall.
 */
@Autonomous(name = "Decode Auto Right", group = "Main")
public class DecodeAuto_Right extends LinearOpMode {
    protected boolean isMirrored() {
        return false;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        DriveSubsystem drive = new DriveSubsystem(hardwareMap);
        IntakeSubsystem intake = new IntakeSubsystem(hardwareMap);
        SlideSubsystem slides = new SlideSubsystem(hardwareMap);
        GateSubsystem gate = new GateSubsystem(hardwareMap);
        VisionSubsystem vision = new VisionSubsystem(hardwareMap);

        gate.close();
        vision.start();

        boolean cycleRequested = false;
        DetectedMotif detectedMotif = DetectedMotif.MOTIF_B;
        while (!isStarted() && !isStopRequested()) {
            detectedMotif = vision.getCurrentMotif();
            if (gamepad1.a) {
                cycleRequested = true;
            }
            telemetry.addData("Detected Motif", detectedMotif);
            telemetry.addData("Cycle after preload?", cycleRequested);
            telemetry.addData("Mirrored?", isMirrored());
            telemetry.update();
        }

        waitForStart();
        if (isStopRequested()) {
            vision.stop();
            return;
        }

        drive.resetHeading();
        detectedMotif = vision.getCurrentMotif();
        double mirror = isMirrored() ? -1.0 : 1.0;

        // 1. leave the launch line and bias toward the right wall
        drive.driveStraightWithHeading(22, 0.55, 0, this);

        // 2. raise slides while shifting over to the right spike/backdrop lane
        slides.goToPreset(SlidePreset.HIGH);
        drive.strafeWithHeading(10 * mirror, 0.55, 0, this);
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

        // 3. bump into scoring range and dump the preload
        drive.driveStraightWithHeading(8, 0.35, 0, this);
        gate.open();
        sleep(600);
        gate.close();

        // 4. optional quick cycle: dip to the stack, grab, and re-score at LOW
        if (cycleRequested) {
            slides.goToPreset(SlidePreset.INTAKE);
            drive.driveStraightWithHeading(-12, 0.45, 0, this);
            drive.strafeWithHeading(-8 * mirror, 0.5, 0, this);

            intake.intakeIn();
            drive.driveStraightWithHeading(10, 0.35, 0, this); // creep into the stack
            sleep(500);
            drive.driveStraightWithHeading(-10, 0.4, 0, this);
            intake.stop();

            drive.strafeWithHeading(6 * mirror, 0.55, 0, this);
            slides.goToPreset(SlidePreset.LOW);
            while (opModeIsActive() && !slides.isAtTarget() && !slides.isFaulted()) {
                telemetry.addData("Step", "Cycling to LOW");
                slides.addTelemetry(telemetry);
                telemetry.update();
                idle();
            }

            if (slides.isFaulted()) {
                telemetry.addData("Slide fault", slides.getFaultReason());
                telemetry.update();
                return;
            }

            drive.driveStraightWithHeading(6, 0.35, 0, this);
            gate.open();
            sleep(450);
            gate.close();
        }

        // 5. retract and park according to the detected motif
        slides.goToPreset(SlidePreset.INTAKE);
        drive.driveStraightWithHeading(-6, 0.45, 0, this);
        if (detectedMotif == DetectedMotif.MOTIF_A) {
            drive.strafeWithHeading(-14 * mirror, 0.5, 0, this);
        } else if (detectedMotif == DetectedMotif.MOTIF_C) {
            drive.strafeWithHeading(14 * mirror, 0.5, 0, this);
        }

        drive.stop();
        vision.stop();
    }
}
