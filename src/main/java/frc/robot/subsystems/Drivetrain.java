package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/*
 * YOU SHOULD TAKE A LOOK AT THE ARM SUBSYSTEM FIRST!
 * It talks about subsystems a little more indepth, as well as DoubleSupplier and Command construction
 * 
 * The Drivetrain subsystem has a few more pieces than say the Arm or HeadPivot subsystem.
 * But it isn't significantly more complicated. Several Talon instance variables, one for
 * each motor on the Drivetrain, and then a composite representation of the MecanumDrive as a whole.
 */
public class Drivetrain extends SubsystemBase {
    private Talon frontLeft;
    private Talon frontRight;
    private Talon rearLeft;
    private Talon rearRight;

    private MecanumDrive drive;

    /*
     * When constructing a MecanumDrive, we have to invert the motors on one side
     * or the other to make sure everything moves the right way. In this case
     * we invert the motors on the right side. 
     * 
     * Note that the order of the motors when building a MecanumDrive variable is
     * very important. If the order is wrong, the driving will be wrong. 
     */
    public Drivetrain() {
        frontLeft = new Talon(Constants.kFrontLeftPWMID);
        frontRight = new Talon(Constants.kFrontRightPWMID);
        rearLeft = new Talon(Constants.kRearLeftPWMID);
        rearRight = new Talon(Constants.kRearRightPWMID);

        frontRight.setInverted(true);
        rearRight.setInverted(true);

        drive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
    }

    /*
     * The Drivetrain subsystem only has one method that creates a command, the teleopCommand
     * is really more like a drive command. It allows you to provide lambdas to provide values
     * for forward and backward motion, left and right (strafing) motion, and rotation of the 
     * drive base. 
     * 
     * This command is based on run(), so it goes "forever" until it's interrupted by another command
     * that tries to start. 
     */
    public Command teleopCommand(DoubleSupplier forwardBack, DoubleSupplier strafe, DoubleSupplier rot) {
        return run(() -> {
            drive.driveCartesian(
                forwardBack.getAsDouble(), 
                strafe.getAsDouble(), 
                rot.getAsDouble()
            );
        });
    }
}
