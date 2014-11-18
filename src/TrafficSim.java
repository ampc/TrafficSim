import java.util.Scanner;

public class TrafficSim {
    static void test_android() {
	try {
	    AndroidThread android = new AndroidThread();
	    Thread.sleep(5000);
	    android.send_update(0,0);
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    static void test_arduino() { 
	try {
	    ArduinoThread arduino = new ArduinoThread();
	    Thread.sleep(5000);
	    arduino.send_change_request(0,0);
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    static void test_sspot() {
	System.out.println("No implemented yet...");
    }

    public static void main(String args[]) {
	if(args.length == 1) { 
	    if(args[0].equals("--test-android"))
		test_android();
	    else if(args[0].equals("--test-arduino"))
		test_arduino();
	    else if(args[0].equals("--test-sspot")) {
		test_sspot();
	    }
	}
	else {
	    System.out.println("Running algorithm...");
	}

	    
	// start threads
	// wait around < 10 seconds to give enough time to all devices to connect
	// start algorithm
    }
}
