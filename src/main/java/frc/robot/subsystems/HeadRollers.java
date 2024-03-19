package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/*
 * YOU SHOULD TAKE A LOOK AT THE ARM SUBSYSTEM FIRST!
 * It talks about subsystems a little more indepth, as well as DoubleSupplier and Command construction
 * 
 * The HeadRollers subsystem is identical to the Arm subsystem, with the exception of
 * some differences in variable naming, and the actual work this subsystem does. 
 * 
 * This subsystem doesn't move an "arm", it makes wheels turn. But fundamentally the 
 * control of the subsystem is the same. 
 */
public class HeadRollers extends SubsystemBase {
    private Talon headRollerMotor;

    public HeadRollers() {
        headRollerMotor = new Talon(Constants.kPickupHeadPWMID);
    }

    public Command setSpeed(DoubleSupplier speed) {
        return run(() -> {
            headRollerMotor.set(speed.getAsDouble());
        });
    }

    public Command stop() {
        return setSpeed(() -> 0);
    }
}
