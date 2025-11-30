import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    TaggedConnection con;
    ReentrantLock lockGlobal = new ReentrantLock();
    Map<Integer, TaggedConnection.Frame> mapa = new HashMap<Integer, TaggedConnection.Frame>();
    boolean running = false;

    public Demultiplexer(TaggedConnection conn) {
        this.con = conn;
    }
    public void start() {
        running = true;
        new Thread(() -> {
            try{
                while(running){

                }
            } catch (Exception ignored) {}
        });
    }
    public void send(TaggedConnection.Frame frame) throws IOException {

    }
    public void send(int tag, byte[] data) throws IOException {

    }
    public byte[] receive(int tag) throws IOException, InterruptedException {
        return;
    }
    public void close() throws IOException {

    }
}