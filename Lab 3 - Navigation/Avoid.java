package Lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

//import lejos.nxt.UltrasonicSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Avoid extends Thread{
 
 //declaring variables
 public static int MIN_DISTANCE = 10;
 double []currentPosition;
 private EV3LargeRegulatedMotor leftMotor, rightMotor;
 public double WB;                    // wheel base (cm)
 public double leftWheelRadius;       // wheel radius (cm)
 public double rightWheelRadius;
 public double[][] path2 = { {60.0,0.0}, {0.0, 60.0} };
 private UltrasonicPoller us;
 private Odometer odometer;
    private Drive driver;
    private int distance;
 
 private static boolean enabled;
 static{enabled = true;}
 
 
 public Avoid(UltrasonicPoller us, Odometer odometer, Drive driver){
  this.us = us;
  this.driver = driver;
  this.odometer = odometer;
 }
      //us.fetchSample(usData, 0);
  
  
   //Called when the Avoid thread is started
   //When it detects an object, the robot moves to avoid the block, 
   //and resumes traveling towards the original waypoint
   
  @Override
  public void run(){
   while(true){
    //starts process if sensor detects a block
    if(us.getUsData() < MIN_DISTANCE && Avoid.enabled){   //&& Avoid.enabled
     //stops motors, just in case
     lab3.leftMotor.stop();
     lab3.rightMotor.stop();
     
     double destX, destY;
     double currX, currY;
     
     synchronized(driver){     
      //stores locations it is driving to, in case of obstacle avoidance
      destX = driver.nextX;    
      destY = driver.nextY;
      
       driver.isNavigating();
       avoidBlock();
      //}
     }
      
      
     //after it is done avoiding the block, it starts traveling to the previous destination
     //deciding wheter the robot is travelling to waypoint 1 or waypoint 2
     //if less than 25 cm from the origin, that means that the robot is still in its first path
     if(destX <25.0)
     {
     driver.travel(destX, destY);
     // 0.785398 is pi/4
     driver.turnTo(0.785398);
     driver.travel(60, 0);
     }
     else
     {
     driver.travel(destX, destY);
     }
    }
    try { Thread.sleep(10); } catch(Exception e){}
   }
  }
  
  public void avoidBlock(){
      // pi/2 = 1.5708
   driver.turnTo((1.5808));
   driver.goForward(30);
   driver.turnTo(-1.5808);
   driver.goForward(43);
   driver.turnTo(-1.5808);
   driver.goForward(25);
   driver.turnTo((1.5808));
   if(us.getUsData() < MIN_DISTANCE){
    avoidBlock();
   }
  }
   
   //Enabled block detection and avoidance
   
  public static void enable(){
   enabled = true;
   }
  
   //Disable block detection and avoidance
   
  public static void disable(){
            enabled =false;
            }
 }

