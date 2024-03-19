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
 * The HeadPivot subsystem is identical to the Arm subsystem, with the exception 
 * of some differences in variable naming. You should look at the Arm subsystem for 
 * an explanation of how all this works. 
 */
public class HeadPivot extends SubsystemBase {
    private Talon headPivotMotor;

    public HeadPivot() {
        headPivotMotor = new Talon(Constants.kPickupAnglePWMID);
    }

    public Command setSpeed(DoubleSupplier speed) {
        return run(() -> {
            headPivotMotor.set(speed.getAsDouble());
        });
    }

    public Command stop() {
        return setSpeed(() -> 0);
    }
}
