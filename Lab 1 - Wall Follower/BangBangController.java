
package wallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
 private final int bandCenter, bandwidth;
 private final int motorLow, motorHigh;
 private int distance;
 private EV3LargeRegulatedMotor leftMotor, rightMotor;
 private int filterControl;
 private final int motorStraight = 100, FILTER_OUT = 20;
 
 public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
         int bandCenter, int bandwidth, int motorLow, int motorHigh) {
  //Default Constructor
  this.bandCenter = bandCenter;
  this.bandwidth = bandwidth;
  this.motorLow = motorLow;
  this.motorHigh = motorHigh;
  this.leftMotor = leftMotor;
  this.rightMotor = rightMotor;
  
  // Start robot moving forward
  leftMotor.setSpeed(motorHigh); 
  rightMotor.setSpeed(motorHigh);
  leftMotor.forward();
  rightMotor.forward();
 }
 
 @Override
 public void processUSData(int distance) {
  this.distance = distance;
    // This method process a movement based on the us distance passed in
    // If too close to the wall move away
    if (this.distance < this.bandCenter-this.bandwidth)
    {
     
     this.rightMotor.setSpeed((int)((1.0)*motorLow));
     this.leftMotor.setSpeed((int)(motorHigh+(5.5)*motorLow));
     this.leftMotor.forward();
     this.rightMotor.forward();
    }

    // If too far from the wall move closer
    else if(this.distance > this.bandCenter+this.bandwidth)
    {
     
     this.leftMotor.setSpeed((int)(motorHigh+(1.0)*motorLow));
     this.rightMotor.setSpeed((int)(motorHigh+(4.0)*motorLow));
     this.leftMotor.forward();
     this.rightMotor.forward();
    }
    
    // If distance from the wall is optimal stay on track
    else
    {
     this.leftMotor.setSpeed(motorHigh);
     this.rightMotor.setSpeed(motorHigh);
     this.leftMotor.forward();
     this.rightMotor.forward();
    }
   }
 

 @Override
 public int readUSDistance() {
  return this.distance;
 }
}
