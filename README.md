# 3451 Pine Tree Java Rewrite

I'm going to try to provide as much quick information as I can so that if you all decide to try to continue to move forward with this code, either this year or next, you have some extra information about how everything works. 

## Command Based
This robot project is written in a development style called Command Based. You have subsystems (essentially individual mechanisms on your robot) and commands (different actions those subsystems can take). There are some general rules about subsystems and commands in Command Based that need to be adhered to, these are in no particular order.

- Subsystems should represent discrete systems on your robot that need to act independent of other systems. The best example of this on your robot is the pivot for the head, and the rollers on the head. There are instances when you want to move the rollers and not the head, and vice versa. 
- Subsystems should always have a default command. Something that they're doing when they're not being told to do something else. 
- Subsystems can only run one Command at a time, if a subsystem is running a command and you try to start another, the command that was originally running will be cancelled, and the new command you just asked to start up will begin

## Files and Folders
The most important folder is ./src/main/java/frc/robot. This is where all of your project files are, and where you would make changes if you wanted to do something new. I'll give a brief description of each file below, and try to give more detail in the file themselves about what is going on internally to make things work. 

### Main.java
This is where your robot program "begins". This file shouldn't be touched <i>ever</i>.

### Robot.java
This is a file that is used by Main.java to start your robot program. It is <i>very unlikely</i> that you would ever need to add or modify anything here. 

### RobotContainer.java
This is the "central file" to your Command Based robot project. This is where you'll setup your subsystems, and associate joystick axis' and buttons to different commands to make your robot do work. This is also where you can set up a command to run in autonomous. This file is also where you can setup certain things to show up on the Shuffleboard, the updated dashboard I installed on your driving computer. Shuffleboard is very flexible, and could be a separate long form topic all on its own. <b>When you're finished reading this README, you should read through this file first, then take a look at the various files in the Subsystems folder</b>

### Constants.java
This is where you define the various "magic" numbers or values of your program. A "magic" number or value is simply any number or other variable that may need to be changed in the future that is either found in multiple places in your robot code, or is buried deep somewhere and could be difficult to find if you wanted to change it, or some combination of both of these. These include things like, CAN, DIO, and PWM IDs, joystick buttons, values for things like joystick axis deadbands, and other values which should remain the same throught the running life of the robot program. By having all of these "magic" numbers or values in one place, you know exactly where to look when you want to change something, without having to dig around for it or try to make sure you change it in all the places that value is used. 

### The subsystems Folder
This folder contains the definitions for all of the different subsystems on your robot. You currently have 5. 

- Drivetrain
- Arm
- HeadPivot
- HeadRollers
- LeadScrew

Each subsystem handles the mechanism there name describes independent of the other subsystems in the list. Each subsystem "exposes" methods (sometimes reffered to as behaviors) that create commands to cause that subsystem to do something. One command the drivetrain might produce is one to make the robot drive based on inputs from various joystick axis. A command for the head pivot subsystem might set the speed of the motor (or stop the motor altogether). You have flexibility in how you define your commands that are provided by your subsystem. 

A quick side note. You may be wondering at this point how you can get subsystems to work together to complete actions. This is definitely possible, and there are some powerful tools in Command Based that allow you to do this in very dynamic ways, but the thing to remember is that subsystems should only have commands that make that subsystem do something, a command for one subsystem shouldn't affect the independent behavior of another, or it could cause weird robot behavior. 

## A Final Note

I highly encourage you to check out not only the core programming content provided by the folks at WPILIB (https://docs.wpilib.org/en/stable/docs/software/what-is-wpilib.html) but also checkout the Command Based section specifically after you have some Java basics under your belts (https://docs.wpilib.org/en/stable/docs/software/commandbased/index.html). There are also plenty of resources out there from other Java teams that may also be useful. You should feel comfortable with exploring and experimenting. 