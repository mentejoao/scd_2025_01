public class CaixaBancaria extends Thread {
    private ContaBancaria conta;

    public CaixaBancaria(ContaBancaria conta) {
        this.conta = conta;
    }

    public void run() {
        conta.sacar(500.00);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        conta.depositar(500.00);
    }
}
