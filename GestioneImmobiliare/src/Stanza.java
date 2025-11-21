public class Stanza {
    private String nome;
    private double superficieMq;

    public Stanza(String nome, double superficieMq){
        this.nome = nome;
        this.superficieMq = superficieMq;
    }

    public String getNome() {
        return nome;
    }

    public double getSuperficieMq() {
        return superficieMq;
    }

    @Override
    public String toString() {
        return "Stanza{" +
                "nome='" + nome + '\'' +
                ", superficieMq=" + superficieMq +
                '}';
    }

}
