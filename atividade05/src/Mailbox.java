public class Mailbox {
    private String message = null;

    public synchronized void storeMessage(String msg) throws InterruptedException {
        while (message != null) {
            wait();
        }
        message = msg;
        notifyAll();
    }

    public synchronized String retrieveMessage() throws InterruptedException {
        while (message == null) {
            wait();
        }
        String msg = message;
        message = null;
        notifyAll();
        return msg;
    }
}