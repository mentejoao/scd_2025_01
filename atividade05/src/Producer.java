public class Producer extends Thread {
    private Mailbox mailbox;
    private String id;

    public Producer(Mailbox mailbox, String id) {
        this.mailbox = mailbox;
        this.id = id;
    }

    @Override
    public void run() {
        int count = 1;
        try {
            while (true) {
                String msg = "Mensagem " + count + " do Produtor " + id;
                mailbox.storeMessage(msg);
                System.out.println("[Produtor " + id + "] Enviou: " + msg);
                count++;
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("[Produtor " + id + "] Interrompido.");
        }
    }
}
