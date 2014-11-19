import java.util.Scanner;

public class TrafficSim {
    public static void main(String args[]) {
        if(args.length == 1) {
            Tests t = null;
            if(args[0].equals("--test-android")) {
                t = new Tests();
                t.test_android();
            }
            else if(args[0].equals("--test-arduino")) {
                t = new Tests();
                t.test_arduino();
            }
            else if(args[0].equals("--test-sspot")) {
                t = new Tests();
                t.test_sspot();
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
