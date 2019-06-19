package Lab4;

//import Lab42.Navigation;
//import Lab4.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
 private Odometer odo;
 private SampleProvider colorSensor;
 private float[] colorData; 
 private int LIGHT_THRESHOLD = 150;
 private int SENSOR_LENGTH = 12;
 private EV3ColorSensor lightSensor;
 private Navigation robot;
 private EV3ColorSensor cs;
 private final static double LIGHT_INTENSITY_THRESHOLD = 0.28; 
 private final static long CORRECTION_PERIOD = 100; 
 private final static double SENSOR_DISTANCE = 13.0;
 private final static int ROTATION_SPEEDS= 75; 
 //US_SENSOR_OFFSET is the distance from the ultrasonic sensor to the centre of rotation to account for when 
 //moving in a straight line.
 public static double US_SENSOR_OFFSET = 3.7, DISTANCE_COLORSENSOR_TO_ROTATION_CENTER= 12.0;
 public final double OFFSET_ANGLE = 117.5; // This is an angle to make up the error for every line reading (4) from rotation speed (90+(6.8*4));

 //public LightLocalizer(Odometer odo, SampleProvider cs, float[] colorData) {
  public LightLocalizer(Odometer odo, EV3ColorSensor cs, Navigation robot) {
   this.odo = odo;
   this.cs = cs;
   //this.colorData = colorData;
   this.robot = robot;
   //this.robot = robot;
  }
  
  public void doLocalization() {
   
   // drive to location listed in tutorial
   // start rotating and clock all 4 gridlines
   // do trig to compute (0,0) and 0 degrees
   // when done travel to (0,0) and turn to 0 degrees
   
   float [] colorData = new float[1];
   double [] detectedThetas = new double[4];// 4 thetas to calculate from the lines. 
   double [] finalPosition = new double [3]; 
   int lineCounter = 0;// Counts the number of grid lines detected by the color sensor.
   int arraysAccessor = 0; // this is a constant that I will use inside my while loop for accessing arrays. 
    
   boolean detectedRightLine = false; 
   while (!detectedRightLine){
    this.cs.getRedMode().fetchSample(colorData, 0);
    double intensity = colorData[0]; 
    robot.setSpeeds(ROTATION_SPEEDS,ROTATION_SPEEDS);
    if (intensity< LIGHT_INTENSITY_THRESHOLD){
     robot.setSpeeds(0, 0);
     Sound.beep();
     break;  
    } 
   }
   try {
    Thread.sleep(2000);
   }
   catch (InterruptedException e) {
          
       }
         Sound.twoBeeps();
   robot.turnTo(180.0);
   robot.setSpeeds(0,0);
   robot.goForward(DISTANCE_COLORSENSOR_TO_ROTATION_CENTER+ US_SENSOR_OFFSET );
   robot.turnTo(90.0);
   robot.setSpeeds(0,0);
   robot.setSpeeds(50,50);
   try {
    Thread.sleep(3000);
   }
   catch (InterruptedException e) {
          
       }
   while (!detectedRightLine){
    this.cs.getRedMode().fetchSample(colorData, 0);  //lightSensor
    double intensity = colorData[0]; 
    if (intensity< LIGHT_INTENSITY_THRESHOLD){
     robot.setSpeeds(0, 0);
     Sound.beep();
     break;  
    } 
   }
   Sound.twoBeeps();
   robot.turnTo(270);
   robot.setSpeeds(0,0);
   try {
    Thread.sleep(2000);
   }
   catch (InterruptedException e) {
          
       }
   robot.goForward(10);
   robot.setSpeeds(0,0);
   robot.turnTo(0.0);
   try {
    Thread.sleep(2000);
   }
   catch (InterruptedException e) {
          
       }
   robot.goForward(US_SENSOR_OFFSET+0.5);
   
   // move Backward so the tip of the robot is on the line approximately. 
   //navigator.move
   robot.setSpeeds(-40, 40);
   try {
    Thread.sleep(8000);
   }
   catch (InterruptedException e) {
          
       }
   
        //A loop to make every line reading and store the thetas in 
        //the detectedThetas. Really the part where Light localizer does it's magic.
        
   while (lineCounter != 4){
    this.cs.getRedMode().fetchSample(colorData, 0);
    double intensity = colorData[0]; 
    if (intensity< LIGHT_INTENSITY_THRESHOLD){
     odo.getPosition(finalPosition);
     detectedThetas[arraysAccessor] = finalPosition[2];
     lineCounter++; 
     arraysAccessor++;
     Sound.beep();
     if (lineCounter !=4){
     try {
      Thread.sleep(2000);
     }
     catch (InterruptedException e) {
            
         }
     }
    }
    
   }
   robot.setSpeeds(0, 0);
   Sound.beep();
  
    // This is the calculations for finding the the new orientation and moving to 0,0 and orient 0. 
   
   int thetaX = (int)(detectedThetas[0]-detectedThetas[2]);
   int thetaY = (int)(detectedThetas[1]-detectedThetas[3]);// detectedThetas[0]
   double deltaTheta =  (int)( 180- detectedThetas[0] + thetaY/2);
   double xCoordinate =(int) (-SENSOR_DISTANCE*Math.cos((double)thetaY/2)/10); 
   double YCoordinate = (int)(-SENSOR_DISTANCE*Math.cos((double)thetaX/2)/10); 
   double actualTheta = Odometer.fixDegAngle((int)finalPosition[2]-(int)deltaTheta)+OFFSET_ANGLE;
   // setting it to the coordincates calculated. 
   odo.setPosition(new double [] {xCoordinate, YCoordinate, actualTheta }, new boolean [] {true, true, true});
   robot.travelTo(0,0);
   robot.turnTo(0);
     
    }
    
 

 }
