package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.common.Subsystem;

/**
 * Intake subsystem.
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Controls the intake motor for collecting game elements.
 */
public class Intake extends Subsystem {
    
    public enum State {
        OFF,
        INTAKING,
        OUTTAKING,
        MANUAL  // For direct power control
    }
    
    private final DcMotor intakeMotor;
    private State currentState = State.OFF;
    private double currentPower = 0;
    private double manualPower = 0;
    
    public Intake(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, IntakeConstants.INTAKE_MOTOR);
    }
    
    /**
     * Run intake to collect game elements.
     */
    public void intake() {
        currentState = State.INTAKING;
    }
    
    /**
     * Run intake in reverse to eject game elements.
     */
    public void outtake() {
        currentState = State.OUTTAKING;
    }
    
    /**
     * Stop the intake.
     */
    public void stop() {
        currentState = State.OFF;
        manualPower = 0;
    }
    
    /**
     * Set intake power directly (e.g., from trigger input).
     * Uses MANUAL state to allow variable power control.
     * @param power Power from -1 to 1
     */
    public void setPower(double power) {
        currentState = State.MANUAL;
        manualPower = power;
    }
    
    /**
     * Get the current intake state.
     */
    public State getState() {
        return currentState;
    }
    
    @Override
    public void periodic() {
        switch (currentState) {
            case OFF:
                currentPower = 0;
                break;
            case INTAKING:
                currentPower = IntakeConstants.INTAKE_POWER;
                break;
            case OUTTAKING:
                currentPower = IntakeConstants.OUTTAKE_POWER;
                break;
            case MANUAL:
                currentPower = manualPower;
                break;
        }
        intakeMotor.setPower(currentPower);
    }
    
    @Override
    public void writeTelemetry(Telemetry telemetry) {
        telemetry.addData("Intake", "%s (%.2f)", currentState, currentPower);
    }
}

