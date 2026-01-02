package org.firstinspires.ftc.teamcode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.Robot;

/**
 * Shooter Test OpMode
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Tests the shooter subsystem.
 * 
 * CONTROLS:
 * - Left/Right Trigger: Intake/Outtake
 * - Right Bumper: Increase target RPM
 * - Left Bumper: Decrease target RPM
 * - A: Fire when ready
 */
@TeleOp(name = "Shooter Test", group = "Test")
public class ShooterTest extends OpMode {
    
    private Robot robot;
    private double targetRPM = 4350;
    private ElapsedTime buttonTimer = new ElapsedTime();
    
    @Override
    public void init() {
        robot = new Robot(hardwareMap, false);
        robot.init();
        
        telemetry.addData("Status", "Initialized");
    }
    
    @Override
    public void loop() {
        // Intake control
        double intakePower = gamepad1.left_trigger - gamepad1.right_trigger;
        robot.intake.setPower(intakePower);
        
        // Spin up shooter
        robot.shooter.spinUp(targetRPM);
        
        // RPM adjustment
        if (gamepad1.right_bumper && buttonTimer.seconds() > 0.3) {
            targetRPM += 50;
            buttonTimer.reset();
        }
        if (gamepad1.left_bumper && buttonTimer.seconds() > 0.3) {
            targetRPM -= 50;
            buttonTimer.reset();
        }
        
        // Fire when ready
        if (gamepad1.a && robot.shooter.isReady()) {
            robot.shooter.fire();
        }
        
        // Update and telemetry
        robot.periodic();
        
        telemetry.addData("Target RPM", targetRPM);
        telemetry.addData("Ready to Fire", robot.shooter.isReady());
        robot.write(telemetry);
    }
}

