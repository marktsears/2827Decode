package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.Robot;

/**
 * Drive Test OpMode
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Tests field-centric mecanum driving.
 * 
 * CONTROLS:
 * - Left Stick: Drive/Strafe (field-centric)
 * - Right Stick X: Rotation
 * - A: Reset gyro heading
 */
@TeleOp(name = "Drive Test", group = "Test")
public class DriveTest extends LinearOpMode {
    
    private ElapsedTime runtime = new ElapsedTime();
    
    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap, false);
        robot.init();
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        waitForStart();
        runtime.reset();
        
        while (opModeIsActive()) {
            // Reset heading with A button
            if (gamepad1.a) {
                robot.drive.resetHeading();
            }
            
            // Field-centric driving
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double rotate = gamepad1.right_stick_x;
            
            robot.drive.driveFieldCentric(drive, strafe, rotate);
            
            // Update and telemetry
            robot.periodic();
            
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            robot.write(telemetry);
        }
    }
}

