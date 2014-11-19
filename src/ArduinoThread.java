import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/*
  Each ArduinoThread class will make a connection to a server in an arduino, thus
  representing each, a semaphore.
*/
class ArduinoThread extends Thread {
    private final String INPUT = "I";
    private final String OUTPUT = "O";

    // ON and OFF are used to check the state of the light in this semaphore
    private final int ON = 1;
    private final int OFF = 0;

    private final String RED_ON = "H13"; // H is to turn on, 11 is the red light
    private final String RED_OFF = "L13";
    private final String RED_STATE = "R13";

    private int port;
    private int SEM_ID;
    private String SEM_IP;

    private final int ACK = 1;
    private final String NEW_LINE = "\n";

    ServerSocket sc; // socket that the Arduino is connecting to
    Socket socket;

    DataOutputStream out; // write to socket
    BufferedReader in; // read from socket

    public ArduinoThread(String SEM_IP, int port) throws Exception {
        socket = new Socket("192.168.1.2", port);
        this.port = port;
        this.SEM_IP = SEM_IP;
        this.SEM_ID = set_id(SEM_IP);
        start();
    }

    // sets the semaphore ID according to the last digit of its IP
    private int set_id(String SEM_IP) {
        return Integer.parseInt(SEM_IP.split("\\.")[3]);
    }

    @Override
    public void run() {
        try {
            System.out.println("# Arduino: started thread...");
            //socket = sc.accept(); // wait for a connection. When connected enter the while loop
            System.out.println("# Arduino: got a connection...");
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg; // msg read from socket

            while(true) {
                //     System.out.println("# Arduino: First Message sent...");
                //     Thread.sleep(5000);
                //     System.out.println("# Arduino: Second Message sent...");
                //     Thread.sleep(5000);
                //     System.out.println("# Arduino: Third Message sent...");
                //     break;
            }

            // System.out.println("# Arduino: closing socket..");
            // socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void parse_msg(String msg) {
        
    }
    // one msg to turn off "off_sem"
    // one msg to turn on "on_sem "semaphore
    public void send_change_request(int on_sem, int off_sem) {
        try {
            String msg;
            System.out.println("# Arduino: Sending message...");

            // out.writeBytes(RED_OFF + NEW_LINE);
            // out.writeBytes(RED_STATE + NEW_LINE); // ask for the semaphore state. Response is equivalent to an ACK

            // if(!change_confirmed(OFF)) {
            //     // exit this function and call try again?;
            // }
            // else
            //     System.out.println("# Arduino: OFF was a Success");
            out.writeBytes(OUTPUT + "13" + NEW_LINE); // ask arduino to be in input mode
            out.writeBytes(RED_ON +  NEW_LINE);

            out.writeBytes(RED_STATE + NEW_LINE); // ask for the semaphore state.
            //            Response is equivalent to an ACK

            if(!change_confirmed(ON)) {
                // exit this function and call try again?
            }
            else
                System.out.println("# Arduino: ON was a Success");
            
            // update arduino
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Return if the request to change semaphore was fullfiled
    // Receives as parameter the expected state of the semaphore
    // E.g, if the arduino was asked to turn off a semaphore, expected state will be OFF
    public boolean change_confirmed(int expected_state) {
        try {
            String msg;
            while((msg = in.readLine()) == null); // obligatory to wait for "ack"
            msg = msg.replace(NEW_LINE, ""); // remove "\n"

            return Integer.parseInt(msg) == expected_state;

        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void update_android() {
        
    }
    // private class LastChange {
    //     int current_semaphore; // semaphore that was turned on
    //     int last_semaphore; // semaphore that was turned off
    // }
}
