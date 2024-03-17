// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/*
 * THIS FILE SHOULD BE LEFT ALONE
 * 
 * Main.java is where your robot program begins, there is never a reason to add or modify anything in
 * this file!
 */
public final class Main {
  private Main() {}

  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}
