package org.firstinspires.ftc.teamcode.common;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.drive.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.intake.Intake;
import org.firstinspires.ftc.teamcode.subsystems.shooter.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.vision.Vision;

import java.util.ArrayList;

/**
 * Central hub for all robot subsystems.
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * This class coordinates all subsystems and provides a clean interface
 * for OpModes to interact with the robot.
 * 
 * Usage in OpModes:
 *   Robot robot = new Robot(hardwareMap);
 *   robot.init();  // Call during init
 *   robot.periodic();  // Call every loop
 *   robot.write(telemetry);  // Call for telemetry output
 */
public class Robot {
    
    // --- Subsystems ---
    public MecanumDrive drive;
    public Intake intake;
    public Shooter shooter;
    public Vision vision;
    
    private ArrayList<Subsystem> subsystems = new ArrayList<>();
    private boolean visionEnabled = false;
    
    /**
     * Create a new Robot instance.
     * @param hardwareMap The hardware map from the OpMode
     */
    public Robot(HardwareMap hardwareMap) {
        this(hardwareMap, true);
    }
    
    /**
     * Create a new Robot instance with optional vision.
     * @param hardwareMap The hardware map from the OpMode
     * @param enableVision Whether to initialize vision (set false if Limelight not connected)
     */
    public Robot(HardwareMap hardwareMap, boolean enableVision) {
        this.visionEnabled = enableVision;
        
        try {
            // Initialize subsystems
            drive = new MecanumDrive(hardwareMap);
            intake = new Intake(hardwareMap);
            shooter = new Shooter(hardwareMap);
            
            // Register subsystems for periodic updates
            subsystems.add(drive);
            subsystems.add(intake);
            subsystems.add(shooter);
            
            // Vision is optional (may not be connected)
            if (enableVision) {
                try {
                    vision = new Vision(hardwareMap);
                    subsystems.add(vision);
                } catch (Exception e) {
                    // Vision not available, continue without it
                    vision = null;
                    visionEnabled = false;
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException(
                    "Hardware initialization failed. Check your robot configuration. Error: " + e.getMessage()
            );
        }
    }
    
    /**
     * Initialize the robot. Call this during OpMode init.
     * Starts vision if enabled.
     */
    public void init() {
        if (visionEnabled && vision != null) {
            vision.start();
        }
    }
    
    /**
     * Update all subsystems. Call this every loop.
     */
    public void periodic() {
        // Update robot orientation for vision if available
        if (visionEnabled && vision != null) {
            vision.updateRobotOrientation(drive.getHeadingDegrees());
        }
        
        // Update all subsystems
        for (Subsystem subsystem : subsystems) {
            subsystem.periodic();
        }
    }
    
    /**
     * Write telemetry data from all subsystems.
     * @param telemetry The telemetry object to write to
     */
    public void write(Telemetry telemetry) {
        for (Subsystem subsystem : subsystems) {
            subsystem.writeTelemetry(telemetry);
        }
        telemetry.update();
    }
    
    /**
     * Check if vision is enabled and available.
     */
    public boolean isVisionEnabled() {
        return visionEnabled && vision != null;
    }
    
    /**
     * Stop all subsystems.
     */
    public void stop() {
        drive.stop();
        intake.stop();
        shooter.stop();
    }
}

