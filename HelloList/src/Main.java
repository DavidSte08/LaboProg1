public class Main {
    public static void main(String[] args) {
        Carrello c1 = new Carrello("spesa");

        Alimento spaghetti = new Alimento("Spaghetti","Pasta","01.06.2026",4);

        c1.AggiungiAlimento(spaghetti);
        c1.AggiungiAlimento("Latte","Latte","20.01.2026",1);

    }
}