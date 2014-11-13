import java.nio.channels.SocketChannel;

public class Device {
    private final SocketChannel sc;
    private int id;
	
    public Device(SocketChannel sc) {
        this.sc = sc;    
    }
	
    SocketChannel getSocket() {
        return sc;
    }

    public void set_device_id(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
