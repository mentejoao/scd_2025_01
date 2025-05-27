public class ContagemComSleep {
    public static void main(String[] args) throws InterruptedException {
        Thread contador = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 1; i <= 10; i++) {
                        System.out.println("Contando: " + i);
                        Thread.sleep(1000);
                    }
                    System.out.println("Contagem concluída!");
                } catch (InterruptedException e) {
                    System.out.println("Contador interrompido durante a contagem!");
                }
            }
        });

        contador.start();

        Thread.sleep(4000);
        System.out.println("Main thread vai interromper o contador!");
        contador.interrupt();
    }
}
