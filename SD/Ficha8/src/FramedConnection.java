import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    ReentrantLock lockRead = new ReentrantLock();
    ReentrantLock lockWrite = new ReentrantLock();

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(this.socket.getOutputStream());
        this.in = new DataInputStream(this.socket.getInputStream());
    }
    public void send(byte[] data) throws IOException {
        lockWrite.lock();
        try{
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally {
            lockWrite.unlock();
        }
    }
    public byte[] receive() throws IOException {
        lockRead.lock();
        try{
            int size = in.readInt();
            byte[] data = new byte[size];
            in.readFully(data);
            return data;
        } finally {
            lockRead.unlock();
        }
    }
    public void close() throws IOException {
        socket.close();
    }
}