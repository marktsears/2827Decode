package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.hardware.limelightvision.LLResult;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name="redSideTeleop", group="Iterative OpMode")

public class redSideTeleop extends OpMode{
    int State = 0;
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
    double turretPosition= 0.175;


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



       // limelight3A= hardwareMap.get(Limelight3A.class, "limelight");

        imu = hardwareMap.get(IMU.class, "imu");

        blocker = hardwareMap.get(Servo.class, "blocker");
        hood = hardwareMap.get(Servo.class, "hood");
        turret = hardwareMap.get(Servo.class, "turret");
        transfer = hardwareMap.get(Servo.class, "transfer");
        if (State==0) {
            blocker.setPosition(0.0);
        }
        transfer.setPosition(0.35);
        turret.setPosition (0);

        //limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        //limelight3A.pipelineSwitch(0);
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
        //limelight3A.pipelineSwitch(0);
       // limelight3A.start();

    }
    @Override
    public void loop(){

       // LLResult llResult = limelight3A.getLatestResult();

        double max;


        double axial   = -gamepad1.right_stick_x;  // Note: pushing stick forward gives negative value
        double lateral =  -gamepad1.left_stick_x;
        double yaw     =  gamepad1.left_stick_y ;
        //double tx = llResult.getTx();


        double intakeSpeed = gamepad1.left_trigger - gamepad1.right_trigger;

        double frontLeftPower  = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower   = axial - lateral + yaw;
        double backRightPower  = axial + lateral - yaw;
        double shooterRPM=(shooter.getVelocity()*60)/-28;
        turret.setPosition(turretPosition);



        max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower  /= max;
            frontRightPower /= max;
            backLeftPower   /= max;
            backRightPower  /= max;
        }
       // if ((imu.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES) < 10) && (imu.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES) >-10)) {
           // if ((tx < 10) && (tx > -10)) {
          //      RightAngle = true;

            //}
       // }

        leftFrontDrive.setPower(frontLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        leftBackDrive.setPower(backLeftPower);
        rightBackDrive.setPower(backRightPower);
        intake.setPower(intakeSpeed);



        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("RPM", "Shooter: " + shooterRPM);
        telemetry.addData("Fire", "Ready: " + ReadyToFire);
        telemetry.addData("Motors", "left (%.2f), right (%.2f)",frontLeftPower, frontRightPower, backLeftPower, backRightPower);
        telemetry.addData("turretPosition", turretPosition);

        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        //limelight3A.updateRobotOrientation(orientation.getYaw());

        /*if (llResult != null && llResult.isValid()){
            Pose3D botPose = llResult.getBotpose_MT2();
            telemetry.addData("Tx",llResult.getTx());
            telemetry.addData("Ty",llResult.getTy());
            telemetry.addData("Ta",llResult.getTa());
            telemetry.addData("Botpose", botPose.toString());
            telemetry.addData("turretPosition", turretPosition);
            //telemetry.addData("April Tag", llResult.getTid());
        }

         */
        telemetry.update();

        if  (gamepad1.dpad_up){
            shooter.setVelocity(-28*4000/60);

        }

        if (gamepad1.dpad_down){
            shooter.setVelocity(-28*3800/60);
        }
        if (gamepad1.dpad_right){
            shooter.setVelocity(-28*3700/60);
        }
        if (gamepad1.dpad_left){
            shooter.setVelocity(-28*3650/60);
        }
        if ((shooterRPM > 3975) && (shooterRPM < 4025) && (RightAngle)){

                ReadyToFire=true;

        }
        else ReadyToFire = false;

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
            State=0;
        }
        if (gamepad1.right_bumper) {
            turretPosition+=0.01;
            runtime.reset();
            while (runtime.seconds()<0.1){

            }
        }
        if (gamepad1.left_bumper) {
            turretPosition-=0.01;
            runtime.reset();
            while (runtime.seconds()<0.1){

            }
        }

        if (gamepad1.a) {
            if (State==0){
            State=1;
            }
        }
        else{

                blocker.setPosition(0);
                transfer.setPosition(0.36);

        }

        /*if (gamepad1.b) {
            transfer.setPosition(0.2);
            runtime.reset();
            while (runtime.seconds()<0.2){

            }
            transfer.setPosition(0.35);
        }

         */

        if (gamepad1.x) {

        }

        if (gamepad1.y) {


        }
        /*
        if (llResult.getTx()< -20) {
            turretPosition-=0.01;
            RightAngle=false;
        }

        if (llResult.getTx()>-18) {
            turretPosition+=0.01;
                    RightAngle=false;
        }

         */
        if (gamepad1.dpad_up && runtime.time()>120){

        }









    }
}

