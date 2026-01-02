package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.Robot;

/**
 * Red Side TeleOp
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * CONTROLS:
 * - Left Stick: Drive (strafe)
 * - Right Stick X: Rotation
 * - Left/Right Trigger: Intake/Outtake
 * - D-Pad Up/Down/Left/Right: Different shooter speeds
 * - A: Start firing sequence
 * - Left/Right Bumper: Adjust turret position
 */
@TeleOp(name = "Red Side TeleOp", group = "Competition")
public class RedSideTeleOp extends OpMode {
    
    private Robot robot;
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime buttonTimer = new ElapsedTime();
    
    @Override
    public void init() {
        // Initialize robot without vision (commented out in original)
        robot = new Robot(hardwareMap, false);
        robot.init();
        
        telemetry.addData("Status", "Initialized");
    }
    
    @Override
    public void init_loop() {
        // Nothing needed here
    }
    
    @Override
    public void start() {
        runtime.reset();
    }
    
    @Override
    public void loop() {
        // === DRIVING ===
        double axial = -gamepad1.right_stick_x;
        double lateral = -gamepad1.left_stick_x;
        double yaw = gamepad1.left_stick_y;
        
        robot.drive.driveRobotCentric(axial, lateral, yaw);
        
        // === INTAKE ===
        double intakeSpeed = gamepad1.left_trigger - gamepad1.right_trigger;
        robot.intake.setPower(intakeSpeed);
        
        // === SHOOTER SPEED PRESETS ===
        if (gamepad1.dpad_up) {
            robot.shooter.spinUp(4000);
        }
        if (gamepad1.dpad_down) {
            robot.shooter.spinUp(3800);
        }
        if (gamepad1.dpad_right) {
            robot.shooter.spinUp(3700);
        }
        if (gamepad1.dpad_left) {
            robot.shooter.spinUp(3650);
        }
        
        // === FIRING ===
        if (gamepad1.a) {
            if (robot.shooter.isReady()) {
                robot.shooter.fire();
            }
        }
        
        // === TURRET ADJUSTMENT ===
        if (gamepad1.right_bumper && buttonTimer.seconds() > 0.1) {
            robot.shooter.adjustTurret(0.01);
            buttonTimer.reset();
        }
        if (gamepad1.left_bumper && buttonTimer.seconds() > 0.1) {
            robot.shooter.adjustTurret(-0.01);
            buttonTimer.reset();
        }
        
        // === UPDATE & TELEMETRY ===
        robot.periodic();
        
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        robot.write(telemetry);
    }
}

