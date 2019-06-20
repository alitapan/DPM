package Lab4;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab4 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Port usPort = LocalEV3.get().getPort("S1");		
	private static final Port colorPort = LocalEV3.get().getPort("S2");
	private static final ThreadEnder ender = new ThreadEnder();
	
	public static TextLCD Text_LCD = LocalEV3.get().getTextLCD();

	
	public static void main(String[] args) {
		
		//Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		@SuppressWarnings("resource")							    	
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				    // colorData is the buffer in which data are returned
		
		//Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
				
		// setup the odometer and display
		Odometer odo = new Odometer(leftMotor, rightMotor);
		
		int option = mainMenu(); //Display main menu and store selected US Localization method.
		
		LCDInfo lcd = new LCDInfo(odo);	//starts automatically upon creation
		
		odo.start(); //Start odometer thread
		Navigation nav = new Navigation(odo);

		ender.start(); //In case of infinite loops
		
		// perform the ultrasonic localization
		
		switch(option) {
		case Button.ID_LEFT:
			//Falling edge was selected
			USLocalizer usl = new USLocalizer(odo, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
			lcd.pause(); 
			Text_LCD.clear();
			Text_LCD.drawString("Finished US Localization", 0, 0);
			break;
		case Button.ID_RIGHT:
			//Rising edge was selected
			USLocalizer usr = new USLocalizer(odo, usValue, usData, USLocalizer.LocalizationType.RISING_EDGE);
			usr.doLocalization();
			lcd.pause();
			Text_LCD.clear();
			Text_LCD.drawString("Finished US Localization", 0, 0);
			break;
		default:
			//Invalid option - end program
			Text_LCD.clear();
			Text_LCD.drawString("Are you nuts?", 3, 0);
			System.exit(1);
		}
		
		nav.turnTo(0, true);
		Button.waitForAnyPress();
		
		lcd.resume();
		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, colorValue, colorData);
		lsl.doLocalization();	
	
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
		
	}
	
	private static int mainMenu() {
		//Display main menu, return button id of input
		Text_LCD.clear();
		Text_LCD.drawString("Fall    N   <A>  ", 0, 0);
		Text_LCD.drawString("     -' | '-/    ", 0, 1);
		Text_LCD.drawString("    /   | / \\   ", 0, 2);
		Text_LCD.drawString(" -W)----o----(E- ", 0, 3);
		Text_LCD.drawString("    \\  /|   /    ", 0, 4);
		Text_LCD.drawString("     /-.|.-      ", 0, 5);
		Text_LCD.drawString("   <>   S    Rise", 0, 6);	

		return Button.waitForAnyPress();
	}

}
