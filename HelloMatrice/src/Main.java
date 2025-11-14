public class Main {
    public static void main(String[] args) {
        float [][] matrice;
        matrice = new float[3][3];
        for(int i = 0; i < matrice.length; i++){
            for(int j = 0; j < matrice[i].length; j++){
                matrice[i][j]= 1;
                System.out.println(matrice[i][j]);
            }
            System.out.println();
        }
    }
}