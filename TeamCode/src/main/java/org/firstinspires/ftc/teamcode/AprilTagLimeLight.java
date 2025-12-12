package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.LLDetection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
//import org.firstinspires.ftc.external.navigation.AngleUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import java.util.List;
@TeleOp(name="AprilTagLimeLight", group="Iterative OpMode")
public class AprilTagLimeLight extends OpMode {
    //private Limelight3A limelight;
    //private IMU imu;
    private double distance;
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    @Override
    public void init(){
        //limelight = hardwareMap.get(Limelight3A.class, "limelight");
        //limelight.pipelineSwitch(0);
        //imu = hardwareMap.get(IMU.class, "imu");
        //RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,
                //RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        //imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        aprilTag = new AprilTagProcessor.Builder()
                .build();
        visionPortal = new VisionPortal.Builder()
               // .setCamera(hardwareMap.get(webcamName.class,"limelight"))
                .addProcessor(aprilTag)
                .build();
        telemetry.addData("Status", "Initialized. Press Play.");

    }


    @Override
    public void start() {
        //limelight.start();
    }
    @Override
    public void loop(){
        //YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        //limelight.updateRobotOrientation(orientation.getYaw());
        //LLResult llResult = limelight.getLatestResult();
/*
        if (llResult != null && llResult.isValid()){
           // List<LLDetection>
            Pose3D botPose = llResult.getBotpose_MT2();
            telemetry.addData("Tx",llResult.getTx());
            telemetry.addData("Ty",llResult.getTy());
            telemetry.addData("Ta",llResult.getTa());
            telemetry.addData("Botpose", botPose.toString());
           // for (int i = 0; i<detections)
        }
        telemetry.update();

*/
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        if (currentDetections.size()>0){
            for (AprilTagDetection detection : currentDetections) {
                int tagId = detection.id;
                if (detection.metadata != null){
                    String tagName = detection.metadata.name;

                    telemetry.addData("Detected Tag Id", tagId);
                    telemetry.addData("Tag Name", tagName);
                }else{
                    telemetry.addData("Detected Tag Id (Metadata Missing)", tagId);
                }

                //telemetry.addData("tag name", tagName);
            }
        }else {
            telemetry.addData("Status", "No April Tags Detected");
        }
    }
}
