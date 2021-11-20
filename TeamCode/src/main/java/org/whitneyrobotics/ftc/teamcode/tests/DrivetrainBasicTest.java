package org.whitneyrobotics.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Arrays;
import java.util.Arrays.*;

import org.whitneyrobotics.ftc.teamcode.lib.geometry.Coordinate;
import org.whitneyrobotics.ftc.teamcode.lib.geometry.Position;
import org.whitneyrobotics.ftc.teamcode.lib.util.RobotConstants;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImpl;
import org.whitneyrobotics.ftc.teamcode.subsys.WHSRobotImplDrivetrainOnly;

@TeleOp(name="Basic Drivetrain Test", group="Tests")
public class DrivetrainBasicTest extends OpMode {
    private WHSRobotImplDrivetrainOnly robot;

    private final double robotCenterWidthOffset = 6;
    private final double robotCenterLengthOffset = 6.5;

    private final int TANK_DRIVE = 0;
    private final int MECANUM_DRIVE = 1;
    private final int RTT = 2;
    private final int DTT = 3;
    private Toggler modeTog = new Toggler(4);
    private String driveMode = "Exponential Drive";

    @Override
    public void init(){
        robot = new WHSRobotImplDrivetrainOnly(hardwareMap);
        robot.robotDrivetrain.resetEncoders(); // May already run without encoders
        robot.robotDrivetrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Coordinate init = new Coordinate(-1800 + robotCenterWidthOffset,900,90);
        robot.setInitialCoordinate(init);
    }

    @Override
    public void loop(){
        robot.estimateCoordinate();
        robot.estimatePosition();
        modeTog.changeState(gamepad1.x);
        telemetry.addData("Current Mode: ", modeTog.currentState());
        if(gamepad1.y){
            stop();
        }
        switch(modeTog.currentState()){
            case TANK_DRIVE:
                if(gamepad1.left_bumper){
                    robot.robotDrivetrain.operate(-gamepad1.left_stick_y/2.54,-gamepad1.right_stick_y/2.54);
                } else {
                    robot.robotDrivetrain.operate(-gamepad1.left_stick_y,-gamepad1.right_stick_y);
                }
                break;
            case MECANUM_DRIVE:
                if(gamepad1.left_bumper){
                    robot.robotDrivetrain.operateMecanumDrive(gamepad1.left_stick_x/2.54,-gamepad1.left_stick_y/2.54,gamepad1.right_stick_x/2.54,robot.getCoordinate().getHeading());
                    driveMode = "Slow linear drive";
                } else if (gamepad1.right_bumper){
                    robot.robotDrivetrain.operateMecanumDriveScaled(gamepad1.left_stick_x/2.54,-gamepad1.left_stick_y/2.54,gamepad1.right_stick_x/2.54,robot.getCoordinate().getHeading());
                    driveMode = "Slow exponential drive";
                } else {
                    robot.robotDrivetrain.operateMecanumDriveScaled(gamepad1.left_stick_x,-gamepad1.left_stick_y,gamepad1.right_stick_x,robot.getCoordinate().getHeading());
                    driveMode = "Exponential drive";
                }
                break;
            case RTT:
                if(gamepad1.a){
                    robot.robotDrivetrain.resetEncoders();
                    robot.rotateToTarget(robot.getCoordinate().getHeading()+45,false);
                } if (robot.rotateToTargetInProgress()){
                robot.rotateToTarget(robot.getCoordinate().getHeading()+45,false);
            }
            case DTT:
                if(gamepad1.a){
                    robot.robotDrivetrain.resetEncoders();
                    Position target = new Position(robot.getCoordinate().getX()+300,robot.getCoordinate().getY()+300);
                    robot.driveToTarget(target,false);
                    if(robot.driveToTargetInProgress()){
                        robot.driveToTarget(target,false);
                    }
                }

        }
        telemetry.addLine();
        telemetry.addData("Mecanum mode",driveMode);
        telemetry.addData(" Drive to Target",robot.driveToTargetInProgress());
        telemetry.addData("Drive kP", RobotConstants.DRIVE_CONSTANTS.kP);
        telemetry.addData("Drive kD", RobotConstants.DRIVE_CONSTANTS.kD);
        telemetry.addData("Drive kI", RobotConstants.DRIVE_CONSTANTS.kI);
        telemetry.addData("Drive PID output",robot.driveController.getOutput());
        telemetry.addData("Drive derivative",robot.driveController.getDerivative());
        telemetry.addData("Drive integral",robot.driveController.getIntegral());
        telemetry.addData("Robot heading",robot.getCoordinate().getHeading());
        telemetry.addData("Rotate Error",robot.angleToTargetDebug);
        telemetry.addData("Rotate to Target",robot.rotateToTargetInProgress());
        telemetry.addData("Rotate kP", RobotConstants.ROTATE_CONSTANTS.kP);
        telemetry.addData("Rotate kD", RobotConstants.ROTATE_CONSTANTS.kD);
        telemetry.addData("Rotate kI", RobotConstants.ROTATE_CONSTANTS.kI);
        telemetry.addData("Rotate PID output",robot.rotateController.getOutput());
        telemetry.addData("Rotate derivative",robot.rotateController.getDerivative());
        telemetry.addData("Rotate integral",robot.rotateController.getIntegral());
        telemetry.addData("Encoder position in ticks", Arrays.toString(robot.robotDrivetrain.getAllEncoderPositions()));
        telemetry.addData("Encoder Deltas in ticks",Arrays.toString(robot.robotDrivetrain.getAllEncoderDelta()));
        telemetry.addData("Wheel velocities (ticks/sec)",Arrays.toString(robot.robotDrivetrain.getAllWheelVelocities()));
    }

    @Override
    public void stop() {
        robot.robotDrivetrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.robotDrivetrain.operate(0,0);
        super.stop();
    }
}
