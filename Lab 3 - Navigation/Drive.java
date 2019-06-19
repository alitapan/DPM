package Lab3;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
public class Drive extends Thread{
  
 //constants
 private final double[][] path;
 private final int FORWARD_SPEED = 250;
 private final int ROTATE_SPEED = 150; 
 
 //variables
 private boolean isNavigating = false;
 public double nextX, nextY;
 private Odometer odometer;
 private EV3LargeRegulatedMotor leftMotor, rightMotor;
 public double WB;              // wheel base (cm)
 public double leftWheelRadius; // wheel radius (cm)
 public double rightWheelRadius;
 public double thetar, xr, yr;
 
 //constructor
 public Drive (EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
   double leftRadius, double rightRadius, double width, Odometer odometer, double[][] path){
   
   this.path = path;
   this.odometer = odometer;
   this.leftMotor = leftMotor;
   this.rightMotor = rightMotor;   
   this.WB = width;
   this.leftWheelRadius = leftRadius;
   this.rightWheelRadius = rightRadius;   
  }
 
 //method for Thread.start()
 public void run(){
  // wait 2 seconds
  try {
    Thread.sleep(2000);
   } catch (InterruptedException e) {  
  }
   
  //for each coordinate calculate the angle need to rotate and distance need to travel
  //pass the angle and distance to the function that controls the wheels to do the corresponding movement
  for (int a=0; a<path.length; a++){   
   //get the x and y coordinate of the point that needs to go
   this.nextX = path[a][0];
   this.nextY = path[a][1];   
   //get the current position from odometer (x,y,theta)
   double []currentPosition = new double[3];
   boolean [] returnValue = new boolean[] { true, true, true };
   odometer.getPosition(currentPosition, returnValue);   
   //calculate the angle need to rotate towards the point needs to go
   double angleNeeded = angleNeedToRotate(currentPosition, nextX, nextY);
   //make the rotation
   turnTo(angleNeeded);
   //now since the direction is towards the destination, move towards the destination
   travelTo(currentPosition, nextX, nextY);     
  }
 }
 
 public void travelTo(double []currentPosition, double x, double y){
  isNavigating = true;
  //calculate the distance between starting point to ending point
  double distanceNeedToTravel = Math.sqrt((Math.pow(x-currentPosition[0], 2) + Math.pow(y-currentPosition[1], 2)));
  
  leftMotor.setSpeed(FORWARD_SPEED);
  rightMotor.setSpeed(FORWARD_SPEED);

  leftMotor.rotate(convertDistance(leftWheelRadius, distanceNeedToTravel), true);
  rightMotor.rotate(convertDistance(rightWheelRadius, distanceNeedToTravel), false);
  isNavigating = false;
 }
 
 //specific for Avoid.java, the robot travels to a given point without having the previous point
 public void travel (double x, double y){
  //gets position. Synchronized to avoid collision
   synchronized (odometer.lock) {
    thetar = odometer.getTheta() ;   // * 180 / Math.PI;
    xr = odometer.getX();
    yr = odometer.getY();
   }
   //calculates degrees to turn from 0 degrees
   double thetad =  Math.atan2(x - xr, y - yr) ;    //* 180 / Math.PI;
   //calculates actual angle to turn
   double theta =  thetad - thetar;
   //calculates magnitude to travel
   double distance  = Math.sqrt(Math.pow((y-yr), 2) + Math.pow((x-xr),2));
   //finds minimum angle to turn (ie: it's easier to turn +90 deg instead of -270)
   if(theta < -180){
    turnTo(theta + 360);
   }
   else if(theta > 180){
    turnTo(theta - 360);
   }
   else turnTo(theta);
   
   
   goForward(distance);
 }
 
 //specific for Avoid.java, robot travels in a given distance
 public void goForward(double distance){
  
  // drive forward 
  leftMotor.setSpeed(FORWARD_SPEED);
  rightMotor.setSpeed(FORWARD_SPEED);
  
  //for isNavigatingMethod
  isNavigating = true;
  
  leftMotor.rotate(convertDistance(leftWheelRadius, distance), true);
  rightMotor.rotate(convertDistance(rightWheelRadius, distance), false);
  
  isNavigating = false;
 }
 public void turnTo(double theta){
  isNavigating = true;
  leftMotor.setSpeed(ROTATE_SPEED);
  rightMotor.setSpeed(ROTATE_SPEED);
  //if theta > 0 turn clockwise theta radius
  //if theta < 0 turn counterclockwise theta radius
  int angle = convertAngle(leftWheelRadius, WB, Math.toDegrees(theta));
  leftMotor.rotate(angle, true);
  rightMotor.rotate(-angle, false);
  isNavigating = false;
 }
 private double angleNeedToRotate(double []currentPosition, double x, double y){
  double deltaX = x-currentPosition[0];
  double deltaY = y-currentPosition[1];
  double angle = 0.0;
  double deltaAngle;
  
  
  //calculating the minimum angle using if else statements 
  if (deltaX >= 0 && deltaY > 0){
   angle = Math.atan(deltaX/deltaY);
  }else if(deltaX > 0 && deltaY < 0){
   angle = Math.PI + Math.atan(deltaX/deltaY);
  }else if(deltaX < 0 && deltaY < 0){
   angle = -1*Math.PI + Math.atan(deltaX/deltaY);
  }else if(deltaX < 0 && deltaY > 0){
   angle = Math.atan(deltaX/deltaY);
  }
  else if(deltaX<0 && deltaY==0)
  {
   angle = (-(currentPosition[2]+(Math.PI/2)));
   return(angle);
  }
  else if(deltaX>=0 && deltaY==0)
  {
   angle = ((Math.PI/2)-currentPosition[2]);
   return(angle);
  }
  
  //get minimal turning angle
     deltaAngle = angle - currentPosition[2];
  if((deltaAngle < Math.PI) && (deltaAngle > -1*Math.PI)){
   //if deltaAngle > 0 means turn clockwise deltaAngle radius,
   //if deltaAngle < 0 means turn counterclockwise deltaAngle radius
   return deltaAngle;
  }
  else if (deltaAngle < -1*Math.PI){
   //turn clockwise deltaAngle + 2*Math.PI
   return (deltaAngle + 2*Math.PI); 
  }
  //else if (deltaAngle > Math.PI)
  else {
   //turn counterclockwise deltaAngle - 2*Math.PI
   return (deltaAngle - 2*Math.PI); 
  }  
 }
 
 //This method returns true if another thread has called travelTo()  or
 //turnTo()  and the method has yet to return; false otherwise.
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
