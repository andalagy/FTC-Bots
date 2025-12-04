package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem.SlidePreset;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem.DetectedMotif;

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

    private enum MacroState {IDLE, GOING_UP, DUMPING, RETURNING}
    private MacroState macroState = MacroState.IDLE;
    private final ElapsedTime macroTimer = new ElapsedTime();

    private boolean headingHoldToggleLatch = false;

    @Override
    public void runOpMode() throws InterruptedException {
        // Build each subsystem so we reuse the same hardware mapping everywhere
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        slides = new SlideSubsystem(hardwareMap);
        gate = new GateSubsystem(hardwareMap);
        vision = new VisionSubsystem(hardwareMap);

        gate.close();
        vision.start();
        telemetry.addLine("TeleOp ready — press play when the field says go");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) {
            vision.stop();
            return;
        }

        // zero heading before moving so field-centric drive lines up with the real field
        drive.resetHeading();
        drive.enableHeadingHold(false);

        while (opModeIsActive()) {
            // Drive control: left stick moves the robot around the field, right stick rotates it like a car joystick
            double y = -gamepad1.left_stick_y; // forward on the stick is negative in FTC, so flip it
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x;
            boolean slowMode = gamepad1.right_bumper; // hold for gentle mode when lining up
            if (gamepad1.left_bumper) {
                drive.resetHeading(); // quick re-zero if field-centric starts to feel off
            }
            boolean headingHoldButton = gamepad1.x;
            if (headingHoldButton && !headingHoldToggleLatch) {
                drive.enableHeadingHold(!drive.isHeadingHoldEnabled());
            }
            headingHoldToggleLatch = headingHoldButton;

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

            // Slide control + scoring macro
            double slideInput = -gamepad2.left_stick_y; // push up to extend, pull down to retract
            boolean macroRequested = gamepad2.y;
            if (macroRequested && macroState == MacroState.IDLE) {
                // Start a simple high-score macro: stop intake, raise, dump, return
                intake.stop();
                gate.close();
                slides.goToPreset(SlidePreset.HIGH);
                macroState = MacroState.GOING_UP;
            }

            if (Math.abs(slideInput) > 0.1 && macroState != MacroState.IDLE) {
                // Driver intervention cancels the macro
                macroState = MacroState.IDLE;
            }

            if (macroState == MacroState.IDLE) {
                slides.manualControl(slideInput);
            } else {
                runMacro();
            }

            // Vision heartbeat: report live motif
            DetectedMotif detectedMotif = vision.getCurrentMotif();

            telemetry.addData("Heading (deg)", "%.1f", drive.getHeadingDegrees());
            telemetry.addData("Heading hold", drive.isHeadingHoldEnabled());
            telemetry.addData("Slides target", slides.getTargetPosition());
            telemetry.addData("Slides pos", slides.getAveragePosition());
            telemetry.addData("Slide at target?", slides.isAtTarget());
            telemetry.addData("Vision motif", detectedMotif);
            telemetry.update();
        }

        // make sure everything is stopped once the driver hits stop
        drive.stop();
        intake.stop();
        slides.stop();
        gate.close();
        vision.stop();
    }

    private void runMacro() {
        switch (macroState) {
            case GOING_UP:
                if (slides.isAtTarget()) {
                    gate.open();
                    macroTimer.reset();
                    macroState = MacroState.DUMPING;
                }
                break;
            case DUMPING:
                if (macroTimer.milliseconds() > 600) {
                    gate.close();
                    slides.goToPreset(SlidePreset.INTAKE);
                    macroTimer.reset();
                    macroState = MacroState.RETURNING;
                }
                break;
            case RETURNING:
                if (slides.isAtTarget()) {
                    macroState = MacroState.IDLE;
                }
                break;
            case IDLE:
            default:
                break;
        }
    }
}
