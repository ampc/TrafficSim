import java.io.*; 
import java.net.*;

class Server extends Thread implements Runnable {
    public final static int N_DEVICES = 3;
    public final static int ARDUINO = 0;
    public final static int ANDROID = 1;
    public final static int SUNSPOT = 2;
    public final static int C_ACCEPTED = 4;
    public final static int IDENTITY_QUERY = 4;

    public static final int port = 6780;
    private static int n_connected = 0;

    final private ServerSocket server_socket;
    final private DeviceSocket[] devices_sockets;

    String msg_string; // any message received
    ServerSocket socket; // main server socket

    public Server() throws Exception {
        this.server_socket = new ServerSocket(port);
        devices_sockets = new DeviceSocket[N_DEVICES];

        start();
        //run();
    }

    @Override
    public void run() {
        boolean dev_identified;
        try {
            while ( !this.interrupted() ) {
                System.out.println("PIM");
                dev_identified = false;
                //wait for clients
                Socket connection = this.server_socket.accept();
                DeviceSocket d_socket = new DeviceSocket(connection);
                devices_sockets[n_connected] = d_socket;
                if(n_connected == ARDUINO) {
                    System.out.println("0");
                    d_socket.send_message("You are Arduino");
                }
                else if(n_connected == ANDROID) {
                    System.out.println("1");
                    d_socket.send_message("You are Android");
                }
                else if(n_connected == SUNSPOT) {
                    System.out.println("2");
                    d_socket.send_message("You are SUNSPOT");
                }
                n_connected++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            }
    }
    protected class DeviceSocket extends Thread {
        final private Socket socket;
        public int device;
        public boolean identified;

        BufferedReader in; // read from socket devices messages
        DataOutputStream out; // output messages to the devices through the socket
        String device_msg;

        public DeviceSocket(Socket socket) throws Exception {
            this.socket = socket;
            device_msg = "";
            device = -1;
            identified = false;

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            start();
        }

        public void set_identity(int device) {
            this.device = device;
            this.identified = true;
        }

        public void send_message(String msg) throws Exception {
            out.writeBytes(msg);            
        }

        @Override
        public void run() {
            try {
                while(!this.interrupted()) {
                    System.out.println("CLIENT");
                    device_msg = in.readLine();
                    System.out.println("Received from" + this.device + ": " + device_msg);
                    out.writeBytes(device + ",I received this from you: " + device_msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void parse_msg(String d_msg) {
            // device is identifying himself
            System.out.println("# Client: " + d_msg.length());
            if(d_msg.length() == 1)
                set_identity(Integer.parseInt(d_msg));
            else {
                // do something else
            }
        }

        public void close() {
            try {
                this.socket.close();
            } catch ( IOException e ) {
                //ignore
            }
        }
    }
}

public class TrafficSim  {
    public static void main(String []Args) throws Exception { 
        Server server = new Server();
    }
}
