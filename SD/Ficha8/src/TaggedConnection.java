import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    public static class Frame {
        public int tag;
        public byte[] data;

        public Frame(int tag, byte[] data) {
            this.tag = tag;
            this.data = data;
        }
    }

    Socket socket;
    DataOutputStream out;
    DataInputStream in;

    ReentrantLock lockRead = new ReentrantLock();
    ReentrantLock lockWrite = new ReentrantLock();

    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public void send(Frame frame) throws IOException {
        lockWrite.lock();
        try{
            out.writeInt(frame.tag);
            out.writeInt(frame.data.length);
            out.write(frame.data);
            out.flush();
        } finally {
            lockWrite.unlock();
        }
    }

    public void send(int tag, byte[] data) throws IOException {
        lockWrite.lock();
        try{
            out.writeInt(tag);
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally {
            lockWrite.unlock();
        }
    }

    public Frame receive() throws IOException {
        lockRead.lock();
        try{
            int tag = in.readInt();
            int size = in.readInt();
            byte[] data = new byte[size];
            in.readFully(data);
            return new Frame(tag, data);
        } finally {
            lockRead.unlock();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}