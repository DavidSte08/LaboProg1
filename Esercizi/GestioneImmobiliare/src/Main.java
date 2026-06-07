public class Main {
    public static void main(String[] args) {

        Stanza s1 = new Stanza("stanza 1",25);
        Casa c1 = new Casa();

        c1.aggiungiStanza("Soggiorno", -10);
        c1.aggiungiStanza(s1);

        c1.visualizzaStanze();
    }
}