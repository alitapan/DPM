package Ballistics;

//Uluc Ali Tapan 
//260556540

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Lab5 {
 
 // declare motors and assign them to ports 
 public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
 public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
 
 public static void main (String args[]){ 
  for(int i = 0; i<5 ; i++)
  {
  
  // create an object of Launcher and run it
  Launcher launcher = new Launcher (leftMotor, rightMotor);
  launcher.run();
  try {
   Thread.sleep(5000);    //sleep for 5 seconds so you can reload the ball
  } catch (InterruptedException e) {  
 }
  }
 }
}
