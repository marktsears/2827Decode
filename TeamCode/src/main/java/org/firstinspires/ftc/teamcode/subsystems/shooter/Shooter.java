package org.firstinspires.ftc.teamcode.subsystems.shooter;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.common.Subsystem;

/**
 * Shooter subsystem.
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Controls the shooter flywheel, turret, hood, blocker, and transfer servos.
 */
public class Shooter extends Subsystem {
    
    public enum State {
        IDLE,
        SPINNING_UP,
        READY,
        FIRING
    }
    
    private final DcMotorEx shooterMotor;
    private final Servo turret;
    private final Servo hood;
    private final Servo blocker;
    private final Servo transfer;
    
    private State currentState = State.IDLE;
    private double targetRPM = 0;
    private double currentRPM = 0;
    private double turretPosition = ShooterConstants.TURRET_CENTER;
    private final ElapsedTime firingTimer = new ElapsedTime();
    private int firingStep = 0;
    
    public Shooter(HardwareMap hardwareMap) {
        // Initialize shooter motor
        shooterMotor = hardwareMap.get(DcMotorEx.class, ShooterConstants.SHOOTER_MOTOR);
        shooterMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        // Initialize servos
        turret = hardwareMap.get(Servo.class, ShooterConstants.TURRET_SERVO);
        hood = hardwareMap.get(Servo.class, ShooterConstants.HOOD_SERVO);
        blocker = hardwareMap.get(Servo.class, ShooterConstants.BLOCKER_SERVO);
        transfer = hardwareMap.get(Servo.class, ShooterConstants.TRANSFER_SERVO);
        
        // Set initial positions
        blocker.setPosition(ShooterConstants.BLOCKER_CLOSED);
        transfer.setPosition(ShooterConstants.TRANSFER_IDLE);
        turret.setPosition(turretPosition);
    }
    
    /**
     * Start spinning up the shooter to the target RPM.
     * @param rpm Target RPM
     */
    public void spinUp(double rpm) {
        targetRPM = rpm;
        currentState = State.SPINNING_UP;
    }
    
    /**
     * Spin up to the default RPM.
     */
    public void spinUp() {
        spinUp(ShooterConstants.DEFAULT_TARGET_RPM);
    }
    
    /**
     * Stop the shooter.
     */
    public void stop() {
        targetRPM = 0;
        currentState = State.IDLE;
        shooterMotor.setVelocity(0);
    }
    
    /**
     * Check if the shooter is at target RPM and ready to fire.
     */
    public boolean isReady() {
        return currentState == State.READY;
    }
    
    /**
     * Get the current shooter state.
     */
    public State getState() {
        return currentState;
    }
    
    /**
     * Get the current RPM.
     */
    public double getCurrentRPM() {
        return currentRPM;
    }
    
    /**
     * Get the target RPM.
     */
    public double getTargetRPM() {
        return targetRPM;
    }
    
    /**
     * Set the target RPM.
     */
    public void setTargetRPM(double rpm) {
        this.targetRPM = rpm;
    }
    
    /**
     * Adjust the target RPM by a delta.
     */
    public void adjustRPM(double delta) {
        targetRPM += delta;
    }
    
    // ========== TURRET CONTROL ==========
    
    /**
     * Set the turret position.
     * @param position Servo position (0 to 1)
     */
    public void setTurretPosition(double position) {
        turretPosition = Math.max(0, Math.min(1, position));
        turret.setPosition(turretPosition);
    }
    
    /**
     * Adjust the turret position by a delta.
     */
    public void adjustTurret(double delta) {
        setTurretPosition(turretPosition + delta);
    }
    
    /**
     * Center the turret.
     */
    public void centerTurret() {
        setTurretPosition(ShooterConstants.TURRET_CENTER);
    }
    
    /**
     * Get the current turret position.
     */
    public double getTurretPosition() {
        return turretPosition;
    }
    
