package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/*
 * YOU SHOULD TAKE A LOOK AT THE ARM SUBSYSTEM FIRST!
 * It talks about subsystems a little more indepth, as well as DoubleSupplier and Command construction
 * 
 * THIS IS BY FAR THE MOST COMPLICATED SUBSYSTEM!
 * It would be best to take a look at the other subsystems first before reading this one.
 * 
 * The lead screw has the capability of damaging your robot if the motors aren't stopped
 * at the most extreme ranges of the lead screws motion. So we have two DigitalInputs
 * that are connected to limit switches to detect when the lead screw is at the top 
 * and bottom of it's runs.
 * 
 * We also make use of a DoubleSolenoid object, which is used to trigger the shifter
 * on the gearbox. You have to set this to either "Forward" or "Reverse" to change
 * the high gear/low gear state of the gearbox.
 */
public class LeadScrew extends SubsystemBase {
    /*
     * This is what is known as an enumeration. This is a special form of named constants,
     * these named constants all have to be in some way related to each other.
     * 
     * In this case ShifterState is used to to indicate whether the shifting gearbox should be
     * in high or low gear. 
     */
    public enum ShifterState {
        HIGH,
        LOW
    }

    private Talon leadScrewMotors;

    private DigitalInput upSwitch;
    private DigitalInput downSwitch;

    private DoubleSolenoid shifter;

    public LeadScrew() {
        leadScrewMotors = new Talon(Constants.kLeadScrewPWMID);

        upSwitch = new DigitalInput(Constants.kLeadScrewUpLimitDIO);
        downSwitch = new DigitalInput(Constants.kLeadScrewDownLimitDIO);

        shifter = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.kLeadScrewShifterH, Constants.kLeadScrewShifterL);
    }

    /*
     * A quick note about these methods (getUpSwitchValue and getDownSwitchValue) 
     * they aren't actually used to make your robot function persay. They are just 
     * here to provide a way to "see" the states of the switches outside of the subsystem.
     * 
     * The only places they are used is in RobotContainer to setup the Shuffleboard, so
     * we can see the states of the switches on the Shuffleboard. 
     */
    public boolean getUpSwitchValue() {
        return upSwitch.get();
    }

    public boolean getDownSwitchValue() {
        return downSwitch.get();
    }
    
    /*
     * The setSpeed method here is significantly more complicated than the setSpeeds
     * we see elsewhere. It still uses run, but has some internal logic to help
     * manage when the motor should run, and stop it from running in a particular direction
     * if certain switches are pressed. 
     * 
     * The plain english version of this is:
     * 
     * Store the current desired speed in a variable called desired
     * if the desired speed is less than 0 (i.e. we want to go down)
     *   if the down switch is pressed
     *      stop the motors
     *   otherwise, if the down switch is not pressed
     *      run the motors at the desired speed
     * otherwise, if the desired speed is greater than or equal to 0 (i.e. we want to stop or go up)
     *   if the up switch is pressed
     *      stop the motors
     *   otherwise, if the up switch is not pressed
     *      run the motors at the desired speed
     * 
     * All of this comes together to make sure that the robot can't tear itself apart by
     * running the motors when the lead screw is all the way at the top or all the way at 
     * the bottom
     */
    public Command setSpeed(DoubleSupplier speed) {
        return run(() -> {
            double desired = speed.getAsDouble();

            if(MathUtil.applyDeadband(desired, Constants.kDeadband) < 0) {
                if (downSwitch.get()) {
                    leadScrewMotors.set(0);
                } else {
                    leadScrewMotors.set(desired);
                }
            } else {
                if (upSwitch.get()) {
                    leadScrewMotors.set(0);
                } else {
                    leadScrewMotors.set(desired);
                }
            }
        });
    }

    /*
     * This method produces a command which sets the shifter state, you have to 
     * specify either a HIGH or LOW ShifterState enumeration value to set things the way
     * you want them.
     * 
     * Notice that this command is created using the runOnce() decorator NOT run().
     * runOnce() means that the logic in the lambda only happens one time before the command
     * ends. It's important to do the shifting this way because remember that a single subsystem
     * can only run one command at a time, this isn't the command you want running forever, because 
     * once the shifter cylinder is set it stays set to whatever you asked for. 
     * 
     * The plain english version of this is:
     * 
     * if state is HIGH
     *   Set the shifter solenoid to Forward
     * otherwise, if the state value is anything other than HIGH (i.e. LOW)
     *   Set the shifter solenoid to Reverse
     */
    public Command setShifter(ShifterState state) {
        return runOnce(() -> {
            if (state == ShifterState.HIGH) {
                shifter.set(Value.kForward);
            } else {
                shifter.set(Value.kReverse);
            }
        });
    }

    /*
     * Stop is stop, this is effectively the same as the stop method you see in
     * things like the Arm command
     */
    public Command stop() {
        return setSpeed(() -> 0);
    }

}
