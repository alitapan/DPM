package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class OdometryCorrection extends Thread {
 
 // declare and initialize variables and constants
 private static final long CORRECTION_PERIOD = 10;
 private static final double maxLight =0.45;
 double previous_x = 0;
 double previous_y = 0;
 double x = 0;
 double y = 0;
 
 //declare light sensor
 private Odometer odometer;
 Port usPort = LocalEV3.get().getPort("S1");
 EV3ColorSensor lightSensor = new EV3ColorSensor(usPort);

 // constructor
 public OdometryCorrection(Odometer odometer) {
  this.odometer = odometer;
 }

 // run method (required for Thread)
 public void run() {
  long correctionStart, correctionEnd;
  
  while (true) {
   
   //declartion of variables and getting of data
   
   correctionStart = System.currentTimeMillis(); 
   x = odometer.getX();
   y = odometer.getY();
   SensorMode mode = lightSensor.getRedMode();
   float[] sample = new float[mode.sampleSize()];
   mode.fetchSample(sample, 0);
   float intensity = sample[0];

   if (intensity < maxLight){ //check for black line and not intersection
    Sound.beep(); //beep to confirm
    
    //iterate through possible line values, 10 and 40 we use 10 and 40 instead of 45/15 to account for sensor distance from center
    
    for (int i = 1; i<5; i= i+3){ 
     
     if (Math.abs(y-previous_y) < 2){ //check which direction the robot is traveling x or y
      
      if (Math.abs(x-i*10) <5 ){ //check if the robot is close to the line value
       x = i*10; //if within bounds set to line 
       odometer.setX(x);
      }
     }
     else if (Math.abs(x-previous_x) < 5){//same as previous but for traveling in y direction
      
      if (Math.abs(y-i*10) <5){
       y = i*10;
       odometer.setY(y);
      }
     }
    }
   }
   previous_x = odometer.getX();
   previous_y = odometer.getY();

   // this ensure the odometry correction occurs only once every period
   correctionEnd = System.currentTimeMillis();
   if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
    try {
     Thread.sleep(CORRECTION_PERIOD
       - (correctionEnd - correctionStart));
    } catch (InterruptedException e) {
     // there is nothing to be done here because it is not
     // expected that the odometry correction will be
     // interrupted by another thread
    }
   }
  }
 }
 
}
