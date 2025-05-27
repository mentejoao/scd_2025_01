import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*2. Escreva uma classe que permita paralelizar uma pesquisa em um array de inteiros. Isso deve ser feito com o seguinte método:

public static int parallelSearch(int x, int[] A, int numThreads).

a) Este método cria tantas threads quanto especificadas em numThreads, divide o array A em muitas partes e dá a cada thread parte do array para procurar sequencialmente pelo valor x.

b) Se uma thread encontrar o valor x, então é retornado o índice i (A[i]=x), ao contrário -1.*/

public class exercicio02 {

    public static int parallelSearch(int x, int[] A, int numThreads) throws InterruptedException {
        int length = A.length;
        numThreads = Math.min(numThreads, length); // ✅ Limita o número de threads

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger resultIndex = new AtomicInteger(-1);
        AtomicBoolean found = new AtomicBoolean(false);
        int chunkSize = (int) Math.ceil((double) length / numThreads);

        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, length);

            executor.execute(() -> {
                for (int j = start; j < end && !found.get(); j++) {
                    if (A[j] == x) {
                        if (found.compareAndSet(false, true)) {
                            resultIndex.set(j);
                        }
                        break;
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdownNow();

        return resultIndex.get();
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite a quantidade de elementos no array: ");
        int tamanho = scanner.nextInt();
        int[] vetor = new int[tamanho];

        System.out.println("Digite os " + tamanho + " números inteiros separados por espaço:");
        for (int i = 0; i < tamanho; i++) {
            vetor[i] = scanner.nextInt();
        }

        System.out.print("Digite o valor a ser procurado: ");
        int valorProcurado = scanner.nextInt();

        System.out.print("Digite o número de threads a serem usadas: ");
        int numThreads = scanner.nextInt();

        if (numThreads > tamanho) {
            System.out.printf("Número de threads (%d) maior que o tamanho do array (%d). Ajustando...\n", numThreads, tamanho);
        }

        long inicio = System.currentTimeMillis();
        int indice = parallelSearch(valorProcurado, vetor, numThreads);
        long fim = System.currentTimeMillis();

        if (indice != -1) {
            System.out.printf("✅ Valor %d encontrado no índice %d\n", valorProcurado, indice);
        } else {
            System.out.printf("❌ Valor %d não encontrado\n", valorProcurado);
        }

        System.out.printf("Tempo de busca: %.2f segundos\n", (fim - inicio) / 1000.0);

        scanner.close();
    }
}
