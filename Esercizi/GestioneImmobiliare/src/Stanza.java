public class Stanza {
    private String nome;
    private double superficieMq;

    public Stanza(String nome, double superficieMq){
        this.nome = nome;
        if (nome.isEmpty())
            System.out.println("Dai un nome alla stanza");

        this.superficieMq = superficieMq;
        if (superficieMq < 0 || superficieMq == 0)
            System.out.println("Inserisci una superficie valida alla stanza " + getNome());
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
