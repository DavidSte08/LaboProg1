import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        Random rand = new Random();

        int numero = 0;
        int numeroGenerato= rand.nextInt(101);
        int conta = 0;

        System.out.print("Inserisci un numero: ");
        numero = input.nextInt();
            while(true) {
            if (numero == numeroGenerato) {
                conta = conta + 1;
                System.out.println("Numero indovinato al " + conta + " tentativo");
                break;

            } else if (numero < numeroGenerato) {
                System.out.println("Numero troppo basso");
                System.out.print("Inserisci un numero: ");
                numero = input.nextInt();
                conta = conta + 1;

            } else if (numero > numeroGenerato) {
                System.out.println("Numero troppo alto");
                System.out.print("Inserisci un numero: ");
                numero = input.nextInt();
                conta = conta + 1;
            }
        }
    }
}