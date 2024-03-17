package frc.robot;

public class Constants {
    public static final int kFrontLeftPWMID = 2;
    public static final int kFrontRightPWMID = 3;
    public static final int kRearLeftPWMID = 0;
    public static final int kRearRightPWMID = 1;
    public static final int kLeadScrewPWMID = 4;
    public static final int kPickupHeadPWMID= 5;
    public static final int kPickupArmPWMID = 6;
    public static final int kPickupAnglePWMID = 7;

    public static final int kLeadScrewUpLimitDIO = 0;
    public static final int kLeadScrewDownLimitDIO = 1;

    public static final int kLeadScrewShifterH = 0;
    public static final int kLeadScrewShifterL = 1;

    public static final double kDeadband = .05;

    public static final int kLeadScrewUpButton = 8;
    public static final int kLeadScrewDownButton = 6;
    public static final int kArmUpDownAxis = 3; //Y Axis limited to 75% power and inverted
    public static final int kHeadUpDownAxis = 1; //X Axis limited to 60$ power 
    public static final int kHeadRollersInButton = 2; //limited to 70% power
    public static final int kHeadRollersOutButton = 4; //limited to 40$ and inverted
    public static final int kShifterHighButton = 10;
    public static final int kShifterLowButton = 9;

    public static final int kDriverUSB = 0;
    public static final int kSecondaryUSB = 1;
}
