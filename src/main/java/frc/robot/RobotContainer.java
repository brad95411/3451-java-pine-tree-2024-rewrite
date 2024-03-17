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

/*
 * RobotContainer is the file where the subsystems and the commands you create come together to actually
 * make your robot do work. Like your subsystems, there will be a fair bit of stuff in here to look at and go
 * through. I'll be adding in concepts from Java as well. 
 */
public class RobotContainer {
  /*
   * Between this comment, and the line "public RobotContainer() {" are instance varibles. 
   * Instance variables are a concept in Java related to classes and objects. You can think of classes
   * as a blueprint to create objects with different characteristics, these characteristics being instance 
   * variables. 
   * 
   * The characteristics (instance variables) you should have in RobotContainer are things like your Subsystems, Joysticks, 
   * Compressor, Cameras, certain types of Commands, among other things. 
   * 
   * Note that these variables aren't useful until we "fill them in" with a value. That happens inside the section of code
   * that starts with "public RobotContainer() {"
   */
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
    /*
     * Inside RobotContainer is where you "put together" your subsystems, commands, and Joysticks to create
     * real world actions for your robot. 
     * 
     * The most important thing to do first is to "fill in" all of your instance variables as mentioned in 
     * the comment above. It's usually best to "fill in" the subsystem instance variables first, then 
     * Joysticks/Xbox Controllers, the extra things, like Compressors and Cameras. 
     * 
     * Note how we are already see use of the Constants.java file, where we're calling on it to provide the 
     * USB index values to setup our Joysticks. 
     */
    drivetrain = new Drivetrain();
    leadScrew = new LeadScrew();
    arm = new Arm();
    headPivot = new HeadPivot();
    headRollers = new HeadRollers();

    driver = new Joystick(Constants.kDriverUSB);
    secondary = new Joystick(Constants.kSecondaryUSB);

    // A quick note on the Compressor, it's important to make sure you select the right PneumaticsModuleType
    // At the time of building this code, your robot had the CTRE Pneumatics Module. The only other option is the 
    // REV Pneumatics Hub. 
    // The "compressor.enableDigital()" ensures that the compressor turns on when air pressure gets too low. 
    compressor = new Compressor(PneumaticsModuleType.CTREPCM);
    compressor.enableDigital();

    // This starts up the camera on your robot. We store a reference to the camera in a variable named "camera"
    // for later when we set up the Shuffleboard (see section "private void configureShuffleboard() {")
    camera = CameraServer.startAutomaticCapture();

    // What these things do is actually defined below, and how the work will have more details below.
    // configureBindings() sets up the associations between your subsystems and commands and your joysticks.
    // configureShuffleboard() sets up what gets displayed the Shuffleboard, for the most part for your robot,
    // this is just used to setup the viewer for your Camera.
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
