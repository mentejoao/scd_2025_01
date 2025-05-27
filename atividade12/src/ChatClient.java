import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        Scanner teclado = new Scanner(System.in);
        Scanner input = new Scanner(socket.getInputStream());
        PrintStream output = new PrintStream(socket.getOutputStream(), true);

        if (input.hasNextLine()) {
            String prompt = input.nextLine();
            System.out.print(prompt);
        }

        String nome = teclado.nextLine();
        output.println(nome);

        new Thread(() -> {
            try {
                while (input.hasNextLine()) {
                    String linha = input.nextLine();
                    if (linha.startsWith("FILE:")) {
                        // Formato: FILE:nome_arquivo
                        String nomeArquivo = linha.substring(5);
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        long tamanho = dis.readLong();
                        FileOutputStream fos = new FileOutputStream("recebido_" + nomeArquivo);
                        byte[] buffer = new byte[4096];
                        int bytesLidos;
                        long bytesRecebidos = 0;
                        while (bytesRecebidos < tamanho && (bytesLidos = dis.read(buffer, 0, (int) Math.min(buffer.length, tamanho - bytesRecebidos))) != -1) {
                            fos.write(buffer, 0, bytesLidos);
                            bytesRecebidos += bytesLidos;
                        }
                        fos.close();
                        System.out.println("Arquivo recebido: " + nomeArquivo);
                    } else {
                        System.out.println(linha);
                    }
                }
            } catch (Exception e) {
                System.out.println("Conexão encerrada.");
            }
        }).start();

        while (teclado.hasNextLine()) {
            String msg = teclado.nextLine();

            if (msg.contains(":File:")) {
                String[] partes = msg.split(":File:");
                if (partes.length == 2) {
                    String destinatario = partes[0];
                    String nomeArquivo = partes[1];
                    File arquivo = new File(nomeArquivo);
                    if (!arquivo.exists()) {
                        System.out.println("Arquivo não encontrado: " + nomeArquivo);
                        continue;
                    }
                    output.println(destinatario + ":File:" + arquivo.getName());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    FileInputStream fis = new FileInputStream(arquivo);
                    dos.writeLong(arquivo.length());
                    byte[] buffer = new byte[4096];
                    int bytesLidos;
                    while ((bytesLidos = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, bytesLidos);
                    }
                    fis.close();
                    System.out.println("Arquivo enviado: " + nomeArquivo);
                }
            } else {
                output.println(msg);
            }
        }

        socket.close();
    }
}
