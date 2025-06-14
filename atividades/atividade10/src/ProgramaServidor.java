import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ProgramaServidor {

    private static final int PORT = 12345;
    private static final Map<String, String> carInfo = new HashMap<>();

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader("Mapeamentos-CarrosHistorico.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();
                if (linha.startsWith("(\"") && linha.endsWith(");")) {
                    linha = linha.substring(1, linha.length() - 2);
                    int separador = linha.indexOf("\", \"");
                    if (separador != -1) {
                        String chave = linha.substring(1, separador);
                        String valor = linha.substring(separador + 4, linha.length() - 1);
                        carInfo.put(chave.toLowerCase(), valor);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o banco de dados: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Servidor iniciado na porta " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Aceita novos clientes para sempre
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e);
        }
    }

    // Trata um cliente por thread
    private static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String cliente = socket.getRemoteSocketAddress().toString();
            System.out.println("Conectado: " + cliente);
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String linha;
                while ((linha = in.readLine()) != null) {
                    String marca = linha.trim().toLowerCase();
                    if (marca.equals("sair")) break;

                    String resposta = carInfo.getOrDefault(
                            marca,
                            "Desculpe, não encontrei informações sobre a marca \"" + marca + "\".");
                    out.println(resposta);
                }
            } catch (IOException e) {
                System.err.println("Erro com cliente " + cliente + ": " + e);
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
                System.out.println("Desconectado: " + cliente);
            }
        }
    }
}
