package bloqueios;

import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MinhaPilha {

    Stack<String> pilha = new Stack<String>();
    final int MAXIMO = 10;


    public void adicionar(String item) throws InterruptedException {

        Lock bloqueio = new ReentrantLock();
        Condition cheio = bloqueio.newCondition();

        bloqueio.lock();
        while (pilha.size() == MAXIMO)
            cheio.await();

        try {
            pilha.push(item);
        } finally{
            bloqueio.unlock();
        }
    }

    public String retirar() throws InterruptedException {
        Lock bloqueio = new ReentrantLock();
        Condition vazio = bloqueio.newCondition();

        bloqueio.lock();
        while(pilha.size() == 0)
            vazio.await();
        try {
            return pilha.pop();
        }finally {
            bloqueio.unlock();
        }
    }
}