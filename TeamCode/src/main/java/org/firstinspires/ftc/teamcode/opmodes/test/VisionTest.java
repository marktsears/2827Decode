package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.Robot;

/**
 * Vision Test OpMode
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Tests the Limelight vision system.
 * 
 * CONTROLS:
 * - Start: Pipeline 0
 * - B: Pipeline 1
 * - X: Pipeline 2
 * - Left Stick: Drive
 * - Right Stick X: Rotation
 */
@TeleOp(name = "Vision Test", group = "Test")
public class VisionTest extends OpMode {
    
    private Robot robot;
    
    @Override
    public void init() {
        robot = new Robot(hardwareMap, true);
        robot.init();
        
        telemetry.addData("Status", "Initialized. Press Play.");
    }
    
    @Override
    public void start() {
        // Vision is started in robot.init()
    }
    
    @Override
    public void loop() {
        // Pipeline switching (only if vision is available)
        if (robot.isVisionEnabled()) {
            if (gamepad1.start) {
                robot.vision.setPipeline(0);
            } else if (gamepad1.b) {
                robot.vision.setPipeline(1);
            } else if (gamepad1.x) {
                robot.vision.setPipeline(2);
            }
        }
        
        // Basic driving
        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;
        
        robot.drive.driveRobotCentric(axial, lateral, yaw);
        
        // Update and telemetry
        robot.periodic();
        
        if (!robot.isVisionEnabled()) {
            telemetry.addData("WARNING", "Vision not available!");
        }
        robot.write(telemetry);
    }
}

