import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class serverTest {

    public static void main(String[] args) {
        try{
            ServerSocket serverSoket = new ServerSocket(12345);

            while (true) {
                Socket socket = serverSoket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                String line = null;
                while((line = in.readLine()) != null){
                    int i = Integer.parseInt(line);
                    out.println(i);
                    out.flush();
                }

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            }

        } catch (IOException e){
            System.out.println(e);
        }

    }
}
