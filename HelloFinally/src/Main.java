public class Main {
    public static void main(String[] args) {
        //Cicliamo args per verificare se sono numeri
        for(String arg : args) {
            try {
                double numero = Double.parseDouble(arg);
                System.out.println(numero);
            }catch(NumberFormatException nfe) {
                System.out.println("Non è un numero valido");
            }finally {
                System.out.println("Operazione andata a buon fine");
            }
        }
    }
}