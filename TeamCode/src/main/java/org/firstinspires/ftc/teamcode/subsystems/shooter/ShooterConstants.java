package org.firstinspires.ftc.teamcode.subsystems.shooter;

/**
 * Constants for the shooter subsystem.
 * FTC DECODE Team 2827 - Season 2025-2026
 */
public class ShooterConstants {
    // Hardware names
    public static final String SHOOTER_MOTOR = "shooter";
    public static final String TURRET_SERVO = "turret";
    public static final String HOOD_SERVO = "hood";
    public static final String BLOCKER_SERVO = "blocker";
    public static final String TRANSFER_SERVO = "transfer";
    
    // Encoder ticks per revolution for velocity calculation
    public static final double ENCODER_TICKS_PER_REV = 28.0;
    
    // Default RPM targets
    public static final double DEFAULT_TARGET_RPM = 4000.0;
    public static final double RPM_TOLERANCE = 50.0;  // Wider tolerance to avoid oscillation
    
    // Servo positions
    public static final double BLOCKER_CLOSED = 0.0;
    public static final double BLOCKER_OPEN = 0.5;
    public static final double TRANSFER_IDLE = 0.35;
    public static final double TRANSFER_FEED = 0.36;
    public static final double TURRET_CENTER = 0.175;
}

