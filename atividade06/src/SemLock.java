public class SemLock {
    public static void main(String[] args) {
        ContaBancaria conta = new ContaBancaria(2603, 10000.0);
        Thread[] caixas = new Thread[26]; // 10 < x < 30

        for (int i = 0; i < caixas.length; i++) {
            caixas[i] = new CaixaBancario(conta);
            caixas[i].start();
        }

        for (int i = 0; i < caixas.length; i++) {
            try {
                caixas[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Saldo final (sem lock): R$ %.2f\n", conta.getSaldo());
    }
}
