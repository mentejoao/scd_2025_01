import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*3. Cinco lebres disputarão uma corrida. Cada lebre pode dar saltos que variam de 1 a 3 metros de distância. A distância percorrida é de 20 metros. Na corrida, para cada lebre que dá um salto, deve ser informado quantos metros ela pulou. Em seguida, a lebre para de saltar e descansa (yield()).

a) Escreva um programa, utilizando threads (uma para cada lebre)

b) Informe a lebre vencedora e a colocação de cada uma delas no final da corrida.

c) Informar também a quantidade de pulos de cada lebre.*/

public class exercicio03 {

    private static final int DISTANCIA_TOTAL = 20;
    private static final int NUM_LEBRES = 5;

    // Usado para registrar a ordem de chegada
    private static final List<String> colocacoes = new CopyOnWriteArrayList<>();

    // Classe da lebre (Thread)
    static class Lebre extends Thread {
        private final String nome;
        private final Random random = new Random();
        private int distanciaPercorrida = 0;
        private int pulos = 0;

        public Lebre(String nome) {
            this.nome = nome;
        }

        public void run() {
            while (distanciaPercorrida < DISTANCIA_TOTAL) {
                int pulo = random.nextInt(3) + 1; // de 1 a 3 metros
                distanciaPercorrida += pulo;
                pulos++;
                System.out.printf("🐇 %s pulou %d metros (Total: %d)\n", nome, pulo,
                        Math.min(distanciaPercorrida, DISTANCIA_TOTAL));
                Thread.yield(); // descansa
                try {
                    Thread.sleep(100); // simula o tempo de descanso
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            colocacoes.add(nome + " (" + pulos + " pulos)");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Lebre[] lebres = new Lebre[NUM_LEBRES];

        // Cria e inicia as threads
        for (int i = 0; i < NUM_LEBRES; i++) {
            lebres[i] = new Lebre("Lebre " + (i + 1));
            lebres[i].start();
        }

        // Aguarda todas finalizarem
        for (Lebre lebre : lebres) {
            lebre.join();
        }

        // Exibe o resultado
        System.out.println("\n🏁 Resultado da Corrida:");
        for (int i = 0; i < colocacoes.size(); i++) {
            String resultado = colocacoes.get(i);
            if (i == 0) {
                System.out.printf("🥇 1º lugar: %s\n", resultado);
            } else if (i == 1) {
                System.out.printf("🥈 2º lugar: %s\n", resultado);
            } else if (i == 2) {
                System.out.printf("🥉 3º lugar: %s\n", resultado);
            } else {
                System.out.printf("   %dº lugar: %s\n", i + 1, resultado);
            }
        }
    }
}
