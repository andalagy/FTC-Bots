package org.firstinspires.ftc.teamcode.subsystems;

import android.content.res.Resources;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.WebcamName;

import org.firstinspires.ftc.teamcode.RobotConstants;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * EasyOpenCV-backed vision helper that streams from a webcam and classifies the sleeve/signal motif.
 * The pipeline uses simple color thresholds in three regions of interest and reports MOTIF_A/B/C.
 * Replace the thresholds/ROIs as you tune on the real field — the public API stays the same.
 */
public class VisionSubsystem {

    public enum DetectedMotif {
        MOTIF_A,
        MOTIF_B,
        MOTIF_C
    }

    private final OpenCvWebcam webcam;
    private final SleevePipeline pipeline;

    public VisionSubsystem(HardwareMap hardwareMap) {
        Resources res = hardwareMap.appContext.getResources();
        int cameraMonitorViewId = res.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, RobotConstants.WEBCAM_NAME),
                cameraMonitorViewId);
        pipeline = new SleevePipeline();
        webcam.setPipeline(pipeline);
    }

    /** start streaming to the RC phone and begin detecting motifs */
    public void start() {
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                // leave blank; telemetry in the OpMode will show MOTIF_A default if camera fails
            }
        });
    }

    /** stop streaming to free the camera for other OpModes */
    public void stop() {
        webcam.stopStreaming();
        webcam.closeCameraDeviceAsync(() -> { });
    }

    /** latest classification from the pipeline; safe to call from any OpMode loop */
    public DetectedMotif getCurrentMotif() {
        return pipeline.getCurrentMotif();
    }

    /**
     * Example pipeline that checks three side-by-side ROIs for dominant color.
     * Tune the HSV bounds for your sleeve/signal art and adjust the rectangles to match the frame.
     */
    private static class SleevePipeline extends OpenCvPipeline {
        private static final Scalar LOWER_BLUE = new Scalar(90, 60, 50);
        private static final Scalar UPPER_BLUE = new Scalar(140, 255, 255);
        private static final Scalar LOWER_GREEN = new Scalar(40, 50, 50);
        private static final Scalar UPPER_GREEN = new Scalar(85, 255, 255);
        private static final Scalar LOWER_RED1 = new Scalar(0, 70, 50);
        private static final Scalar UPPER_RED1 = new Scalar(10, 255, 255);
        private static final Scalar LOWER_RED2 = new Scalar(170, 70, 50);
        private static final Scalar UPPER_RED2 = new Scalar(180, 255, 255);

        private final Rect leftRoi = new Rect(40, 200, 160, 120);
        private final Rect centerRoi = new Rect(240, 200, 160, 120);
        private final Rect rightRoi = new Rect(440, 200, 160, 120);

        private DetectedMotif currentMotif = DetectedMotif.MOTIF_A;

        @Override
        public Mat processFrame(Mat input) {
            Mat hsv = new Mat();
            Imgproc.cvtColor(input, hsv, Imgproc.COLOR_RGB2HSV);

            double leftScore = computeScore(hsv.submat(leftRoi));
            double centerScore = computeScore(hsv.submat(centerRoi));
            double rightScore = computeScore(hsv.submat(rightRoi));

            // choose the brightest ROI as the detected motif
            if (leftScore > centerScore && leftScore > rightScore) {
                currentMotif = DetectedMotif.MOTIF_A;
            } else if (centerScore > rightScore) {
                currentMotif = DetectedMotif.MOTIF_B;
            } else {
                currentMotif = DetectedMotif.MOTIF_C;
            }

            // draw debug overlays so tuning is easier on the RC preview
            Imgproc.rectangle(input, leftRoi, new Scalar(255, 0, 0), 2);
            Imgproc.rectangle(input, centerRoi, new Scalar(0, 255, 0), 2);
            Imgproc.rectangle(input, rightRoi, new Scalar(0, 0, 255), 2);
            Imgproc.putText(input, currentMotif.name(), new org.opencv.core.Point(20, 40),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 255, 255), 2);

            hsv.release();
            return input;
        }

        private double computeScore(Mat roi) {
            // blend multiple colors so different sleeve palettes still classify
            Mat blueMask = new Mat();
            Mat greenMask = new Mat();
            Mat redMask = new Mat();
            Mat redMask2 = new Mat();
            Core.inRange(roi, LOWER_BLUE, UPPER_BLUE, blueMask);
            Core.inRange(roi, LOWER_GREEN, UPPER_GREEN, greenMask);
            Core.inRange(roi, LOWER_RED1, UPPER_RED1, redMask);
            Core.inRange(roi, LOWER_RED2, UPPER_RED2, redMask2);

            double score = Core.sumElems(blueMask).val[0] + Core.sumElems(greenMask).val[0]
                    + Core.sumElems(redMask).val[0] + Core.sumElems(redMask2).val[0];

            blueMask.release();
            greenMask.release();
            redMask.release();
            redMask2.release();
            roi.release();
            return score;
        }

        public DetectedMotif getCurrentMotif() {
            return currentMotif;
        }
    }
}
