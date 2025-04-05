import java.util.*;
import java.util.concurrent.*;

/*1. Faça um programa em Java que identifique e escreva quantos e quais os números primos:

a) entre 1.000.000 e 7.000.000
b) entre 25.000.000 e 45.000.000
c) registre o tempo total gasto para realizar os cálculo e escreva este tempo no final.*/



public class exercicio01 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Informe a quantidade de threads: ");
        int numThreads = sc.nextInt();

        long inicioTotal = System.currentTimeMillis();

        executarIntervalo(1_000_000, 7_000_000, numThreads, "A");
        executarIntervalo(25_000_000, 45_000_000, numThreads, "B");

        long fimTotal = System.currentTimeMillis();
        double tempoTotal = (fimTotal - inicioTotal) / 1000.0;
        System.out.printf("\n⏱ Tempo TOTAL de execução: %.2f segundos\n", tempoTotal);

        sc.close();
    }

    public static void executarIntervalo(int inicio, int fim, int numThreads, String nomeIntervalo)
            throws InterruptedException, ExecutionException {

        System.out.printf("\n🔍 Intervalo %s) Números primos entre %,d e %,d:\n", nomeIntervalo, inicio, fim);

        long inicioIntervalo = System.currentTimeMillis();

        int tamanhoIntervalo = fim - inicio + 1;
        int passo = tamanhoIntervalo / numThreads;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<List<Integer>>> resultados = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int inicioLocal = inicio + i * passo;
            int fimLocal = (i == numThreads - 1) ? fim : inicioLocal + passo - 1;

            resultados.add(executor.submit(() -> encontrarPrimos(inicioLocal, fimLocal)));
        }

        List<Integer> primosTotais = new ArrayList<>();
        for (Future<List<Integer>> futuro : resultados) {
            primosTotais.addAll(futuro.get());
        }

        executor.shutdown();

        long fimIntervalo = System.currentTimeMillis();
        double tempoIntervalo = (fimIntervalo - inicioIntervalo) / 1000.0;

        System.out.println("✔️ Total de primos encontrados: " + primosTotais.size());
        System.out.printf("⏱ Tempo para o intervalo %s: %.2f segundos\n", nomeIntervalo, tempoIntervalo);
    }

    public static List<Integer> encontrarPrimos(int inicio, int fim) {
        List<Integer> primos = new ArrayList<>();
        for (int i = inicio; i <= fim; i++) {
            if (isPrime(i)) {
                primos.add(i);
            }
        }
        return primos;
    }

    public static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;

        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }

        return true;
    }
}
