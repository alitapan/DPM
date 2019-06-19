package Ballistics;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Launcher {
 
 //declare motors 
 private EV3LargeRegulatedMotor leftMotor, rightMotor;
 
 //constructor
 public Launcher(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor)
 {
  this.rightMotor = rightMotor;
  this.leftMotor = leftMotor;
 }
 
 public void run(){
  
  leftMotor.setSpeed(leftMotor.getMaxSpeed());   //set the catapult speed to maximum
  rightMotor.setSpeed(rightMotor.getMaxSpeed());
  
  rightMotor.rotateTo(-80, true);  //rotate both motors for the specified angle to throw the ball
     leftMotor.rotateTo(-80, false);
  
 }
}
