import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Server {
    static private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );
    public static final int port = 6780;

    // Decoder for incoming text -- assume UTF-8
    static private final Charset charset = Charset.forName("UTF8");
    static private final CharsetDecoder decoder = charset.newDecoder();
    final static String endChar = "\n";


    private static HashMap<SocketChannel, Device> devices = new HashMap<>();
    private static SocketChannel alg_sc; //socket used to communicate with the algorithm
    private final static int N_DEVICES = 3;
    private final static int ARDUINO = 0;
    private final static int ANDROID = 1;
    private final static int SUNSPOT = 2;
    private final static int ACK = 3; // confirmation from the arduino that a semaphore was changed
    private static int n_dev = 0;

    public static void main (String args[]) {
        // Parse port from command line
        try {
            new TrafficSim();
            // Instead of creating a ServerSocket, create a ServerSocketChannel
            ServerSocketChannel ssc = ServerSocketChannel.open();

            // Set it to non-blocking, so we can use select
            ssc.configureBlocking( false );

            // Get the Socket connected to this channel, and bind it to the
            // listening port
            ServerSocket ss = ssc.socket();
            InetSocketAddress isa = new InetSocketAddress( port );
            ss.bind( isa );

            // Create a new Selector for selecting
            Selector selector = Selector.open();

            // Register the ServerSocketChannel, so we can listen for incoming
            // connections
            ssc.register( selector, SelectionKey.OP_ACCEPT );
            System.out.println( "Listening on port "+port );

            while (true) {
                // See if we've had any activity -- either an incoming connection,
                // or incoming data on an existing connection
                int num = selector.select();

                // If we don't have any activity, loop around and wait again
                if (num == 0) {
                    continue;
                }

                // Get the keys corresponding to the activity that has been
                // detected, and process them one by one
                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    // Get a key representing one of bits of I/O activity
                    SelectionKey key = (SelectionKey)it.next();

                    // What kind of activity is it?
                    if (key.isAcceptable()) {
                        // It's an incoming connection.  Register this socket with
                        // the Selector so we can listen for input on it
                        Socket s = ss.accept();
                        System.out.println( "Got connection from "+s );

                        // Make sure to make it non-blocking, so we can use a selector
                        // on it.
                        SocketChannel sc = s.getChannel();
                        sc.configureBlocking( false );

                        // Register it with the selector, for reading
                        sc.register( selector, SelectionKey.OP_READ );

                        sc.write(charset.encode(CharBuffer.wrap("Hello " + n_dev)));
                        if(n_dev == -1)
                            alg_sc = sc;
                        init_message(n_dev);
                        n_dev++;
                    } 
                    else if (key.isReadable()) {
                        SocketChannel sc = null;
                        try {
                            // It's incoming data on a connection -- process it
                            sc = (SocketChannel)key.channel();
                            Device device = null;

                            // if the device already made contact with the server.
                            if(devices.containsKey(sc))
                                device = devices.get(sc);
                            //first time device is sending data to the server: add device.
                            else {
                                System.out.println("PIM");
                                device = new Device(sc);
                                device.set_device_id(n_dev);
                                devices.put(sc, device);
                            }
                            boolean ok = process_input(device);
                            // If the connection is dead, remove it from the selector
                            // and close it
                            if (!ok) {
                                key.cancel();

                                Socket s = null;
                                try {
                                    s = sc.socket();
                                    System.out.println( "Closing connection to "+s );
                                    s.close();
                                } catch( IOException ie ) {
                                    System.err.println( "Error closing socket "+s+": "+ie );
                                }
                            }
                        } catch( Exception ie ) {

                            // On exception, remove this channel from the selector
                            key.cancel();

                            try {
                                sc.close();
                            } catch( IOException ie2 ) { System.out.println( ie2 ); }

                            System.out.println( "Closed " + sc );
                        }
                    }
                }

                // We remove the selected keys, because we've dealt with them.
                keys.clear();
            }
        } catch( IOException ie ) {
            System.err.println( ie );
        }
    }
    // Read and parse messages
    static private boolean process_input(Device device) throws IOException {
        int return_value = 0;
        // Read the message to the buffer
        buffer.clear();
        return_value = device.getSocket().read( buffer );
        String dev_msg = new String(buffer.array(), charset);

        if(return_value == -1)
            return false;

        if(msg_from(ARDUINO, device.id()))
            System.out.println("Got this: " + dev_msg + " from ARDUINO");
        else if(msg_from(ANDROID, device.id()))
            System.out.println("Got this: " + dev_msg + " from ANDROID");

        buffer.flip();

        return true;
    }
    // Debugging message the server prints everytime a device connects
    static private void init_message(int device) {
        if(device == ARDUINO)
            System.out.println("# Arduino just connected...");
        else if(device == ANDROID)
            System.out.println("# Android just connected...");
        else if(device == SUNSPOT)
            System.out.println("# SunSpot just connected...");
    }

    private static void send_message_to(int device, String msg) {
        
    }

    static private void update_android() {
        
    }

    static private boolean msg_from(int device, int id) {
        return device == id;
    }
}

class TrafficSim extends Thread {
    final int CAR_WATING_MAX = 10; // max wating time for a car
    final int QUEUE_WAITING_MAX = 15; // max wating time for 10+ cars
    final int N_SEMAPHORES = 1; // number of active semaphores
    int ON_SEM = 0; // semaphore currently on
    int ON_CROSSWALK = -1; // crosswalk open
    int NEXT_ON = 0; // next semaphore to turn 

    int n_queue[]; //  waiting cars in a given semaphore
    int time_counter = 0;
    LinkedList<Integer> crossw_req;

    int n = 1;

    public TrafficSim() {
        n_queue = new int[N_SEMAPHORES]; // number of cars
        crossw_req = new LinkedList<Integer>();
        start();
    }

    @Override
    public void run() {
        System.out.println("HEY");
        while(true) {
            if(time_counter == n * 1000000000) {
                System.out.println("Hey");
                n++;
                time_counter++;   
            }
            time_counter++;
        }
    }

    private void change_semaphore(int sempahore, int to) {
        // send message to arduino
    }

    // used by the server when it gets a request from the sunspot
    public void add_crossw_request(int crossw) {
        crossw_req.push(crossw);
    }

    private boolean new_crossw_request() {
        return !crossw_req.isEmpty();
    }
}
