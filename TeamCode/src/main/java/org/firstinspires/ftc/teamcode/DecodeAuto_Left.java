package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem.DetectedMotif;

/**
 * Simple left-side auto that drives off the line, raises slides, and drops the preload.
 * Uses the same subsystems as TeleOp so hardware changes only happen in one place.
 * Vision is a placeholder; detection is polled once so we can branch later if needed.
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
        vision = new VisionSubsystem(hardwareMap);

        gate.close();

        // peek at vision before we ever hit start ☆
        DetectedMotif detectedMotif = vision.detectMotif();
        telemetry.addData("Detected Motif", detectedMotif);
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        // zero out the heading so field-centric math matches reality ->
        drive.resetHeading();

        // 1. leave the Launch Line
        driveForTime(0, 0.6, 0, 1.0);

        // 2. shuffle toward the goal while cranking the slides up :)
        slides.goToHigh();
        driveForTime(0, 0.5, 0, 0.7);

        // 3. dump the preloaded artifacts
        sleep(300);
        gate.open();
        sleep(600);
        gate.close();

        // 4. optional nudge based on which motif we spotted ★
        if (detectedMotif == DetectedMotif.MOTIF_B) {
            driveForTime(0.4, 0, 0, 0.6); // strafe right slightly :)
        } else if (detectedMotif == DetectedMotif.MOTIF_C) {
            driveForTime(-0.4, 0, 0, 0.6); // strafe left slightly :)
        }

        // 5. slides down and a gentle retreat to the base/park
        slides.goToIntake();
        driveForTime(0, -0.5, 0, 1.1);

        // hold position ☆
        drive.stop();
    }

    /**
     * lazy timer drive so i don't rewrite the same loop every step.
     */
    private void driveForTime(double x, double y, double rotation, double seconds) {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        while (opModeIsActive() && timer.seconds() < seconds) {
            drive.drive(x, y, rotation, false);
            telemetry.addData("Auto step", "Driving");
            telemetry.update();
        }
        drive.stop();
    }
}
