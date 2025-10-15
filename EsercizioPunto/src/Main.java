//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Punto punto1 = new Punto(5.0,-3.0);
        Punto punto2 = new Punto(2.0,4.0);
        Punto punto3 = new Punto();

        System.out.println("Il punto 1 si trova nel quadrante "+ punto1.determinaQuadrante());
        System.out.println("Il punto 2 si trova nel quadrante "+ punto2.determinaQuadrante());
        System.out.println("Il punto 3 si trova nel quadrante "+ punto3.determinaQuadrante());


        System.out.println("Distanza dal centro del punto 1: " + punto1.distanzaCentro());
        System.out.println("Distanza dal centro del punto 2: " + punto2.distanzaCentro());
        System.out.println("Distanza dal centro del punto 3: " + punto3.distanzaCentro());

        System.out.println(punto1.toString());
        System.out.println(punto2.toString());
        System.out.println(punto3.toString());

    }

}