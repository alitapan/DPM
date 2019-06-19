package Lab4;

import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;
import lejos.hardware.lcd.TextLCD;

public class Lab4 {

 // Static Resources:
 // Left motor connected to output A
 // Right motor connected to output D
 // Ultrasonic sensor port connected to input S1
 // Color sensor port connected to input S2
  
 public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
 public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
 private static final Port usPort = LocalEV3.get().getPort("S1");  
 private static final Port colorPort = LocalEV3.get().getPort("S2");  

 
 public static void main(String[] args) {
 
 
  int buttonChoice;
  
  final TextLCD t = LocalEV3.get().getTextLCD();
  
  //Setup ultrasonic sensor
  // 1. Create a port object attached to a physical port (done above)
  // 2. Create a sensor instance and attach to port
  // 3. Create a sample provider instance for the above and initialize operating mode
  // 4. Create a buffer for the sensor data
  @SuppressWarnings("resource") 
  SensorModes usSensor = new EV3UltrasonicSensor(usPort);
  SampleProvider usValue = usSensor.getMode("Distance");   // colorValue provides samples from this instance
  float[] usData = new float[usValue.sampleSize()];        // colorData is the buffer in which data are returned
  
  //Setup color sensor
  // 1. Create a port object attached to a physical port (done above)
  // 2. Create a sensor instance and attach to port
  // 3. Create a sample provider instance for the above and initialize operating mode
  // 4. Create a buffer for the sensor data
  //EV3ColorSensor lightSensor = new EV3ColorSensor(colorPort);
  //SensorMode mode = lightSensor.getRedMode();
  //float[] sample = new float[mode.sampleSize()];
  //mode.fetchSample(sample, 0);
  //float intensity = sample[0];
  EV3ColorSensor cs = new EV3ColorSensor(colorPort);
  SampleProvider colorValue = cs.getMode("Red");            // colorValue provides samples from this instance
  float[] colorData = new float[colorValue.sampleSize()];   // colorData is the buffer in which data are returned
  
    
  // setup the odometer and display
  Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
  Navigation robot = new Navigation(odo);
  LCDInfo lcd = new LCDInfo(odo);
  
  do {
   // clear the display
   t.clear();
   // ask the user whether the motors should drive in a square or float
   t.drawString("< US    |Light >", 0, 0);
   t.drawString("        |       ", 0, 1);
   t.drawString(" local  |Local  ", 0, 2);
   t.drawString(" alizer |alizer ", 0, 3);
   t.drawString("        |       ", 0, 4);
   buttonChoice = Button.waitForAnyPress();
  } while (buttonChoice != Button.ID_LEFT
    && buttonChoice != Button.ID_RIGHT);
  
  if (buttonChoice == Button.ID_LEFT) {
  
  // perform the ultrasonic localization
  USLocalizer usl = new USLocalizer(odo, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE, robot);
  usl.doLocalization();
  }
  else{
  // perform the light sensor localization
  //LightLocalizer lsl = new LightLocalizer(odo, colorSensor, colorData);
  LightLocalizer lsl = new LightLocalizer(odo, cs, robot);
  lsl.doLocalization();   
  }
  while (Button.waitForAnyPress() != Button.ID_ESCAPE);
  System.exit(0); 
  
 }
 

 
}
