import java.util.ArrayList;

public class Casa {
    private ArrayList<Stanza> stanze = new ArrayList<>();

    public Casa() {
        stanze = new ArrayList<>();
    }

    public void aggiungiStanza(String nome, double superficieMq) {
        Stanza nuova = new Stanza(nome, superficieMq);
        stanze.add(nuova);
    }

    public void aggiungiStanza(Stanza nuova) {
        stanze.add(nuova);
    }

    public void visualizzaStanze() {
        if (stanze.isEmpty()) {
            System.out.println("La casa è vuota.");
        } else {
            System.out.println("Elenco stanze della casa: ");
            for (Stanza s : stanze) {
                System.out.println(s.toString());
            }
        }
    }
}
