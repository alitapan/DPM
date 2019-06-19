package Lab4;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer {
 public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
 public static double ROTATION_SPEED = 30;

 private Odometer odo;
 private Navigation robot;
 private SampleProvider usSensor;
 private float[] usData;
 private LocalizationType locType;
 private double distance, angleB, angleA, errorAngle;
 public static final double WALL_DISTANCE = 30;
 public static final double NOISE = 5;
 public static float WALL_OFFSET = 40; // This is the distance away from the US sensor to latch angles. 

 
 public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType, Navigation robot) {
  this.odo = odo;
  this.usSensor = usSensor;
  this.usData = usData;
  this.locType = locType;
  this.robot = robot;
 }
 
 public void doLocalization() {
  double [] pos = new double [3];
  double angleA, angleB;
  
  if (locType == LocalizationType.FALLING_EDGE) {

   // rotate the robot until it sees no wall
   
   rotateFromWall(true);
   
   //trying to avoid the same wall twice
   
   Sound.beep();
   robot.turnTo(25);
   Sound.beep();
   
   // keep rotating until the robot sees a wall, then latch the angle
   
   rotateToWall(true);
   angleA = odo.getAng();
   Sound.beep();
   robot.turnTo(-25);
   Sound.beep();
   
   // switch direction and wait until it sees no wall
 
   rotateFromWall(false);
   
   // keep rotating until the robot sees a wall, then latch the angle
   
   rotateToWall(false);
   angleB = odo.getAng();
   // angleA is clockwise from angleB, so assume the average of the
   // angles to the right of angleB is 45 degrees past 'north'
   
   errorAngle = getAngle(angleA, angleB);
 
   // update the odometer position (example to follow:)
   Sound.beep();
   robot.turnTo(errorAngle + 42);
   Sound.beep();
   Sound.twoBeeps();
   Sound.twoBeeps();
   //robot.goForward(25);
   odo.setPosition(new double [] {0.0, 0.0, 0}, new boolean [] {true, true, true});
  } else {
   /*
    * The robot should turn until it sees the wall, then look for the
    * "rising edges:" the points where it no longer sees the wall.
    * This is very similar to the FALLING_EDGE routine, but the robot
    * will face toward the wall for most of it.
    */
   //finds wall
   
   rotateToWall(true);
   //goes to end of wall
   rotateFromWall(true);
   angleA = odo.getAng();
   
   Sound.beep();
   Sound.beep();
   //goes in the opposite direction towards a wall
   rotateToWall(false);
   
   //rotateToWall(false);
   
   angleB = odo.getAng();

   errorAngle = getAngle(angleA, angleB);
   robot.turnTo(errorAngle + 45);
   Sound.twoBeeps();
   Sound.twoBeeps();
   odo.setPosition(new double [] {0.0, 0.0, 45}, new boolean [] {true, true, true});
  }
 }
 
 private void rotateFromWall(boolean direction)
  {
  robot.rotate(direction);
  distance = getFilteredData();
  while(distance < (WALL_DISTANCE + NOISE)){
   distance = getFilteredData(); 
  }
  robot.stop();
 }
 
 
 private void rotateToWall(boolean direction){
  robot.rotate(direction);
  distance = getFilteredData();
  while(distance > (WALL_DISTANCE - NOISE)){
   distance = getFilteredData();
  }
  robot.stop();
 }
 private double getAngle(double alpha, double beta){
  //return (alpha > beta) ? (225 - (alpha + beta)/2) : (45 - (alpha + beta)/2);

   double deltaTheta;
   
   if(alpha > beta)
   {
     deltaTheta = 45 - (alpha + beta)/2;
     
   }
   else
   {
    deltaTheta = 225 - (alpha + beta)/2;
   }
    
   return deltaTheta;
  }
 
 private float getFilteredData() {
  usSensor.fetchSample(usData, 0);
  float distance = usData[0]*100;
  if (distance > WALL_OFFSET){ 
   //System.out.println(distance);
   distance = WALL_OFFSET; 
  }
    
  return distance;
 }
 

 
}
