import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;

class AndroidThread extends Thread {
    private final int port = 54333;
    private ServerSocket sc; // socket that the Android is connecting to
    private Socket socket;

    private DataOutputStream out; // write to socket
    private DataInputStream in; // read from socket

    public AndroidThread() throws Exception {
        sc = new ServerSocket(port);
        socket = null;
        start();
    }

    @Override
    public void run() {
        try {
            System.out.println("# Android: started thread...");
            socket = sc.accept(); // wait for a connection. When connected enter the while loop
            System.out.println("# Android: got a connection...");
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            String msg; // msg read from socket

             while(true) {
            //     System.out.println("# Android: First Message sent...");
            //     Thread.sleep(5000);
            //     System.out.println("# Android: Second Message sent...");
            //     Thread.sleep(5000);
            //     System.out.println("# Android: Third Message sent...");
            //     break;
             }

            // System.out.println("# Android: closing socket..");
            // socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void parse_msg(String msg) {
        
    }
    // one msg to turn off "off_sem"
    // one msg to turn on "on_sem "semaphore
    public void send_update(String msg) {
        try {
            System.out.println("# Android: Sending message...");

            out.writeBytes(msg + "\n");

            System.out.println("# Android: done...");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
