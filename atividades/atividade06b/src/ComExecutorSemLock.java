import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComExecutorSemLock {
    public static void main(String[] args) {
        ContaBancaria conta = new ContaBancaria(2603, 10000.0);
        int numCaixas = 26;

        ExecutorService executor = Executors.newFixedThreadPool(numCaixas);

        for (int i = 0; i < numCaixas; i++) {
            executor.execute(new CaixaBancario(conta));
        }

        executor.shutdown();
        while (!executor.isTerminated()){
        }

        System.out.printf("Saldo final (com Executor, sem lock): R$ %.2f\n", conta.getSaldo());
    }
}
