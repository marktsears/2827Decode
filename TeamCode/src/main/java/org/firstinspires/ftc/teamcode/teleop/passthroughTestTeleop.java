package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class passthroughTestTeleop extends OpMode {
    private Servo turret;
    private DcMotorEx shooter;
    private DcMotor Intake;
    private Servo spindexer;
    private Servo lever;

    public boolean ReadyToFire=false;
    public double targetRPM=4350;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init(){
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret = hardwareMap.get(Servo.class, "turret");
        lever = hardwareMap.get(Servo.class, "lever");
        Intake = hardwareMap.get(DcMotor.class, "intake");
        spindexer = hardwareMap.get(Servo.class, "spindexer");
        spindexer.setPosition(0.15);
        lever.setPosition(0.1);

    }
    @Override
    public void loop(){
        double shooterRPM=(shooter.getVelocity()*60)/-28;
        telemetry.addData("ReadyToFire", ReadyToFire);
        telemetry.addData("shooterRPM", shooterRPM);
        double IntakePower=gamepad1.left_trigger-gamepad1.right_trigger;
        Intake.setPower(IntakePower);
        shooter.setVelocity(-28*targetRPM/60);
        if (gamepad1.right_bumper){
            targetRPM+=50;
            runtime.reset();
            while (runtime.seconds()<0.3){

            }

        }
        if (gamepad1.left_bumper){
            targetRPM-=50;
            runtime.reset();
            while (runtime.seconds()<0.3){

            }

        }
        if ((shooterRPM > targetRPM-25) && (shooterRPM < targetRPM+25)){

            ReadyToFire=true;

        }
        else {
            ReadyToFire = false;
        }
        if (gamepad1.a){
            if (ReadyToFire){

                runtime.reset();
                while (runtime.seconds()<0.5){
                    spindexer.setPosition(0.5);
                }
                runtime.reset();
                while (runtime.seconds()<0.5){
                    lever.setPosition(0);
                }
                runtime.reset();
                while (runtime.seconds()<0.5){
                    lever.setPosition(0.1);
                }
                runtime.reset();
                while (runtime.seconds()<0.5){
                    spindexer.setPosition(0.15);
                }

            }
        }

    }
}
