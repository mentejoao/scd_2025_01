public class ContagemInterrupted {
    public static void main(String[] args) throws InterruptedException {
        Thread contador = new Thread(new Runnable() {
            public void run() {
                int i = 1;
                while (true) {
                    System.out.println("Contando: " + i);
                    i++;

                    if (Thread.interrupted()) {
                        System.out.println("Contador detectou interrupção! Encerrando...");
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Contador foi interrompido durante o sleep!");
                        return;
                    }
                }
            }
        });

        contador.start();

        Thread.sleep(4000);
        System.out.println("Main thread vai interromper o contador!");
        contador.interrupt();
    }
}
