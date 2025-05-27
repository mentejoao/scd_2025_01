import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ProgramaCliente {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(
                     socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado ao servidor. Digite o nome da marca ou 'sair' para terminar.");

            while (true) {
                System.out.print("Marca: ");
                String marca = scanner.nextLine();
                out.println(marca);

                if (marca.equalsIgnoreCase("sair")) break;

                String resposta = in.readLine();
                System.out.println("Servidor: " + resposta);
            }

        } catch (IOException e) {
            System.err.println("Erro no cliente: " + e);
        }

        System.out.println("Cliente encerrado.");
    }
}
