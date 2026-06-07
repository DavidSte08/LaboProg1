import java.util.Random;
public class Matrice {
private int righe;
private int colonne;

    public Matrice() {
        righe = 5;
        colonne = 5;
        System.out.println("Default");
        System.out.println("Matrice: righe = " + righe);
        System.out.println("Matrice: colonne = " + colonne);
        System.out.println();
    }

    public Matrice(int righe, int colonne) {
        Random r = new Random();
        this.righe = righe;
        this.colonne = colonne;

        float [][] matrice;
        matrice = new float[righe][colonne];

        System.out.println("Matrice: righe = " + righe);
        System.out.println("Matrice: colonne = " + colonne);
        System.out.println();

        for(int i = 0; i < matrice.length; i++){
            for(int j = 0; j < matrice[i].length; j++){
                matrice[i][j]= r.nextInt(2);
                System.out.print(matrice[i][j] + " ");
            }
            System.out.println();
        }

    }

    public int getRighe() {
        return righe;
    }

    public int getColonne() {
        return colonne;
    }



}
