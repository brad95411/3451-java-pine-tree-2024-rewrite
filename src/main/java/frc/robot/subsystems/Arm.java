package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/*
 * The Arm subsystem is pretty straightforward. We have an instance variable that is 
 * the representation of the motor that drives the Arm. We have one component here, public Arm()
 * that is used to "create" the Arm subsystem in RobotContainer, along with two methods (behaviors)
 * which produce commands. These methods that produce commands are described in more detail
 */
public class Arm extends SubsystemBase {
    private Talon armMotor;

    public Arm() {
        armMotor = new Talon(Constants.kPickupArmPWMID);
    }

    /*
     * This method produces a command which controls the speed of the arm. There are several
     * things to mention here. 
     * 
     * run() is a decorator that is part of all subsystems, it takes a lambda which describes
     * what the command should ultimately be doing. Commands which are created with the run()
     * decorator run "forever" until the command is interrupted by another command trying to make
     * use of the subsystem.
     * 
     * For setSpeed, the only action the command will repeatedly perform is setting the speed of the motor.
     * The value to set the motor to is provided by the method parameter (variable) speed. Which is a DoubleSupplier.
     * 
     * DoubleSupplier is some what special in that it can provide values dynamically as opposed to statically.
     * This means that the speed element can be any lambda that returns a double (decimal) value. It can be a static
     * value, like 0, or .5, etc, or it can be from a dynamic source, like a joystick axis. 
     */
    public Command setSpeed(DoubleSupplier speed) {
        return run(() -> {
            armMotor.set(speed.getAsDouble());
        });
    }

    /*
     * This method produces a command which stops the arms motion. It actually makes use of setSpeed
     * discussed previously. We provide a lambda which provides a static value of 0, to indicate that
     * we want the command to set the motors speed to 0, or nothing. 
     */
    public Command stop() {
        return setSpeed(() -> 0);
    }
}
