package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/*
 * YOU SHOULD TAKE A LOOK AT THE ARM SUBSYSTEM FIRST!
 * It talks about subsystems a little more indepth, as well as DoubleSupplier and Command construction
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
