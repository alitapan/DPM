package Lab3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
 // robot position
 private double x, y, theta;

 // odometer update period, in ms
 private static final long ODOMETER_PERIOD = 10;

 // lock object for mutual exclusion
 public Object lock;
 
 //self defined variables
 private EV3LargeRegulatedMotor leftMotor, rightMotor;
 public static int lastTachoL;    // tacho L at last sample
 public static int lastTachoR;    // tacho R at last sample
 public static int nowTachoL;     // current tacho L
 public static int nowTachoR;     // current tacho R
 public double WB;                // wheel base (cm)
 public double leftWheelRadius;   // wheel radius (cm)
 public double rightWheelRadius;
 

 
 // constructor
 public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftWheelRadius, double rightWheelRadius, double WB) 
 {
  this.leftMotor = leftMotor;
  this.rightMotor = rightMotor;
  this.WB = WB;
  this.leftWheelRadius = leftWheelRadius;
  this.rightWheelRadius = rightWheelRadius;
  x = 0.0;
  y = 0.0;
  theta = 0.0;
  lock = new Object();
 }

 // run method (required for Thread)
 public void run() {
  long updateStart, updateEnd;

  while (true) {
   updateStart = System.currentTimeMillis();
   double distL, distR, deltaD, deltaT, dx, dy = 0;
   
   synchronized (lock) {
    nowTachoL = leftMotor.getTachoCount(); // get tacho counts
    nowTachoR = rightMotor.getTachoCount();
    distL = Math.PI*leftWheelRadius*(nowTachoL-lastTachoL)/180; // compute wheel
    distR = Math.PI*rightWheelRadius*(nowTachoR-lastTachoR)/180; // displacements
    lastTachoL = nowTachoL; // save tacho counts for next iteration
    lastTachoR = nowTachoR;
    deltaD = 0.5*(distL+distR); // compute vehicle displacement
    deltaT = (distL - distR)/WB; // compute change in heading, angles are increasing clockwise
    
    theta = (theta + deltaT) % (2*Math.PI);  // update heading, theta is in radius
    
    dx = deltaD * Math.sin(theta); // compute X component of displacement
    dy = deltaD * Math.cos(theta); // compute Y component of displacement
    x = x + dx;  // update estimates of X and Y position
    y = y + dy;
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
