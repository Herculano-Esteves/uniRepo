import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EchoServer {

    public static class process implements Runnable{
        public Socket socket;
        public Registos registo;

        public process(Socket socket, Registos registo){
            this.socket = socket;
            this.registo = registo;
        }

        public void run(){
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                String line;
                while ((line = in.readLine()) != null) {
                    int i = Integer.parseInt(line);
                    registo.addRegisto(i);
                    out.println(i);
                    out.flush();
                }
                out.println(registo.getMedia());
                out.flush();

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
                System.out.println("I will close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            Socket socket;
            Registos registo = new Registos();
            while((socket = ss.accept()) != null){
                Thread t1 = new Thread(new process(socket, registo));
                t1.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Registos{
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        float media = 0;
        int numDeRegistos = 0;

        public void addRegisto(int numero){
            lock.writeLock().lock();
            try{
                if(numDeRegistos == 0){
                    numDeRegistos++;
                    media = numero;
                } else {
                    media = ((media * numDeRegistos) + numero) / (numDeRegistos + 1);
                    numDeRegistos++;
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public int getMedia(){
            lock.readLock().lock();
            try{
                return (int)media;
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
