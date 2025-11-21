public class Alimento {
    private String nome;
    private String categoria;
    private String dataScadenza;
    private int qt;

    public Alimento(String nome, String categoria, String dataScadenza, int qt) {
        this.nome = nome;
        this.categoria = categoria;
        this.dataScadenza = dataScadenza;
        this.qt = qt;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public int getQt() {
        return qt;
    }

    public void setQt(int qt) {
        this.qt = qt;
    }

    @Override
    public String toString() {
        return "Alimento{" +
                "nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", dataScadenza='" + dataScadenza + '\'' +
                ", qt=" + qt +
                '}';
    }
}
