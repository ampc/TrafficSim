public class TrafficSim {
    static void test_run() {
        try {
            // ArduinoThread arduino = new ArduinoThread();
            // Thread.sleep(5000);
            // arduino.send_change_request(0,0);
            AndroidThread android = new AndroidThread();
            Thread.sleep(5000);
            android.send_update(0,0);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String arg[]) {
        test_run();

        // start threads
        // wait around < 10 seconds to give enough time to all devices to connect
        // start algorithm
    }
}
