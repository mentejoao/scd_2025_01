public class ContaBancaria {
    private int numero;
    private double saldo;

    public ContaBancaria(int numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = saldoInicial;
    }

    public synchronized void sacar(double valor) {
        saldo -= valor;
    }

    public synchronized void depositar(double valor) {
        saldo += valor;
    }

    public double getSaldo() {
        return saldo;
    }
}
