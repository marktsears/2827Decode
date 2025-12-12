package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="stupidTest", group="Liner OpMode")
public class stupidTest extends LinearOpMode  {
    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private Servo servo;
    private boolean gyroResetRequested= false;
    private BNO055IMU imu;




    @Override
    public void runOpMode() {
        BNO055IMU.Parameters imuParams = new BNO055IMU.Parameters();
        imuParams.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imuParams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imuParams.calibrationDataFile = "BNO055IMUCalibration.json"; // Set this to your calibration file
        imuParams.loggingEnabled = true;
        imuParams.loggingTag = "IMU";
        imuParams.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(imuParams);

        frontLeftDrive = hardwareMap.get(DcMotor.class, "LF");
        backLeftDrive = hardwareMap.get(DcMotor.class, "LR");
        frontRightDrive = hardwareMap.get(DcMotor.class, "RF");
        backRightDrive = hardwareMap.get(DcMotor.class, "RR");

        servo=hardwareMap.get(Servo.class, "servo");
        servo.setPosition(0.0);





        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);






        waitForStart();

        while (opModeIsActive()) {
           //double max;
           //double axial = -gamepad1.left_stick_y;
           //double lateral = gamepad1.left_stick_x;
           //double yaw = gamepad1.right_stick_x;

           //double frontLeftPower = axial + lateral + yaw;
           //double frontRightPower = axial - lateral - yaw;
           //double backLeftPower = axial - lateral + yaw;
           //double backRightPower =axial + lateral - yaw;




            // Get joystick inputs from the gamepad
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double rotate = gamepad1.right_stick_x;
            // reset gyro button
            if (gamepad1.a) {
                gyroResetRequested = true;
            }

            // Perform gyro reset if requestede
            if (gyroResetRequested) {
                resetGyro();
                gyroResetRequested = false; // Reset the request flag
            }

            // Get the robot's heading from the gyro sensor
            double heading = getHeading();
            // Calculate the joystick inputs in the field-oriented frame of reference
            double fieldDrive = drive * Math.cos(Math.toRadians(heading)) - strafe * Math.sin(Math.toRadians(heading));
            double fieldStrafe = drive * Math.sin(Math.toRadians(heading)) + strafe * Math.cos(Math.toRadians(heading));
            // Calculate motor powers for mecanum drive

            double frontLeftPower = fieldDrive + fieldStrafe + rotate;
            double frontRightPower = fieldDrive - fieldStrafe - rotate;
            double backLeftPower = fieldDrive - fieldStrafe + rotate;
            double backRightPower = fieldDrive + fieldStrafe - rotate;

            //max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            //max = Math.max(max, Math.abs(backLeftPower));
            //max = Math.max(max, Math.abs(backRightPower));
            // Ensure motor powers are within the valid range of -1 to 1
            frontLeftPower = Range.clip(frontLeftPower, -1.0, 1.0);
            frontRightPower = Range.clip(frontRightPower, -1.0, 1.0);
            backLeftPower = Range.clip(backLeftPower, -1.0, 1.0);
            backRightPower = Range.clip(backRightPower, -1.0, 1.0);


            double position = 0;

            if (gamepad1.a){
                servo.setPosition(0.2);
            }
            if (gamepad1.b){
                servo.setPosition(0.8);
            }

            //if (max > 1.0) {
            //    frontLeftPower  /= max;
            //    frontRightPower /= max;
            //    backLeftPower   /= max;
            //    backRightPower  /= max;
            //}

            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.update();


        }

    }
    private double getHeading() {
        // Get the robot's heading from the gyro sensor
        return imu.getAngularOrientation().firstAngle;
    }


    private void resetGyro() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // Optional: Load a calibration file if available
        imu.initialize(parameters);
    }

}

