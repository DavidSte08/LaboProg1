import java.util.ArrayList;

public class Casa {
    private ArrayList<Stanza> stanze = new ArrayList<>();

    public Casa() {
        stanze = new ArrayList<>();
    }

    public void addStanza(Stanza stanza) {
        stanze.add(stanza);
    }
    public void visualizzaStanza(Stanza stanza) {
        for (Stanza s : stanze) {
            System.out.println(s);
        }

    }

}
