import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Map<String, PrintStream> usuarios = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(12345);
        System.out.println("Servidor iniciado na porta 12345");

        while (true) {
            Socket cliente = servidor.accept();
            new Thread(() -> tratarCliente(cliente)).start();
        }
    }

    private static void tratarCliente(Socket cliente) {
        String nome = null;
        try {
            Scanner entrada = new Scanner(cliente.getInputStream());
            PrintStream saida = new PrintStream(cliente.getOutputStream(), true);

            // Solicita o nome do usuário
            saida.println("Digite seu nome:");
            nome = entrada.nextLine();

            synchronized (usuarios) {
                if (usuarios.containsKey(nome)) {
                    saida.println("Nome já está em uso. Conexão encerrada.");
                    cliente.close();
                    return;
                }
                usuarios.put(nome, saida);
            }

            broadcast("*", nome + " entrou no chat.");

            while (entrada.hasNextLine()) {
                String linha = entrada.nextLine();

                if (!linha.contains(":")) {
                    saida.println("Formato inválido. Use destinatario:mensagem");
                    continue;
                }

                String[] partes = linha.split(":", 2);
                String destino = partes[0].trim();
                String mensagem = partes[1].trim();

                if (destino.equals("*")) {
                    broadcast(nome, nome + ": " + mensagem);
                } else {
                    enviarPrivado(nome, destino, nome + ": " + mensagem);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro com o cliente " + nome + ": " + e.getMessage());
        } finally {
            if (nome != null) {
                synchronized (usuarios) {
                    usuarios.remove(nome);
                }
                broadcast("*", nome + " saiu do chat.");
            }

            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void enviarPrivado(String remetente, String destino, String mensagem) {
        synchronized (usuarios) {
            PrintStream ps = usuarios.get(destino);
            if (ps != null) {
                ps.println(mensagem);
            } else {
                PrintStream origem = usuarios.get(remetente);
                if (origem != null) {
                    origem.println("Usuário " + destino + " não encontrado.");
                }
            }
        }
    }

    private static void broadcast(String remetente, String mensagem) {
        synchronized (usuarios) {
            for (Map.Entry<String, PrintStream> entry : usuarios.entrySet()) {
                if (!entry.getKey().equals(remetente)) {
                    entry.getValue().println(mensagem);
                }
            }
        }
    }
}
