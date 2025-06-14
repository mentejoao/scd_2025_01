import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        Scanner teclado = new Scanner(System.in);
        Scanner input = new Scanner(socket.getInputStream());
        PrintStream output = new PrintStream(socket.getOutputStream(), true);

        // Primeiro, receba o prompt e envie o nome
        if (input.hasNextLine()) {
            String prompt = input.nextLine();
            System.out.print(prompt); // Digite seu nome:
        }

        String nome = teclado.nextLine();
        output.println(nome);

        // Agora que o nome foi enviado, iniciamos a thread para escutar o servidor
        new Thread(() -> {
            try {
                while (input.hasNextLine()) {
                    String linha = input.nextLine();
                    System.out.println(linha);
                }
            } catch (Exception e) {
                System.out.println("Conexão encerrada.");
            }
        }).start();

        // Envia mensagens digitadas pelo usuário
        while (teclado.hasNextLine()) {
            String msg = teclado.nextLine();
            output.println(msg);
        }

        socket.close();
    }
}
