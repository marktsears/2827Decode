package org.firstinspires.ftc.teamcode.auto;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
@Autonomous(name="redSideAuto", group="Iterative OpMode")
public class redSideAuto extends OpMode {
    private DcMotor leftFrontDrive;
    private DcMotor rightFrontDrive;
    private DcMotor rightBackDrive;
    private DcMotor leftBackDrive;
    private DcMotor lift;
    private DcMotor intake;
    private DcMotorEx shooter;

    private Servo turret;
    private Servo hood;
    private Servo transfer;
    private Servo intakeRaiser;
    private Servo blocker;

    Limelight3A limelight3A;

    private IMU imu;

    private double Distance;

    private ElapsedTime runtime = new ElapsedTime();
    double shooterPower=0;
    boolean ReadyToFire=false;
    boolean RightAngle=true;
    double turretPosition= 0;
    int State=1;


    @Override
    public void init () {
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "FL");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "FR");



        leftBackDrive  = hardwareMap.get(DcMotor.class, "BL");
        rightBackDrive = hardwareMap.get(DcMotor.class, "BR");
        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



        limelight3A= hardwareMap.get(Limelight3A.class, "limelight");

        imu = hardwareMap.get(IMU.class, "imu");

        blocker = hardwareMap.get(Servo.class, "blocker");
        hood = hardwareMap.get(Servo.class, "hood");
        turret = hardwareMap.get(Servo.class, "turret");
        transfer = hardwareMap.get(Servo.class, "transfer");

        blocker.setPosition(0.0);
        transfer.setPosition(0.35);
        turret.setPosition (0);

        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.pipelineSwitch(0);
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));


        telemetry.addData("Status", "Initialized");


    }
    @Override
    public void init_loop() {

    }
    @Override
    public void start(){
        runtime.reset();
        limelight3A.pipelineSwitch(0);
        limelight3A.start();

    }
    @Override
    public void loop(){
        shooter.setVelocity(-28*3800/60);
        LLResult llResult = limelight3A.getLatestResult();

        double max;



        double shooterRPM=(shooter.getVelocity()*60)/-28;
        turret.setPosition(turretPosition);
        if (State==1) {
            if (ReadyToFire) {
                blocker.setPosition(0.5);
                runtime.reset();
                while (runtime.seconds()< 0.5){

                }
                transfer.setPosition(0.35);
                runtime.reset();
                intake.setPower(-1);
                while (runtime.seconds() < 0.5) {

                }
                State=2;
            }
        }
        else if (State==2) {
            if (ReadyToFire) {
                blocker.setPosition(0.5);
                runtime.reset();
                while (runtime.seconds()< 0.5){

                }
                transfer.setPosition(0.35);
                runtime.reset();
                intake.setPower(-1);
                while (runtime.seconds() < 0.5) {

                }
                State=3;
            }
        }
        else if (State==3){
            if (ReadyToFire) {
                blocker.setPosition(0.5);
                runtime.reset();
                while (runtime.seconds()< 0.5){

                }
                transfer.setPosition(0.35);
                runtime.reset();
                intake.setPower(-1);
                while (runtime.seconds() < 0.5) {

                }
                State=4;
            }

        }
        else if (State==4){
            blocker.setPosition(0.5);
            State=5;
        }
        else if (State==5){
            leftFrontDrive.setPower(0);
            rightFrontDrive.setPower(0);
            leftBackDrive.setPower(0);
            rightBackDrive.setPower(0);
            intake.setPower(0);
            shooter.setVelocity(0);
            blocker.setPosition(0);
        }






        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("RPM", "Shooter: " + shooterRPM);
        telemetry.addData("Fire", "Ready: " + ReadyToFire);
        telemetry.addData("state", State);



        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight3A.updateRobotOrientation(orientation.getYaw());

        if (llResult != null && llResult.isValid()){
            Pose3D botPose = llResult.getBotpose_MT2();
            telemetry.addData("Tx",llResult.getTx());
            telemetry.addData("Ty",llResult.getTy());
            telemetry.addData("Ta",llResult.getTa());
            telemetry.addData("Botpose", botPose.toString());
            telemetry.addData("turretPosition", turretPosition);
            //telemetry.addData("April Tag", llResult.getTid());
        }
        telemetry.update();


        if ((shooterRPM > 3700) && (shooterRPM < 3900) ){

            ReadyToFire=true;

        }










    }
}
