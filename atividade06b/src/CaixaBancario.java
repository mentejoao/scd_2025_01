public class CaixaBancario implements Runnable {
    private ContaBancaria conta;

    public CaixaBancario(ContaBancaria conta) {
        this.conta = conta;
    }

    @Override
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
