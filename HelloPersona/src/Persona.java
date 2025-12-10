import java.util.Objects;

public class Persona {
    private String nome;
    private int eta;
    private String AVS;

    public Persona(String nome, int eta, String AVS) {
        this.nome = nome;
        this.eta = eta;
        this.AVS = AVS;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return eta == persona.eta && Objects.equals(nome, persona.nome) && Objects.equals(AVS, persona.AVS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, eta, AVS);
    }
}
