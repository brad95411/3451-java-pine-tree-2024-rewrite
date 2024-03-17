package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Drivetrain extends SubsystemBase {
    private Talon frontLeft;
    private Talon frontRight;
    private Talon rearLeft;
    private Talon rearRight;

    private MecanumDrive drive;

    public Drivetrain() {
        frontLeft = new Talon(Constants.kFrontLeftPWMID);
        frontRight = new Talon(Constants.kFrontRightPWMID);
        rearLeft = new Talon(Constants.kRearLeftPWMID);
        rearRight = new Talon(Constants.kRearRightPWMID);

        frontRight.setInverted(true);
        rearRight.setInverted(true);

        drive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
    }

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
