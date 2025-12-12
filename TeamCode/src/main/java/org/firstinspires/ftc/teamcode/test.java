package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import java.util.List;
import java.lang.Math;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
// import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

    /*
     * This file contains an example of an iterative (Non-Linear) "OpMode".
     * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
     * The names of OpModes appear on the menu of the FTC Driver Station.
     * When a selection is made from the menu, the corresponding OpMode
     * class is instantiated on the Robot Controller and executed.
     *
     * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
     * It includes all the skeletal structure that all iterative OpModes contain.
     *
     * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
     * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
     */

    @TeleOp(name="test", group="Iterative OpMode")

    public class test extends OpMode {
        // Declare OpMode members.
        private ElapsedTime runtime = new ElapsedTime();
        private DcMotor leftFrontDrive;
        private DcMotor rightFrontDrive;
        private DcMotor rightBackDrive;
        private DcMotor leftBackDrive;
        Limelight3A limelight3A;
        private DcMotor intake;
        private DcMotor outake;
        private Servo turret;
        private Servo hood;
        private double Distance;
        private IMU imu;


        /*
         * Code to run ONCE when the driver hits INIT
         */
        @Override
        public void init() {
            telemetry.addData("Status", "Initialized");

            // Initialize the hardware variables. Note that the strings used here as parameters
            // to 'get' must correspond to the names assigned during the robot configuration
            // step (using the FTC Robot Controller app on the phone).
            leftFrontDrive  = hardwareMap.get(DcMotor.class, "LF");
            rightFrontDrive = hardwareMap.get(DcMotor.class, "RF");

            // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
            // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
            // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
            leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
            rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);

            // Tell the driver that initialization is complete.
            telemetry.addData("Status", "Initialized");

            leftBackDrive  = hardwareMap.get(DcMotor.class, "LR");
            rightBackDrive = hardwareMap.get(DcMotor.class, "RR");

            leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
            rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

            limelight3A= hardwareMap.get(Limelight3A.class, "limelight");

            imu = hardwareMap.get(IMU.class, "imu");


            //intake=hardwareMap.get(DcMotor.class, "intake");
            //intake.setDirection(DcMotorSimple.Direction.FORWARD);

            //outake=hardwareMap.get(DcMotor.class, "outake");
            //outake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //turret=hardwareMap.get(Servo.class, "turret");
            //hood=hardwareMap.get(Servo.class, "hood");
        }

        /*
         * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
         */
        @Override
        public void init_loop() {
        }

        /*
         * Code to run ONCE when the driver hits START
         */
        @Override
        public void start() {
            runtime.reset();
            limelight3A.pipelineSwitch(0);
            limelight3A.start();

        }

        /*
         * Code to run REPEATEDLY after the driver hits START but before they hit STOP
         */
        @Override
        public void loop() {
            if (gamepad1.start) {
                limelight3A.pipelineSwitch(0);
            } else if (gamepad1.b) {
                limelight3A.pipelineSwitch(1);
            } else if (gamepad1.x) {
                limelight3A.pipelineSwitch(2);
            }
            // Setup a variable for each drive wheel to save power level for telemetry


            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.


            // Tank Mode uses one stick to control each wheel.
            // - This requires no math, but it is hard to drive forward slowly and keep straight.
            // leftPower  = -gamepad1.left_stick_y ;
            // rightPower = -gamepad1.right_stick_y ;
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }
            //double takePower= gamepad1.right_trigger-gamepad1.left_trigger;


            //boolean outakePower=gamepad1.right_bumper;

            //if(outakePower){
                //outake.setPower(1.0);
            //}
            //else{
                //outake.setPower(0.0);
            //}



            leftFrontDrive.setPower(frontLeftPower);
            rightFrontDrive.setPower(frontRightPower);
            leftBackDrive.setPower(backLeftPower);
            rightBackDrive.setPower(backRightPower);

            //intake.setPower(takePower);




            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "left (%.2f), right (%.2f)",frontLeftPower, frontRightPower, backLeftPower, backRightPower);

            LLResult llResult = limelight3A.getLatestResult();
            if (llResult != null) {
                telemetry.addData("Pipeline Num", llResult.getPipelineIndex());
                if (llResult.isValid()) {
                    telemetry.addData("Tx", llResult.getTx());
                    telemetry.addData("Ty", llResult.getTy());
                    telemetry.addData("Ta", llResult.getTa());
                    telemetry.addData("botpose",llResult.getBotpose());
                }
            }

            double Ta=llResult.getTa();
            double Tx= llResult.getTx();
            double Ty= llResult.getTy();
            //double v=;
            telemetry.update();






        }}


