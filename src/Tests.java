class Tests {

    public Tests() {}

    void test_android() {
        try {
            AndroidThread android = new AndroidThread();
            Thread.sleep(5000);
            android.send_update("1H13");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void test_arduino() { 
        try {
            System.out.println("PIM");
            ArduinoThread arduino = new ArduinoThread("192.168.1.2", 53333);
            // Thread.sleep(5000);
            // arduino.send_change_request(0,0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    void test_sspot() {
        System.out.println("No implemented yet...");
    }
}
