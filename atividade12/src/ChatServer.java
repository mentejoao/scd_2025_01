import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Map<String, Socket> usuarios = new HashMap<>();

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

            saida.println("Digite seu nome:");
            nome = entrada.nextLine();

            synchronized (usuarios) {
                if (usuarios.containsKey(nome)) {
                    saida.println("Nome já está em uso. Conexão encerrada.");
                    cliente.close();
                    return;
                }
                usuarios.put(nome, cliente);
            }

            broadcast("*", nome + " entrou no chat.");

            while (entrada.hasNextLine()) {
                String linha = entrada.nextLine();

                if (!linha.contains(":")) {
                    saida.println("Formato inválido. Use destinatario:mensagem");
                    continue;
                }

                String[] partes = linha.split(":", 3);
                String destino = partes[0].trim();
                String tipo = partes.length > 2 ? partes[1].trim() : "";
                String conteudo = partes.length > 2 ? partes[2].trim() : partes[1].trim();

                if ("File".equalsIgnoreCase(tipo)) {
                    Socket destinoSocket = usuarios.get(destino);
                    if (destinoSocket != null) {
                        PrintStream destinoSaida = new PrintStream(destinoSocket.getOutputStream(), true);
                        destinoSaida.println("FILE:" + conteudo);

                        DataInputStream dis = new DataInputStream(cliente.getInputStream());
                        DataOutputStream dos = new DataOutputStream(destinoSocket.getOutputStream());

                        long tamanho = dis.readLong();
                        dos.writeLong(tamanho);

                        byte[] buffer = new byte[4096];
                        int bytesLidos;
                        long total = 0;
                        while (total < tamanho && (bytesLidos = dis.read(buffer, 0, (int) Math.min(buffer.length, tamanho - total))) != -1) {
                            dos.write(buffer, 0, bytesLidos);
                            total += bytesLidos;
                        }
                        dos.flush();
                        System.out.println("Arquivo encaminhado de " + nome + " para " + destino);
                    } else {
                        saida.println("Usuário " + destino + " não encontrado.");
                    }
                } else {
                    if (destino.equals("*")) {
                        broadcast(nome, nome + ": " + conteudo);
                    } else {
                        enviarPrivado(nome, destino, nome + ": " + conteudo);
                    }
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
            Socket socket = usuarios.get(destino);
            if (socket != null) {
                try {
                    PrintStream ps = new PrintStream(socket.getOutputStream(), true);
                    ps.println(mensagem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Socket remetenteSocket = usuarios.get(remetente);
                if (remetenteSocket != null) {
                    try {
                        PrintStream ps = new PrintStream(remetenteSocket.getOutputStream(), true);
                        ps.println("Usuário " + destino + " não encontrado.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void broadcast(String remetente, String mensagem) {
        synchronized (usuarios) {
            for (Map.Entry<String, Socket> entry : usuarios.entrySet()) {
                if (!entry.getKey().equals(remetente)) {
                    try {
                        PrintStream ps = new PrintStream(entry.getValue().getOutputStream(), true);
                        ps.println(mensagem);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
