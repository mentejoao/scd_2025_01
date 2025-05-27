package bloqueios;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestarBloqueio {

    public static void main(String[] args) {

        MinhaPilha mp = new MinhaPilha();


        ExecutorService es = Executors.newCachedThreadPool();
        es.submit( new Consumidor(mp));
        es.submit( new Produtor(mp));
        es.submit( new Consumidor(mp));
        es.submit( new Produtor(mp));

        es.shutdown();
    }

}















/*
 *
 *         String[] listaDeCompras = {"Arroz", "Feijão", "Café", "Açúcar", "Leite", "Ovos", "Pão", "Queijo", "Presunto", "Manteiga",
                                   "Frutas (maçã, banana, laranja)", "Verduras (alface, tomate, cebola)", "Carne (frango, boi, porco)", "Macarrão", "Molho de tomate",
                                   "Óleo", "Sal", "Tempero", "Bolacha", "Suco", "Iogurte", "Cereal", "Pão de forma", "Farinha de trigo",
                                   "Fermento", "Chocolate", "Biscoito", "Água", "Refrigerante", "Papel higiênico", "Sabão em pó", "Detergente",
                                   "Esponja", "Pano de chão", "Vassoura", "Rodo", "Saco de lixo", "Pasta de dente", "Escova de dente", "Shampoo",
                                   "Condicionador", "Sabonete", "Desodorante", "Guardanapo", "Papel toalha", "Pilhas", "Lâmpada", "Fósforo", "Vela",
                                   "Inseticida"};

 */