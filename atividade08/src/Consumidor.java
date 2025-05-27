package bloqueios;

public class Consumidor implements Runnable {

    private MinhaPilha pilha;

    public Consumidor(MinhaPilha pilha) {
        this.pilha = pilha;
    }

    @Override
    public void run() {
        for(;;) {
            try {
                System.out.println(Thread.currentThread().getName() + " - " + pilha.retirar());
                Thread.sleep((int) (Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}