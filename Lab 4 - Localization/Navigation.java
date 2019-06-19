package Lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
 final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
 final static double DEG_ERR = 3.0, CM_ERR = 1.0;
 private final int ROTATE_SPEED = 150;
 private final int LOCALIZE_SPEED = 100;
 private final double leftWheelRadius = 2.125;
 private final double rightWheelRadius = 2.125;
 private final double WB = 16.70;
 private Odometer odometer;
 private boolean isNavigating = false;
 private EV3LargeRegulatedMotor leftMotor, rightMotor;

 public Navigation(Odometer odo) {
  this.odometer = odo;

  // Set acceleration
  Lab4.leftMotor.setAcceleration(ACCELERATION);
  Lab4.rightMotor.setAcceleration(ACCELERATION);
 }

 
  //Functions to set the motor speeds jointly
 public void setSpeeds(float lSpd, float rSpd) {
  Lab4.leftMotor.setSpeed(lSpd);
  Lab4.rightMotor.setSpeed(rSpd);
  if (lSpd < 0)
   Lab4.leftMotor.backward();
  else
   Lab4.leftMotor.forward();
  if (rSpd < 0)
   Lab4.rightMotor.backward();
  else
   Lab4.rightMotor.forward();
 }

 public void setSpeeds(int lSpd, int rSpd) {
  Lab4.leftMotor.setSpeed(lSpd);
  Lab4.rightMotor.setSpeed(rSpd);
  if (lSpd < 0)
   Lab4.leftMotor.backward();
  else
   Lab4.leftMotor.forward();
  if (rSpd < 0)
   Lab4.rightMotor.backward();
  else
   Lab4.rightMotor.forward();
 }

  //Float the two motors jointly
 
 public void setFloat() {
  Lab4.leftMotor.stop();
  Lab4.rightMotor.stop();
  Lab4.leftMotor.flt(true);
  Lab4.rightMotor.flt(true);
 }
 
 public void turnTo(double theta) {
  
  isNavigating = true;
  
  Lab4.leftMotor.setSpeed(ROTATE_SPEED);
  Lab4.rightMotor.setSpeed(ROTATE_SPEED);
  
  //if theta > 0 turn clockwise theta radius
  //if theta < 0 turn counterclockwise theta radius
  
  int angle = convertAngle(leftWheelRadius, WB, theta);
  Lab4.leftMotor.rotate(angle, true);
  Lab4.rightMotor.rotate(-angle, false);
  
  isNavigating = false;
 }
 
 //method for rotating in terms of boolean expression
 public void rotate (boolean forward){
  
  Lab4.leftMotor.setSpeed(LOCALIZE_SPEED);
  Lab4.rightMotor.setSpeed(LOCALIZE_SPEED);
  
  if (forward){
   
   Lab4.leftMotor.forward();
   Lab4.rightMotor.backward();
   
  } else {
   
   Lab4.leftMotor.backward();
   Lab4.rightMotor.forward();
   
  }
 }

 
  //TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
  //constantly updating it's heading
 
 public void travelTo(double x, double y) {
  double minAng;
  while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
   minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
   if (minAng < 0)
    minAng += 360.0;
   this.turnTo(minAng, false);
   this.setSpeeds(FAST, FAST);
  }
  this.setSpeeds(0, 0);
 }

 
  //TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
  //motors when the turn is completed
  
 public void turnTo(double angle, boolean stop) {

  double error = angle - this.odometer.getAng();

  while (Math.abs(error) > DEG_ERR) {

   error = angle - this.odometer.getAng();

   if (error < -180.0) {
    this.setSpeeds(-SLOW, SLOW);
   } else if (error < 0.0) {
    this.setSpeeds(SLOW, -SLOW);
   } else if (error > 180.0) {
    this.setSpeeds(SLOW, -SLOW);
   } else {
    this.setSpeeds(-SLOW, SLOW);
   }
  }

  if (stop) {
   this.setSpeeds(0, 0);
  }
 }
 
 
  //Go foward a set distance in cm
 
 public void goForward(double distance) {
  this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);

 }
 
 public void stop(){
  
  Lab4.leftMotor.setSpeed(0);
  Lab4.rightMotor.setSpeed(0);
  
 }
  
 
 public boolean isNavigating (){
  return isNavigating;
 }
 
 private static int convertDistance(double radius, double distance) {
  return (int) ((180.0 * distance) / (Math.PI * radius));
 }
 
 private static int convertAngle(double radius, double width, double angle) {
  return convertDistance(radius, Math.PI * width * angle / 360.0);
 } 
 
}
