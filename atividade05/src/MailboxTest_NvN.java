import java.util.Scanner;

public class MailboxTest_NvN {
        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            Mailbox mailbox = new Mailbox();

            System.out.print("Digite o número de produtores: ");
            int numProdutores = scanner.nextInt();

            System.out.print("Digite o número de consumidores: ");
            int numConsumidores = scanner.nextInt();

            for (int i = 1; i <= numProdutores; i++) {
                Producer p = new Producer(mailbox, "P" + i);
                p.start();
            }

            for (int i = 1; i <= numConsumidores; i++) {
                Consumer c = new Consumer(mailbox, "C" + i);
                c.start();
            }

            scanner.close();
        }
    }
