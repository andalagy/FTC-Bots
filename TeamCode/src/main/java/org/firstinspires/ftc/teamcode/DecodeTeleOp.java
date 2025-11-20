package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GateSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem;

@TeleOp(name = "Decode TeleOp", group = "Main")
public class DecodeTeleOp extends LinearOpMode {

    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private SlideSubsystem slides;
    private GateSubsystem gate;

    private final ElapsedTime dumpTimer = new ElapsedTime();
    private boolean dumping = false;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        slides = new SlideSubsystem(hardwareMap);
        gate = new GateSubsystem(hardwareMap);

        gate.close();

        telemetry.addLine("DECODE TeleOp ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // ---- Drive (field-centric) ----
            double x = gamepad1.left_stick_x; // strafe
            double y = -gamepad1.left_stick_y; // forward is negative on stick
            double rotation = gamepad1.right_stick_x;
            boolean slow = gamepad1.left_bumper || gamepad1.right_bumper;

            if (gamepad1.a) {
                drive.resetHeading();
            }
            drive.drive(x, y, rotation, slow);

            // ---- Intake ----
            if (gamepad2.right_trigger > 0.3) {
                intake.intakeIn();
            } else if (gamepad2.left_trigger > 0.3) {
                intake.intakeOut();
            } else {
                intake.stop();
            }

            // ---- Slides presets ----
            if (gamepad2.dpad_down) {
                slides.goToIntake();
            } else if (gamepad2.dpad_left) {
                slides.goToLow();
            } else if (gamepad2.dpad_right) {
                slides.goToHigh();
            } else if (gamepad2.dpad_up) {
                slides.goToMax();
            }

            // ---- Gate ----
            if (gamepad2.a) {
                gate.open();
                dumping = false;
            } else if (gamepad2.b) {
                gate.close();
                dumping = false;
            } else if (gamepad2.y && !dumping) {
                // Start a quick dump: open for 0.4s then close
                gate.open();
                dumpTimer.reset();
                dumping = true;
            }

            if (dumping && dumpTimer.seconds() > 0.4) {
                gate.close();
                dumping = false;
            }

            telemetry.addData("Dumping", dumping);
            telemetry.addData("Slide busy", slides.isBusy());
            telemetry.update();
        }
    }
}
