package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.DriveSubsystem;
import org.firstinspires.ftc.teamcode.GateSubsystem;
import org.firstinspires.ftc.teamcode.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.SlideSubsystem;
import org.firstinspires.ftc.teamcode.VisionSubsystem;
import org.firstinspires.ftc.teamcode.VisionSubsystem.DetectedMotif;

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

        // Prestart vision detection
        DetectedMotif detectedMotif = vision.detectMotif();
        telemetry.addData("Detected Motif", detectedMotif);
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        // Fix heading so field-centric math aligns to field
        drive.resetHeading();

        // 1. Leave the Launch Line
        driveForTime(0, 0.6, 0, 1.0);

        // 2. Move toward alliance goal and raise slides
        slides.goToHigh();
        driveForTime(0, 0.5, 0, 0.7);

        // 3. Score preloaded artifacts
        sleep(300);
        gate.open();
        sleep(600);
        gate.close();

        // 4. Optional: nudge to place artifacts on ramp based on motif
        if (detectedMotif == DetectedMotif.MOTIF_B) {
            driveForTime(0.4, 0, 0, 0.6); // strafe right slightly
        } else if (detectedMotif == DetectedMotif.MOTIF_C) {
            driveForTime(-0.4, 0, 0, 0.6); // strafe left slightly
        }

        // 5. Lower slides for travel and move to base/park
        slides.goToIntake();
        driveForTime(0, -0.5, 0, 1.1);

        // Hold position
        drive.stop();
    }

    /** Simple helper to drive using field-centric power for a duration. */
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
