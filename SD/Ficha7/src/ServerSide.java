import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerSide {
    static class serverWorker implements Runnable{

        private Socket socket;
        private Registo registo;
        public serverWorker(Socket socket, Registo registo) {
            this.socket = socket;
            this.registo = registo;
        }

        public void run(){
            try{
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                while (true) {
                    try {
                        Contact c = Contact.deserialize(in);
                        System.out.println("Recebido: " + c.name());
                        registo.addRegisto(c);
                        out.writeUTF("Contacto " + c.name() + " recebido com sucesso!");
                        out.flush();
                    } catch (EOFException e) {
                        System.out.println("Fim da ligação com cliente.");
                        break;
                    }
                }
                out.flush();
                socket.close();
            } catch (IOException e){
                System.err.println("Connection failed: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        try{
            ServerSocket serversocket = new ServerSocket(12345);
            Registo meuRegisto = new Registo();
            while(true){
                Socket socket = serversocket.accept();
                Thread t1 = new Thread(new serverWorker(socket, meuRegisto));
                t1.start();
            }
        } catch (IOException e){
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    static class Registo{
        HashMap<String,Contact> contactos = new HashMap<String, Contact>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public void addRegisto(Contact contacto){
            lock.writeLock().lock();
            try{
                contactos.put(contacto.name(),contacto);
                System.out.println("Adicionei: " + contacto.name());
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Contact getContacto(String name){
            lock.readLock().lock();
            try{
                return contactos.get(name);
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
