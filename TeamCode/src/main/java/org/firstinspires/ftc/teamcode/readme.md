# Team 2827 DECODE - TeamCode

## Project Structure

```
teamcode/
├── common/                 # Shared core classes
│   ├── Robot.java          # Central hub for all subsystems
│   └── Subsystem.java      # Base class for subsystems
│
├── subsystems/             # Encapsulated subsystem classes
│   ├── drive/
│   │   ├── DriveConstants.java
│   │   └── MecanumDrive.java
│   ├── intake/
│   │   ├── IntakeConstants.java
│   │   └── Intake.java
│   ├── shooter/
│   │   ├── ShooterConstants.java
│   │   └── Shooter.java
│   └── vision/
│       ├── VisionConstants.java
│       └── Vision.java
│
└── opmodes/                # Driver-runnable programs
    ├── teleop/
    │   └── RedSideTeleOp.java
    ├── auto/
    │   └── RedSideAuto.java
    └── test/
        ├── VisionTest.java
        ├── DriveTest.java
        └── ShooterTest.java
```

## Architecture

### Subsystem Pattern

Each subsystem encapsulates:
- Hardware initialization
- State management
- Control methods
- Telemetry output

Subsystems extend the abstract `Subsystem` class and implement:
- `periodic()` - Called every loop to update state
- `writeTelemetry()` - Output debug data

### Robot Class

The `Robot` class is the central hub that:
- Initializes all subsystems
- Coordinates updates via `periodic()`
- Provides unified telemetry via `write()`

### OpMode Usage

```java
@TeleOp(name = "Example TeleOp")
public class ExampleTeleOp extends OpMode {
    private Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        robot.init();
    }

    @Override
    public void loop() {
        // Control subsystems
        robot.drive.driveRobotCentric(axial, lateral, yaw);
        robot.intake.intake();
        
        // Update all subsystems
        robot.periodic();
        
        // Output telemetry
        robot.write(telemetry);
    }
}
```

## Hardware Configuration

These names must match your Robot Configuration on the Control Hub:

| Type   | Name      | Description              |
|--------|-----------|--------------------------|
| Motor  | FL        | Front left drive motor   |
| Motor  | FR        | Front right drive motor  |
| Motor  | BL        | Back left drive motor    |
| Motor  | BR        | Back right drive motor   |
| Motor  | intake    | Intake motor             |
| Motor  | shooter   | Shooter flywheel motor   |
| Servo  | turret    | Turret rotation servo    |
| Servo  | hood      | Hood angle servo         |
| Servo  | blocker   | Ball release servo       |
| Servo  | transfer  | Ball transfer servo      |
| IMU    | imu       | REV Hub IMU              |
| Other  | limelight | Limelight 3A camera      |
