public class MailboxTest_1v1 {
    public static void main(String[] args) {
        Mailbox mailbox = new Mailbox();

        Producer p1 = new Producer(mailbox, "P1");
        Consumer c1 = new Consumer(mailbox, "C1");

        p1.start();
        c1.start();
    }
}
