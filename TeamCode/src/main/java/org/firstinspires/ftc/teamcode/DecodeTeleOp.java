package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

/**
 * Driver-controlled program that wires gamepad inputs into the drive, intake, slides, and gate.
 * Uses the subsystem classes so hardware setup stays in one place and the OpMode stays readable.
 * Field-centric drive math keeps controls intuitive while comments explain the flow for new teammates.
 */
@TeleOp(name = "Decode TeleOp", group = "Main")
public class DecodeTeleOp extends LinearOpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private SlideSubsystem slides;
    private GateSubsystem gate;
    private VisionSubsystem vision;

    @Override
    public void runOpMode() throws InterruptedException {
        // Build each subsystem so we reuse the same hardware mapping everywhere
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        slides = new SlideSubsystem(hardwareMap);
        gate = new GateSubsystem(hardwareMap);
        vision = new VisionSubsystem(hardwareMap);

        gate.close();
        telemetry.addLine("TeleOp ready — press play when the field says go");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        // zero heading before moving so field-centric drive lines up with the real field
        drive.resetHeading();

        while (opModeIsActive()) {
            // Drive control: left stick moves the robot around the field, right stick rotates it like a car joystick
            double y = -gamepad1.left_stick_y; // forward on the stick is negative in FTC, so flip it
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x;
            boolean slowMode = gamepad1.right_bumper; // hold for gentle mode when lining up
            if (gamepad1.left_bumper) {
                drive.resetHeading(); // quick re-zero if field-centric starts to feel off
            }
            drive.drive(x, y, rotation, slowMode);

            // Intake control: right trigger sucks game pieces in, left trigger spits them back out
            if (gamepad2.right_trigger > 0.1) {
                intake.intakeIn();
            } else if (gamepad2.left_trigger > 0.1) {
                intake.intakeOut();
            } else {
                intake.stop();
            }

            // Gate control: A opens the bucket gate, B closes it
            if (gamepad2.a) {
                gate.open();
            } else if (gamepad2.b) {
                gate.close();
            }

            // Slide control: left stick on gamepad 2 raises or lowers with simple soft limits
            double slideInput = -gamepad2.left_stick_y; // push up to extend, pull down to retract
            slides.manualControl(slideInput);

            // Vision heartbeat: placeholder call so we know the camera thread won't crash the OpMode later
            VisionSubsystem.DetectedMotif detectedMotif = vision.detectMotif();

            telemetry.addData("Heading (deg)", "%.1f", drive.getHeadingDegrees());
            telemetry.addData("Slides (ticks)", slides.getAveragePosition());
            telemetry.addData("Vision motif", detectedMotif);
            telemetry.update();
        }

        // make sure everything is stopped once the driver hits stop
        drive.stop();
        intake.stop();
        slides.stop();
        gate.close();
    }
}
