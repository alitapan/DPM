package Lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;


//create this thread only for polling data from US sensor every 50ms
public class UltrasonicPoller extends Thread{
 
 
 private Port usPort = LocalEV3.get().getPort("S1");
 EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(usPort);  // usSensor is the instance
 SampleProvider usDistance = usSensor.getMode("Distance");        // usDistance provides samples from this instance
 float[] usData = new float[usDistance.sampleSize()];             // usData is the buffer in which data are returned
 private Object lock;
 
 public UltrasonicPoller() {
  lock = new Object();
 }
 public void disableSensor(){
  usSensor.disable();
 }
 public void enableSensor(){
  usSensor.enable();
 }
// sensors now return floats using a uniform protocol. 
 public int getUsData() {
  synchronized (lock) {
   return (int)(usData[0]*100.0);
  }
 }

 public void run() {
  
  while (true) {
   synchronized(lock){
    if(usSensor.isEnabled()){
    usDistance.fetchSample(usData,0); // acquire data
    }
   } 
   try { Thread.sleep(50); } catch(Exception e){}  // Poor man's timed sampling
  }
 }

}
