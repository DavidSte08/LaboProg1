import java.util.ArrayList;

public class Carrello {
    private String nome;
    private ArrayList<Alimento> alimenti = new ArrayList<>();

    public Carrello(String nome) {
        this.nome = nome;
        alimenti = new ArrayList<>();
    }

    public void AggiungiAlimento(Alimento alimento) {
        alimenti.add(alimento);
    }

    public void AggiungiAlimento(String nome, String categoria, String dataScadenza, int qt){
        alimenti.add(new Alimento(nome, categoria, dataScadenza, qt));
    }

    public void RimuoviAlimento(Alimento alimento){
        alimenti.remove(alimento);
    }


}
