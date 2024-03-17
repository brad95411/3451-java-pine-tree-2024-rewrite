// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.HeadPivot;
import frc.robot.subsystems.HeadRollers;
import frc.robot.subsystems.LeadScrew;
import frc.robot.subsystems.LeadScrew.ShifterState;

public class RobotContainer {
  private Drivetrain drivetrain;
  private LeadScrew leadScrew;
  private Arm arm;
  private HeadPivot headPivot;
  private HeadRollers headRollers;

  private Joystick driver;
  private Joystick secondary;

  private Compressor compressor;

  private UsbCamera camera;

  public RobotContainer() {
    drivetrain = new Drivetrain();
    leadScrew = new LeadScrew();
    arm = new Arm();
    headPivot = new HeadPivot();
    headRollers = new HeadRollers();

    driver = new Joystick(0);
    secondary = new Joystick(1);

    compressor = new Compressor(PneumaticsModuleType.CTREPCM);
    compressor.enableDigital();

    camera = CameraServer.startAutomaticCapture();

    configureBindings();
    configureShuffleboard();
  }

  private void configureBindings() {
    drivetrain.setDefaultCommand(
      drivetrain.teleopCommand(
        () -> -driver.getY(), 
        () -> -driver.getX(), 
        () -> .5 * driver.getTwist())
    );

    leadScrew.setDefaultCommand(leadScrew.stop());

    arm.setDefaultCommand(
      arm.setSpeed(() -> {
        return -.75 * MathUtil.applyDeadband(secondary.getRawAxis(Constants.kArmUpDownAxis), Constants.kDeadband);
      })
    );

    headPivot.setDefaultCommand(
      headPivot.setSpeed(() -> {
        return .6 * MathUtil.applyDeadband(secondary.getRawAxis(Constants.kHeadUpDownAxis), Constants.kDeadband);
      })
    );

    headRollers.setDefaultCommand(headRollers.stop());

    new Trigger(() -> secondary.getRawButton(Constants.kLeadScrewUpButton)).whileTrue(
      leadScrew.setSpeed(() -> 1)
    );

    new Trigger(() -> secondary.getRawButton(Constants.kLeadScrewDownButton)).whileTrue(
      leadScrew.setSpeed(() -> -1)
    );

    new Trigger(() -> secondary.getRawButtonPressed(Constants.kShifterHighButton)).onTrue(
      leadScrew.setShifter(ShifterState.HIGH)
    );

    new Trigger(() -> secondary.getRawButtonPressed(Constants.kShifterLowButton)).onTrue(
      leadScrew.setShifter(ShifterState.LOW)
    );

    new Trigger(() -> secondary.getRawButton(Constants.kHeadRollersInButton)).whileTrue(
      headRollers.setSpeed(() -> .7)
    );

    new Trigger(() -> secondary.getRawButton(Constants.kHeadRollersOutButton)).whileTrue(
      headRollers.setSpeed(() -> .4)
    );
  }

  public void configureShuffleboard() {
    ShuffleboardTab cameraTab = Shuffleboard.getTab("Camera");
    cameraTab.add("Camera", camera)
      .withPosition(0, 0)
      .withSize(4, 3)
      .withWidget(BuiltInWidgets.kCameraStream);

    Shuffleboard.selectTab("Camera");

    ShuffleboardTab tab = Shuffleboard.getTab("Debug");
    tab.addBoolean("Up Switch State", leadScrew::getUpSwitchValue)
      .withPosition(0, 0)
      .withSize(2, 1)
      .withWidget(BuiltInWidgets.kBooleanBox);

    tab.addBoolean("Down Switch State", leadScrew::getDownSwitchValue)
      .withPosition(0, 1)
      .withSize(2, 1)
      .withWidget(BuiltInWidgets.kBooleanBox);
  }

  public Command getAutonomousCommand() {
    return drivetrain.teleopCommand(
      () -> .5, 
      () -> 0, 
      () -> 0
    ).withTimeout(3.5);
  }
}
