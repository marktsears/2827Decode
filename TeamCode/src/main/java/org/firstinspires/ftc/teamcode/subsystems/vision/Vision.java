package org.firstinspires.ftc.teamcode.subsystems.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.common.Subsystem;

/**
 * Vision subsystem using Limelight 3A.
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Provides target tracking and AprilTag detection.
 */
public class Vision extends Subsystem {
    
    private final Limelight3A limelight;
    private LLResult latestResult = null;
    
    // Cached values from latest result
    private boolean hasTarget = false;
    private double tx = 0; // Horizontal offset from crosshair to target
    private double ty = 0; // Vertical offset from crosshair to target
    private double ta = 0; // Target area (0% to 100% of image)
    private Pose3D botPose = null;
    
    public Vision(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, VisionConstants.LIMELIGHT);
        limelight.pipelineSwitch(VisionConstants.PIPELINE_DEFAULT);
    }
    
    /**
     * Start the Limelight camera.
     */
    public void start() {
        limelight.start();
    }
    
    /**
     * Switch to a specific pipeline.
     * @param pipeline Pipeline index
     */
    public void setPipeline(int pipeline) {
        limelight.pipelineSwitch(pipeline);
    }
    
    /**
     * Update the robot's orientation for MegaTag2.
     * @param yawDegrees Robot yaw in degrees
     */
    public void updateRobotOrientation(double yawDegrees) {
        limelight.updateRobotOrientation(yawDegrees);
    }
    
    /**
     * Check if a valid target is detected.
     */
    public boolean hasTarget() {
        return hasTarget;
    }
    
    /**
     * Get the horizontal offset to the target.
     * @return Offset in degrees (positive = target is to the right)
     */
    public double getTx() {
        return tx;
    }
    
    /**
     * Get the vertical offset to the target.
     * @return Offset in degrees (positive = target is above)
     */
    public double getTy() {
        return ty;
    }
    
    /**
     * Get the target area.
     * @return Area as percentage of image (0-100)
     */
    public double getTa() {
        return ta;
    }
    
    /**
     * Get the robot pose from MegaTag2.
     * @return Pose3D or null if not available
     */
    public Pose3D getBotPose() {
        return botPose;
    }
    
    /**
     * Get the latest LLResult for advanced processing.
     */
    public LLResult getLatestResult() {
        return latestResult;
    }
    
    @Override
    public void periodic() {
        latestResult = limelight.getLatestResult();
        
        if (latestResult != null && latestResult.isValid()) {
            hasTarget = true;
            tx = latestResult.getTx();
            ty = latestResult.getTy();
            ta = latestResult.getTa();
            botPose = latestResult.getBotpose_MT2();
        } else {
            hasTarget = false;
            tx = 0;
            ty = 0;
            ta = 0;
            botPose = null;
        }
    }
    
    @Override
    public void writeTelemetry(Telemetry telemetry) {
        if (hasTarget) {
            telemetry.addData("Vision", "Target: Tx=%.1f Ty=%.1f Ta=%.1f", tx, ty, ta);
            if (botPose != null) {
                telemetry.addData("BotPose", botPose.toString());
            }
        } else {
            telemetry.addData("Vision", "No target");
        }
    }
}

