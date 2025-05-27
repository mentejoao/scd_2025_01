package bloqueios;

public class Produtor implements Runnable {

    private MinhaPilha pilha;
    private String[] listaDeCompras = {"Arroz", "Feijão", "Café", "Açúcar", "Leite", "Ovos", "Pão", "Queijo", "Presunto", "Manteiga",
            "Frutas (maçã, banana, laranja)", "Verduras (alface, tomate, cebola)", "Carne (frango, boi, porco)", "Macarrão", "Molho de tomate",
            "Óleo", "Sal", "Tempero", "Bolacha", "Suco", "Iogurte", "Cereal", "Pão de forma", "Farinha de trigo",
            "Fermento", "Chocolate", "Biscoito", "Água", "Refrigerante", "Papel higiênico", "Sabão em pó", "Detergente",
            "Esponja", "Pano de chão", "Vassoura", "Rodo", "Saco de lixo", "Pasta de dente", "Escova de dente", "Shampoo",
            "Condicionador", "Sabonete", "Desodorante", "Guardanapo", "Papel toalha", "Pilhas", "Lâmpada", "Fósforo", "Vela",
            "Inseticida"};


    public Produtor(MinhaPilha pilha) {
        this.pilha = pilha;
    }

    @Override
    public void run() {
        for(String item : listaDeCompras) {
            try {
                pilha.adicionar(item);
                Thread.sleep((int) (Math.random()*500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}