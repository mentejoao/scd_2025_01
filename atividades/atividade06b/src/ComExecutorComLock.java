import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComExecutorComLock {
    public static void main(String[] args) {
        ContaBancariaComLock conta = new ContaBancariaComLock(2603, 10000.0);
        int numCaixas = 26;

        ExecutorService executor = Executors.newFixedThreadPool(numCaixas);

        for (int i = 0; i < numCaixas; i++) {
            executor.execute(new CaixaBancarioComLock(conta));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        System.out.printf("Saldo final (com Executor e synchronized): R$ %.2f\n", conta.getSaldo());
    }
}
