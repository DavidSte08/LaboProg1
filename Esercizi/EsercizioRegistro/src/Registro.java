import java.util.Scanner;
import java.util.Random;


public class Registro {
    Scanner input = new Scanner(System.in);

    String[] studenti;
    Double[][] voti = new Double[studenti.length][0];

    public Registro(String[] studenti, Double[][] voti) {

        this.studenti = studenti;
        this.voti = voti;

    }

    public String[] inserimentoVoti() {
        System.out.println("Inserisci i voti di un studente:");


    }
}
