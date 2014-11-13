import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Server2 {
    static private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );
    public static final int port = 6780;

    // Decoder for incoming text -- assume UTF-8
    static private final Charset charset = Charset.forName("UTF8");
    static private final CharsetDecoder decoder = charset.newDecoder();
    final static String endChar = "\n";


    private static HashMap<SocketChannel, Device> devices = new HashMap<>();
    private final static int N_DEVICES = 3;
    private final static int ARDUINO = 0;
    private final static int ANDROID = 1;
    private final static int SUNSPOT = 2;
    private static int n_dev = 0;


    static public void main( String args[] ) throws Exception {
        // Parse port from command line
        try {
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
                            boolean ok = processInput(device);
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
    // Just read the message from the socket and send it to stdout
    static private boolean processInput(Device user) throws IOException {
        int returnValue = 0;
        // Read the message to the buffer
        buffer.clear();
        returnValue = user.getSocket().read( buffer );
        String userMessage = new String(buffer.array(), charset);

        if(returnValue == -1)
            return false;

        System.out.println(userMessage);

        buffer.flip();

        return true;
    }
}
