import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContaBancariaComLock {
    private int numero;
    private double saldo;
    private Lock lock = new ReentrantLock();

    public ContaBancariaComLock(int numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = saldoInicial;
    }

    public void sacar(double valor) {
        lock.lock();
        try {
            saldo -= valor;
        } finally {
            lock.unlock();
        }
    }

    public void depositar(double valor) {
        lock.lock();
        try {
            saldo += valor;
        } finally {
            lock.unlock();
        }
    }

    public double getSaldo() {
        return saldo;
    }
}
