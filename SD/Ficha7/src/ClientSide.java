import java.io.*;
import java.net.*;
import java.util.*;

public class ClientSide {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Ligado ao servidor. Introduza contactos no formato:");
            System.out.println("Nome Idade Telefone Empresa Email1 Email2 ...");
            System.out.println("CTRL+D (Linux/macOS) ou CTRL+Z (Windows) para terminar.");

            String line;
            while ((line = stdin.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Divide a linha pelos espaços
                String[] parts = line.split("\\s+");
                if (parts.length < 5) {
                    System.out.println("Formato inválido! Exemplo: John 20 253123456 Company email1 email2");
                    continue;
                }

                String name = parts[0];
                int age = Integer.parseInt(parts[1]);
                long phone = Long.parseLong(parts[2]);
                String company = parts[3];

                // Os restantes são emails
                List<String> emails = new ArrayList<>();
                for (int i = 4; i < parts.length; i++) {
                    emails.add(parts[i]);
                }

                // Cria e envia o contacto
                Contact c = new Contact(name, age, phone, company, emails);
                c.serialize(out);
                out.flush();

                // Lê resposta do servidor
                String resposta = in.readUTF();
                System.out.println("Servidor: " + resposta);
            }

            System.out.println("Cliente terminou.");
        } catch (IOException e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
    }
}
