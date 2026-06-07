import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        int eta= 0;
        String nome = "";
        String cognome = "";
        Scanner input= new Scanner(System.in); // Creato oggetto scanner

        while(true) {
            try {
                System.out.println("Inserisci il tuo nome: ");
                nome = input.nextLine(); //Letto input da tastiera e salvato nella linea seguente
                System.out.println("Inserisci il tuo cognome: ");
                cognome = input.nextLine();
                System.out.println("Inserisci la tua età");
                eta = input.nextInt();
                break;
            } catch (InputMismatchException IME) {
                input.nextLine();
                System.out.println("Scrivi la tua età in formato numerico intero!");
            }
        }
        System.out.println("Ciao " + nome + " " + cognome + " Età= " + eta);
    }
}