public class CaixaBancarioComLock implements Runnable {
    private ContaBancariaComLock conta;

    public CaixaBancarioComLock(ContaBancariaComLock conta) {
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
