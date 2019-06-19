package ev3Odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
 // robot position
 private double x, y, theta;
 
 //declaring constants radius and width
 
 public static double WHEEL_RADIUS = 2.125;
 public static double TRACK = 16.70;
 
 //declared variables
 
 public static int lastTachoL;
 public static int lastTachoR;
 public static int nowTachoL;
 public static int nowTachoR;
 double distL, distR, deltaD, deltaT, dY, dX;

 // odometer update period, in ms
 private static final long ODOMETER_PERIOD = 25;

 // lock object for mutual exclusion
  private Object lock;
  EV3LargeRegulatedMotor leftMotor;
  EV3LargeRegulatedMotor rightMotor;

 // constructor
 public Odometer(EV3LargeRegulatedMotor leftMotor_c, EV3LargeRegulatedMotor rightMotor_c) {
  leftMotor = leftMotor_c;
  rightMotor = rightMotor_c;
  x = 0.0;
  y = 0.0;
  theta = 0.0;
  lock = new Object();
 }

 // run method (required for Thread)
 public void run() {
  long updateStart, updateEnd;
  
  leftMotor.resetTachoCount();
  rightMotor.resetTachoCount();

  while (true) {
   updateStart = System.currentTimeMillis();

   nowTachoL = leftMotor.getTachoCount();
   nowTachoR = rightMotor.getTachoCount();                   //set the variables for tacho count
   
   distL = Math.PI*WHEEL_RADIUS*(nowTachoL-lastTachoL)/180;  //calculate the distance traveled
   distR = Math.PI*WHEEL_RADIUS*(nowTachoR-lastTachoR)/180;
   
   lastTachoL = nowTachoL;                                   //set the current tacho to become the old tacho for next iteration
   lastTachoR = nowTachoR; 
   
   deltaD = 0.5*(distL+distR);                               //calculate the detlta's
   deltaT = (distL-distR)/TRACK;
   
   synchronized (lock) {
    // don't use the variables x, y, or theta anywhere but here!
    
    theta = theta + deltaT;       //set the variables and calculate the distance traveled
    dX = deltaD*Math.sin(theta);
    dY = deltaD*Math.cos(theta);
    
    if (theta > 2*Math.PI){       //if theta more than 0
     double temp_theta = theta%(2*Math.PI);
     theta =temp_theta;
    }
    else if (theta < 0){          //if theta less than 0
     double temp_theta = 2*Math.PI + theta;
     theta =temp_theta;
    }
    x = x + dX;
    y = y + dY;
   }
   

   // this ensures that the odometer only runs once every period
   updateEnd = System.currentTimeMillis();
   if (updateEnd - updateStart < ODOMETER_PERIOD) {
    try {
     Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
    } catch (InterruptedException e) {
     // there is nothing to be done here because it is not
     // expected that the odometer will be interrupted by
     // another thread
    }
   }
  }
 }

 // accessors
 public void getPosition(double[] position, boolean[] update) {
  // ensure that the values don't change while the odometer is running
  synchronized (lock) {
   if (update[0])
    position[0] = x;
   if (update[1])
    position[1] = y;
   if (update[2])
    position[2] = theta;
  }
 }

 public double getX() {
  double result;

  synchronized (lock) {
   result = x;
  }

  return result;
 }

 public double getY() {
  double result;

  synchronized (lock) {
   result = y;
  }

  return result;
 }

 public double getTheta() {
  double result;

  synchronized (lock) {
   result = theta;
  }

  return result;
 }

 // mutators
 public void setPosition(double[] position, boolean[] update) {
  // ensure that the values don't change while the odometer is running
  synchronized (lock) {
   if (update[0])
    x = position[0];
   if (update[1])
    y = position[1];
   if (update[2])
    theta = position[2];
  }
 }

 public void setX(double x) {
  synchronized (lock) {
   this.x = x;
  }
 }

 public void setY(double y) {
  synchronized (lock) {
   this.y = y;
  }
 }

 public void setTheta(double theta) {
  synchronized (lock) {
   this.theta = theta;
  }
 }
}
