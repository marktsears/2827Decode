package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.Robot;

/**
 * Red Side Autonomous
 * FTC DECODE Team 2827 - Season 2025-2026
 * 
 * Autonomous routine that shoots preloaded game elements.
 */
@Autonomous(name = "Red Side Auto", group = "Competition")
public class RedSideAuto extends OpMode {
    
    private enum AutoState {
        SPIN_UP,
        FIRE_1,
        FIRE_2,
        FIRE_3,
        DONE
    }
    
    private Robot robot;
    private ElapsedTime runtime = new ElapsedTime();
    private AutoState state = AutoState.SPIN_UP;
    
    @Override
    public void init() {
        robot = new Robot(hardwareMap, true);
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
        // Start spinning up the shooter
        robot.shooter.spinUp(3800);
    }
    
    @Override
    public void loop() {
        // State machine
        switch (state) {
            case SPIN_UP:
                if (robot.shooter.isReady()) {
                    // Start first shot
                    robot.shooter.fire();
                    state = AutoState.FIRE_1;
                    runtime.reset();
                }
                break;
                
            case FIRE_1:
                // Wait for shot to complete (shooter returns to READY state)
                if (robot.shooter.isReady() && runtime.seconds() > 0.5) {
                    robot.shooter.fire();
                    state = AutoState.FIRE_2;
                    runtime.reset();
                }
                break;
                
            case FIRE_2:
                // Wait for shot to complete
                if (robot.shooter.isReady() && runtime.seconds() > 0.5) {
                    robot.shooter.fire();
                    state = AutoState.FIRE_3;
                    runtime.reset();
                }
                break;
                
            case FIRE_3:
                // Wait for shot to complete
                if (robot.shooter.isReady() && runtime.seconds() > 0.5) {
                    state = AutoState.DONE;
                }
                break;
                
            case DONE:
                robot.stop();
                break;
        }
        
        // Update all subsystems
        robot.periodic();
        
        // Telemetry
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("State", state);
        robot.write(telemetry);
    }
}

