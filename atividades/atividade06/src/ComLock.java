public class ComLock {
    public static void main(String[] args) {
        ContaBancariaComLock conta = new ContaBancariaComLock(2603, 1000.0);
        Thread[] caixas = new Thread[26]; // 10 < x < 30

        for (int i = 0; i < caixas.length; i++) {
            caixas[i] = new CaixaBancarioComLock(conta);
            caixas[i].start();
        }

        for (int i = 0; i < caixas.length; i++) {
            try {
                caixas[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Saldo final (com lock): R$ %.2f\n", conta.getSaldo());
    }
}
