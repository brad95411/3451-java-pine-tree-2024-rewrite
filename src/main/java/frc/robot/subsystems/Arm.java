package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Arm extends SubsystemBase {
    private Talon armMotor;

    public Arm() {
        armMotor = new Talon(Constants.kPickupArmPWMID);
    }

    public Command setSpeed(DoubleSupplier speed) {
        return run(() -> {
            armMotor.set(speed.getAsDouble());
        });
    }

    public Command stop() {
        return setSpeed(() -> 0);
    }
}
