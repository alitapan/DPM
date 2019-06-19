package Lab3;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Lab3 {
 
 // Static Resources:
 // Left motor connected to output A
 // Right motor connected to output D
 
 public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
 public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
 public static final EV3LargeRegulatedMotor usMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

 // Constants
 public static final double LEFT_WHEEL_RADIUS = 2.125;
 public static final double RIGHT_WHEEL_RADIUS = 2.125;
 public static final double TRACK = 16.70;
 public static double[][] path1 = { {60.0,30.0}, {30.0,30.0}, {30.0,60.0}, {60.0,0.0} };
 public static double[][] path2 = { {0.0,60.0}, {60.0, 0.0} };
 public static double xDest;
 public static double yDest;

 public static void main(String[] args) {
  int buttonChoice;

  // some objects that need to be instantiated
  
  final TextLCD t = LocalEV3.get().getTextLCD();
  
  //create this thread only for polling data from US sensor every 50ms
  UltrasonicPoller usPoller = new UltrasonicPoller();
   
  
  /*assumes that the robot¡¯s initial orientation is 0
  radians, which by convention is pointing along the positive y-axis
  */
  Odometer odometer = new Odometer(leftMotor, rightMotor, LEFT_WHEEL_RADIUS, RIGHT_WHEEL_RADIUS, TRACK); 
  OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
  
  //create the thread class for controlling the robot's movement. 
  Drive drive1 = new Drive(leftMotor, rightMotor, LEFT_WHEEL_RADIUS, RIGHT_WHEEL_RADIUS, TRACK, odometer, path1);
  Drive drive2 = new Drive(leftMotor, rightMotor, LEFT_WHEEL_RADIUS, RIGHT_WHEEL_RADIUS, TRACK, odometer, path2);
  Avoid avoid = new Avoid(usPoller, odometer, drive2);
   
  //set starting position to be (0,0) towards positive y direction  
  odometer.setPosition (new double[] {0.0, 0.0, 0.0} , new boolean[] {true, true, true} );
  
  do {
   // clear the display
   t.clear();

   // ask the user whether the motors should drive in a square or float
   t.drawString("< Left | Right >", 0, 0);
   t.drawString("       |        ", 0, 1);
   t.drawString(" First | Second ", 0, 2);
   t.drawString(" part  | part   ", 0, 3);
   t.drawString("       |        ", 0, 4);

   buttonChoice = Button.waitForAnyPress();
  } while (buttonChoice != Button.ID_LEFT
    && buttonChoice != Button.ID_RIGHT);

  if (buttonChoice == Button.ID_LEFT) {
   //start first demo
   odometer.start();
   odometryDisplay.start();   
   drive1.start();
  } 
  else {
   //start second demo
   odometer.start();
   odometryDisplay.start();
   usPoller.start();
   drive2.start();
   avoid.start();
  }  
  while (Button.waitForAnyPress() != Button.ID_ESCAPE);
  System.exit(0);
 }
}