    // ========== HOOD CONTROL ==========
    
    /**
     * Set the hood position.
     * @param position Servo position (0 to 1)
     */
    public void setHoodPosition(double position) {
        hood.setPosition(Math.max(0, Math.min(1, position)));
    }
    
    // ========== FIRING CONTROL ==========
    
    /**
     * Fire a shot. Only works if shooter is ready.
     * @return true if shot was initiated
     */
    public boolean fire() {
        if (currentState == State.READY) {
            currentState = State.FIRING;
            firingStep = 1;  // Start at step 1 (open blocker)
            firingTimer.reset();
            // Immediately open blocker
            blocker.setPosition(ShooterConstants.BLOCKER_OPEN);
            return true;
        }
        return false;
    }
    
    /**
     * Open the blocker to allow a shot.
     */
    public void openBlocker() {
        blocker.setPosition(ShooterConstants.BLOCKER_OPEN);
    }
    
    /**
     * Close the blocker.
     */
    public void closeBlocker() {
        blocker.setPosition(ShooterConstants.BLOCKER_CLOSED);
    }
    
    @Override
    public void periodic() {
        // Calculate current RPM from motor velocity
        // Velocity is in ticks/sec, convert to RPM
        double velocityTicks = shooterMotor.getVelocity();
        currentRPM = (velocityTicks * 60.0) / -ShooterConstants.ENCODER_TICKS_PER_REV;
        
        // Update turret position
        turret.setPosition(turretPosition);
        
        switch (currentState) {
            case IDLE:
                shooterMotor.setVelocity(0);
                blocker.setPosition(ShooterConstants.BLOCKER_CLOSED);
                transfer.setPosition(ShooterConstants.TRANSFER_IDLE);
                break;
                
            case SPINNING_UP:
                // Set motor velocity (negative because of motor direction)
                double targetVelocity = -ShooterConstants.ENCODER_TICKS_PER_REV * targetRPM / 60.0;
                shooterMotor.setVelocity(targetVelocity);
                
                // Check if we're at target RPM
                if (Math.abs(currentRPM - targetRPM) < ShooterConstants.RPM_TOLERANCE) {
                    currentState = State.READY;
                }
                break;
                
            case READY:
                // Keep spinning at target velocity
                double readyVelocity = -ShooterConstants.ENCODER_TICKS_PER_REV * targetRPM / 60.0;
                shooterMotor.setVelocity(readyVelocity);
                
                // Check if RPM dropped below tolerance
                if (Math.abs(currentRPM - targetRPM) > ShooterConstants.RPM_TOLERANCE) {
                    currentState = State.SPINNING_UP;
                }
                break;
                
            case FIRING:
                // Keep shooter spinning during firing
                double firingVelocity = -ShooterConstants.ENCODER_TICKS_PER_REV * targetRPM / 60.0;
                shooterMotor.setVelocity(firingVelocity);
                
                // Firing sequence state machine
                // Step 1: Wait for blocker to open, then feed
                if (firingStep == 1 && firingTimer.seconds() >= 0.3) {
                    // Feed the ball
                    transfer.setPosition(ShooterConstants.TRANSFER_FEED);
                    firingStep = 2;
                    firingTimer.reset();
                } 
                // Step 2: Wait for ball to feed, then reset
                else if (firingStep == 2 && firingTimer.seconds() >= 0.5) {
                    // Reset and go back to ready
                    blocker.setPosition(ShooterConstants.BLOCKER_CLOSED);
                    transfer.setPosition(ShooterConstants.TRANSFER_IDLE);
                    currentState = State.READY;
                    firingStep = 0;
                }
                break;
        }
    }
    
    @Override
    public void writeTelemetry(Telemetry telemetry) {
        telemetry.addData("Shooter", "%s | RPM: %.0f / %.0f", 
                currentState, currentRPM, targetRPM);
        telemetry.addData("Turret", "%.3f", turretPosition);
    }
}

