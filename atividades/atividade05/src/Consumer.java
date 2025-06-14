public class Consumer extends Thread {
    private Mailbox mailbox;
    private String id;

    public Consumer(Mailbox mailbox, String id) {
        this.mailbox = mailbox;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = mailbox.retrieveMessage();
                System.out.println("[Consumidor " + id + "] Recebeu: " + msg);
                Thread.sleep(750); // tempo de consumo 250 ms maior que a produçao
            }
        } catch (InterruptedException e) {
            System.out.println("[Consumidor " + id + "] Interrompido.");
        }
    }
}
