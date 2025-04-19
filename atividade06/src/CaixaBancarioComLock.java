public class CaixaBancario extends Thread {
    private ContaBancariaComLock conta;

    public CaixaBancario(ContaBancariaComLock conta) {
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
