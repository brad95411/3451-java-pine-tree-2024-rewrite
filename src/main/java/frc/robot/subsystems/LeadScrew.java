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
 */
public class LeadScrew extends SubsystemBase {
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

    public boolean getUpSwitchValue() {
        return upSwitch.get();
    }

    public boolean getDownSwitchValue() {
        return downSwitch.get();
    }
    
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

    public Command setShifter(ShifterState state) {
        return runOnce(() -> {
            if (state == ShifterState.HIGH) {
                shifter.set(Value.kForward);
            } else {
                shifter.set(Value.kReverse);
            }
        });
    }

    public Command stop() {
        return setSpeed(() -> 0);
    }

}
