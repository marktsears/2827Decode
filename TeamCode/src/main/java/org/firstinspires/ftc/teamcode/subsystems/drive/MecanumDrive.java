package org.firstinspires.ftc.teamcode.subsystems.drive;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.common.Subsystem;

/**
 * Mecanum drivetrain subsystem.
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Provides field-centric and robot-centric mecanum drive control.
 */
public class MecanumDrive extends Subsystem {
    
    private final DcMotor leftFront;
    private final DcMotor rightFront;
    private final DcMotor leftBack;
    private final DcMotor rightBack;
    private final IMU imu;
    
    // State
    private double frontLeftPower = 0;
    private double frontRightPower = 0;
    private double backLeftPower = 0;
    private double backRightPower = 0;
    private double heading = 0;
    
    public MecanumDrive(HardwareMap hardwareMap) {
        // Initialize motors
        leftFront = hardwareMap.get(DcMotor.class, DriveConstants.LEFT_FRONT_MOTOR);
        rightFront = hardwareMap.get(DcMotor.class, DriveConstants.RIGHT_FRONT_MOTOR);
        leftBack = hardwareMap.get(DcMotor.class, DriveConstants.LEFT_BACK_MOTOR);
        rightBack = hardwareMap.get(DcMotor.class, DriveConstants.RIGHT_BACK_MOTOR);
        
        // Initialize IMU
        imu = hardwareMap.get(IMU.class, DriveConstants.IMU);
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        );
        imu.initialize(new IMU.Parameters(orientation));
    }
    
    /**
     * Drive with robot-centric controls.
     * @param axial Forward/backward power (-1 to 1)
     * @param lateral Left/right strafe power (-1 to 1)
     * @param yaw Rotation power (-1 to 1)
     */
    public void driveRobotCentric(double axial, double lateral, double yaw) {
        // Calculate motor powers for mecanum drive
        frontLeftPower = axial + lateral + yaw;
        frontRightPower = axial - lateral - yaw;
        backLeftPower = axial - lateral + yaw;
        backRightPower = axial + lateral - yaw;
        
        // Normalize powers if any exceeds 1.0
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));
        
        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }
        
        // Set motor powers
        leftFront.setPower(frontLeftPower);
        rightFront.setPower(frontRightPower);
        leftBack.setPower(backLeftPower);
        rightBack.setPower(backRightPower);
    }
    
    /**
     * Drive with field-centric controls.
     * @param axial Forward/backward power (-1 to 1)
     * @param lateral Left/right strafe power (-1 to 1)
     * @param yaw Rotation power (-1 to 1)
     */
    public void driveFieldCentric(double axial, double lateral, double yaw) {
        // Rotate the movement direction by the robot's heading
        double botHeading = getHeading();
        double rotX = lateral * Math.cos(-botHeading) - axial * Math.sin(-botHeading);
        double rotY = lateral * Math.sin(-botHeading) + axial * Math.cos(-botHeading);
        
        driveRobotCentric(rotY, rotX, yaw);
    }
    
    /**
     * Stop all drive motors.
     */
    public void stop() {
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);
        frontLeftPower = 0;
        frontRightPower = 0;
        backLeftPower = 0;
        backRightPower = 0;
    }
    
    /**
     * Get the robot's heading in radians.
     * @return Heading in radians
     */
    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }
    
    /**
     * Get the robot's heading in degrees.
     * @return Heading in degrees
     */
    public double getHeadingDegrees() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
    
    /**
     * Get the robot's orientation angles.
     * @return YawPitchRollAngles object
     */
    public YawPitchRollAngles getOrientation() {
        return imu.getRobotYawPitchRollAngles();
    }
    
    /**
     * Reset the IMU heading to zero.
     */
    public void resetHeading() {
        imu.resetYaw();
    }
    
    @Override
    public void periodic() {
        // Read heading for telemetry (motors are commanded directly in drive methods)
        heading = getHeadingDegrees();
    }
    
    @Override
    public void writeTelemetry(Telemetry telemetry) {
        telemetry.addData("Drive", "FL:%.2f FR:%.2f BL:%.2f BR:%.2f",
                frontLeftPower, frontRightPower, backLeftPower, backRightPower);
        telemetry.addData("Heading", "%.1f°", heading);
    }
}

