package wallFollower;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
 
 private final int bandCenter, bandwidth;
 private final int motorStraight = 200, FILTER_OUT = 20;
 private EV3LargeRegulatedMotor leftMotor, rightMotor;
 private int distance;
 private int filterControl;
 public static final double PROPSCONST = 12.0;
 public static final int MAXCORRECTION_sharp = 200;
 public static final int MAXCORRECTION_wide = 100;
 
 public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
        int bandCenter, int bandwidth) {
  //Constructor
  this.bandCenter = bandCenter;
  this.bandwidth = bandwidth;
  this.leftMotor = leftMotor;
  this.rightMotor = rightMotor;
  
  // Start moving forward
  leftMotor.setSpeed(motorStraight);
  rightMotor.setSpeed(motorStraight);
  leftMotor.forward();
  rightMotor.forward();
  filterControl = 0;
 }
 
 @Override
 public void processUSData(int distance) {
  
  // rudimentary filter - toss out invalid samples corresponding to null signal.
  // (n.b. this was not included in the Bang-bang controller, but easily could have).
  //
  if (distance == 255 && filterControl < FILTER_OUT) {
   // bad value, do not set the distance var, however do increment the filter value
   filterControl ++;
  } else if (distance == 255){
   // true 255, therefore set distance to 255
   this.distance = distance;
  } else {
   // distance went below 255, therefore reset everything.
   filterControl = 0;
   this.distance = distance;
  }
  
  // Process a movement based on the us distance passed in
  // Get Correction Value
    int diff = this.distance-this.bandCenter;
    int corr = this.calcProp(diff);
    System.out.println("Distance From wall: "+this.distance);
    
    
    // Fail safe: will back up straight short distance if very close to wall
    if(this.distance<5)
    {
     this.leftMotor.setSpeed(motorStraight+corr);
     this.rightMotor.setSpeed(motorStraight+corr);
     this.leftMotor.backward();
     this.rightMotor.backward();
     this.leftMotor.setAcceleration(2000);
     this.rightMotor.setAcceleration(2000);
     Sound.twoBeeps();
     try { Thread.sleep(300); } catch(Exception e){}
    }
    
    // If Robot is too close to wall increase speed of right and reduce left motor speed by correction
    if (this.distance > this.bandCenter-this.bandwidth)
    {
     this.leftMotor.setSpeed(motorStraight-corr);  // /2
     this.rightMotor.setSpeed(motorStraight+corr*4);
     this.leftMotor.forward();
     this.rightMotor.forward();
     this.leftMotor.setAcceleration(4000);
     this.rightMotor.setAcceleration(4000);

    }

    // If Robot is too far from wall increase speed of right and reduce left motor speed by correction
    else if(this.distance < this.bandCenter+this.bandwidth)
    {
     this.leftMotor.setSpeed(motorStraight+corr*3);
     this.rightMotor.setSpeed(motorStraight-corr);  // /2
     this.leftMotor.forward();
     this.rightMotor.forward();
     this.leftMotor.setAcceleration(3000);
     this.rightMotor.setAcceleration(3000);
    }

    // Robot is in target range of wall
    else if (this.inBetween(this.bandCenter+this.bandwidth, this.bandCenter-this.bandwidth, this.distance))
    {
     this.leftMotor.setSpeed(motorStraight);
     this.rightMotor.setSpeed(motorStraight);
     this.leftMotor.forward();
     this.rightMotor.forward();
     this.leftMotor.setAcceleration(4000);
     this.rightMotor.setAcceleration(4000);
    }
   
 }

 
 @Override
 public int readUSDistance() {
  return this.distance;
 }
 public boolean inBetween(int upper, int lower, int val)
 {
  if(val<=upper && val>=lower) return true;
  else return false;
 }

 public int calcProp(int diff)
 {
  // for sharper turns use higher constant ie: when very close to wall
  if (diff<0)
  {
   diff=-diff;
   int correction = (int)(PROPSCONST*(double)diff);
   if(correction>=MAXCORRECTION_sharp) return MAXCORRECTION_sharp;
   return correction;
  }
  
  // for wide turns use lower constant ie: when making a 180 degree turn
  else
  {
   int correction = (int)((0.1)*PROPSCONST*(double)diff);
   if(correction>=MAXCORRECTION_wide) return MAXCORRECTION_wide;
   return correction;
  }
  
 }


}
