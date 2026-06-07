public class Main {
    public static void main(String[] args) {

        for (String codice : args) {
            try {
                String Prod = codice.substring(0,4);
                Prod.equals("PROD-");
                System.out.println("Codice valido "+codice);
            }catch(Exception e) {
                System.out.println("Codice non valido: " + codice);
                System.out.println("Motivo: Il codice deve iniziare con 'PROD");
            }
            try {
                String dopoProd = codice.substring(5,9);
                dopoProd = dopoProd.substring(0, dopoProd.length()-1);
            }catch(Exception e) {
                System.out.println("Codice non valido: " + codice);
            }
        }
    }
}