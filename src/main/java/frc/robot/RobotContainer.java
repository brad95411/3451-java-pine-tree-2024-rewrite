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

  /*
   * configureBindings is a method that is commonly used to setup associations between the commands provided
   * by your subsystems and the various joystick buttons and axis' that make up your teloperated control behaviors.
   * This is also where you should be setting up your default commands. 
   */
  private void configureBindings() {
    /*
     * This call to setDefaultCommand configures what the drivetrain does by default. In this case, it uses the command
     * provided the by the teleopCommand method from the Drivetrain subsystem. 
     * 
     * You'll notice that there are three things provided to the teleop command. Each of these are part of how your robot is
     * controlled when driving.
     * 
     * These three things are constructed using something called lambda expressions. That's the "() ->" followed by some extra
     * text. Lambda's are somewhat of a complicated topic, but in the vast majority of cases when working with command based, a lamdba
     * will be used to return something. 
     * 
     * For the first lambda, we return the y axis on your driver joystick (the forward and back axis). We also invert it 
     * using the negative sign to make sure driver controls work the right way (forward is forward and backward is backward)
     * 
     * For the second lambda, we return the x axis on your driver joystick (the left and right axis). We also invert this
     * for a similar reason to the first lambda. (left is left and right is right)
     * 
     * For the third and final lambda, we return the twist on your driver joystick (the rotate left and right axis). There
     * was no need to invert this, the behavior of the robot was already correct for this axis, however, we multiply by .5
     * to cut the speed of rotation in half to make controlling the rotation of the robot easier. 
     */
    drivetrain.setDefaultCommand(
      drivetrain.teleopCommand(
        () -> -driver.getY(), 
        () -> -driver.getX(), 
        () -> .5 * driver.getTwist())
    );

    /*
     * For the lead screw subsystem, the default command is simply to not move. Down below we get into more dynamic actions
     * that can "interrupt" the stop command with other commands while certain buttons are held
     */
    leadScrew.setDefaultCommand(leadScrew.stop());

    /*
     * For the arm subsystem, the default command actually controls the movement based on a joystick axis on the secondary controller.
     * Notice that when we ask for the setSpeed command from the arm subsystem, we are using a lambda again, because this one is more
     * complicated, we needed to add some curly brackets and the word "return". But the function is the same as the lambdas we've already
     * seen, which is to return some value.
     * 
     * We're doing three transformations on the arm up and down joystick axis.
     * 
     * 1. We invert, using the negative sign
     * 2. We multiply by .75, to ensure that the speed of the arm is limited to 75% of what it's capable of
     * 3. We "deadband" the joystick input, deadbanding just ensures that, if your joysticks have some stick drift, and don't
     * sit exactly at zero when being moved, that little but of drift doesn't translate to real world robot actions.
     */
    arm.setDefaultCommand(
      arm.setSpeed(() -> {
        return -.75 * MathUtil.applyDeadband(secondary.getRawAxis(Constants.kArmUpDownAxis), Constants.kDeadband);
      })
    );

    /*
     * For the head pivot subsystem, the default command is similar to the arm subsystem. We set a speed, using a lambda,
     * and apply some transformations. This time, we multiply by .6 to ensure the head moves at a maximum of 60% of what it
     * is capable of, and we do the deadbanding thing again. 
     */
    headPivot.setDefaultCommand(
      headPivot.setSpeed(() -> {
        return .6 * MathUtil.applyDeadband(secondary.getRawAxis(Constants.kHeadUpDownAxis), Constants.kDeadband);
      })
    );

    /*
     * For the head rollers subsystem, we do something similar to the lead screw subsystem. The default being to stop the 
     * rollers from moving, and down below we get into more dynamic actions that can "interrupt" the stop command with
     * other commands while certain buttons are held.
     */
    headRollers.setDefaultCommand(headRollers.stop());


    /*
     * This section deals with various buttons and controls that can trigger other commands from your subsystems to run.
     * 
     * When setting up something you want to happen, you first create a Trigger by saying "new Trigger" and then supplying
     * that Trigger with a lambda. That lambda must return a true or false value (a boolean value). When first starting out
     * with triggers, these lambdas you create will almost certainly be based on a button. Sometimes it will just be the raw
     * state of the button (getRawButton) and other times it may be more specific, like when the button changes from false
     * (i.e. not pressed) to true (pressed) (getRawButtonPressed). 
     * 
     * Once you have a Trigger set up, you need to tell it what to do in a specific circumstance. As a first example, 
     * looking at the first two Trigger setups below, we have one Trigger that is associated with the joystick button to move
     * the lead screw up, and another that moves the lead screw down.
     * 
     * We only want to see motion while those buttons are held down. To do that, we use the "whileTrue" portion to say
     * "while the button is held down, do this". The "this" in this sense being setting the leadScrew subsystem to one speed or 
     * the other, depending on which button is pressed. 
     * 
     * Once you stop holding the button down, for either of these, that command that started when you first pushed the button 
     * down is cancelled, and the default command we set up above (which was to stop the lead screw) takes over automatically.
     * 
     * You may be asking yourself at this point "what happens if I push both buttons"? With the way the libraries work internally
     * one button will always be "pushed" before another, meaning that, whichever button was pushed last will be the one that 
     * gets to run its command. 
     * 
     * I won't go into a ton of detail on each and every trigger, but there will be some more notes here and there below
     * to fill in some gaps in the above explanation
     */
    new Trigger(() -> secondary.getRawButton(Constants.kLeadScrewUpButton)).whileTrue(
      leadScrew.setSpeed(() -> 1)
    );

    new Trigger(() -> secondary.getRawButton(Constants.kLeadScrewDownButton)).whileTrue(
      leadScrew.setSpeed(() -> -1)
    );

    /*
     * In this scenario, we're doing some things slightly different. When changing whether we're shifted low or shifted high
     * you only want the command to run the instant you push the button. You don't want to try to repeatedly change the state
     * of the shifter because all that does is occupy the subsystem with useless work. 
     * 
     * By saying getRawButtonPressed, the value that the lambda returns is only very true for the single instant of time that 
     * the button changes from false to true. We can catch that "rising edge" change using onTrue, and apply our setShifter
     * command appropriately based on whatever button our Trigger is watching. 
     * 
     * ShifterState.HIGH and ShifterState.LOW might seem a little odd, it's looks like a constant, and it technically is, 
     * but you're best to read the explanation in the LeadScrew.java file to understand why it's different. 
     */
    new Trigger(() -> secondary.getRawButtonPressed(Constants.kShifterHighButton)).onTrue(
      leadScrew.setShifter(ShifterState.HIGH)
    );

    new Trigger(() -> secondary.getRawButtonPressed(Constants.kShifterLowButton)).onTrue(
      leadScrew.setShifter(ShifterState.LOW)
    );

    new Trigger(() -> secondary.getRawButton(Constants.kHeadRollersInButton)).whileTrue(
      headRollers.setSpeed(() -> .7) // When running the rollers in, use 70% of mechanisms max speed
    );

    new Trigger(() -> secondary.getRawButton(Constants.kHeadRollersOutButton)).whileTrue(
      headRollers.setSpeed(() -> .4) // When running the rollers out, use 40% of mechanisms max speed
    );
  }

  /*
   * configureShuffleboard is a method which sets up the various things that we want to see on the Shuffleboard dashboard.
   * For the most part, what you want to see when driving is your camera feed. We have an instance variable which "stores"
   * the camera so we can interact with it and we use that here. There's also some other stuff in here that I used for debugging
   * your limit switches, while not as important, it can still be helpful to have, and I'll include them in the explanations below.
   * 
   * I won't go into a ton of detail here however, Shuffleboard and it's associated abilities is a broad topic that would require a 
   * lot more examples and explanations to give a through coverage of what it's capable of. The WPILIB documentation listed in the README
   * also has information about the Shuffleboard and how it works, and is a great place to start to get to know the Shuffleboard more. 
   */
  public void configureShuffleboard() {
    /*
     * Setting up the Camera is a three part job.
     * 
     * 1. Get a tab on the Shuffleboard to display the camera on, this is done using Shuffleboard.getTab
     * 2. Add a camera viewer to that tab, this is where we provide not only the reference to our camera, 
     * but we also inform the Shuffleboard on the position that we want to place the viewer, what size the viewer
     * should be, and what widget the Shuffleboard should use to actually create the viewer. 
     * 
     * Side note before number 3. Different types of data need different widgets, some types of data can use multiple
     * different types of widgets, but for the purposes of a camera, BuiltInWidgets.kCameraStream is basically the only
     * useful option.
     * 
     * 3. We force the Shuffleboard to select (or show) the tab that has the camera viewer on it by calling
     * Shuffleboard.selectTab. This just ensures that everytime your robot boots up for a match, the camera feed is displayed
     * immediately right out of the gate. 
     */
    ShuffleboardTab cameraTab = Shuffleboard.getTab("Camera");
    cameraTab.add("Camera", camera)
      .withPosition(0, 0)
      .withSize(4, 3)
      .withWidget(BuiltInWidgets.kCameraStream);

    Shuffleboard.selectTab("Camera");

    /*
     * This is the section that I used for debugging. It uses a similar setup to that of the camera, however, 
     * these Shuffleboard widgets, as they're called, show little lights that indicate whether the individual switches
     * of the lead screw subsystem are pressed. The light will show red if not pressed, and green if pressed (assuming 
     * the wiring and the code stay the same as they were at Pine Tree)
     * 
     * These aren't vital to robot operation, so if at some point you wanted to remove these few lines, you could safely do 
     * so. 
     */
    ShuffleboardTab debugTab = Shuffleboard.getTab("Debug");
    debugTab.addBoolean("Up Switch State", leadScrew::getUpSwitchValue)
      .withPosition(0, 0)
      .withSize(2, 1)
      .withWidget(BuiltInWidgets.kBooleanBox);

    debugTab.addBoolean("Down Switch State", leadScrew::getDownSwitchValue)
      .withPosition(0, 1)
      .withSize(2, 1)
      .withWidget(BuiltInWidgets.kBooleanBox);
  }

  /*
   * All RobotContainers have to have a getAutonomousCommand method, similar to what's shown below. 
   * There are ways to use the Shuffleboard to select between different commands to allow you to have more than 
   * one autonomous available to run at the beginning of the match, and be able to select it prior to the match starting.
   * That however, is a little more complicated, and isn't shown here.
   * 
   * What is shown is a rudimentary auto that drives the robot straight forward at half power for 3.5 seconds. 
   * This makes use of the existing teleopCommand from the Drivetrain subsystem, as well as something that is built in to 
   * all commands called withTimeout, which just says that this command should run, until the timeout is reached, and then
   * the command should be stopped, and the default command for the associated subsystem or subsystems should take over.
   * 
   * withTimeout is what is known as a "decorator" there are a lot of different "decorators" in Command Based programming that
   * allow you to combine various Commands, conditions, and timeouts to create wildly complex combinations of Commands from any number
   * of subsystems. 
   * 
   * Want to run one command after another? You can do that with decorators. Want to run two commands at the same time? Decorators can do 
   * that, want two or more commands to race each other and when one of the commands stops, all of them stop? Decorators can do that to.
   * Because of the complexity of decorators and making commands work together, I won't go much deeper. But as mentioned in other places previously
   * read the WPILIB docs, all of the information you need for in depth understanding of command decorators is in there. 
   */
  public Command getAutonomousCommand() {
    return drivetrain.teleopCommand(
      () -> .5, 
      () -> 0, 
      () -> 0
    ).withTimeout(3.5);
  }
}
