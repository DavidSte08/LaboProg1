//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Persona p1 = new Persona("Jon",17,"1010");
        Persona p2 = new Persona("Jon",17,"1010");

        System.out.println(p1.equals(p2));
    }
}