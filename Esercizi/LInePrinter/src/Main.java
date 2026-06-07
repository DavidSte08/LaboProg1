//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int length = 0;
        int direction = 0;
        if (args.length <2) {
            System.out.println("You need to provide two arguments!");
            return;
        }
        try {
            length = Integer.parseInt(args[0]);
            direction = Integer.parseInt(args[1]);

            if (length < 1 || direction < 1) {
                System.out.println("You need to provide a positive integer!");
                return;
            }

        }catch(NumberFormatException e) {
            System.out.println("You need to provide a number!");
        }
        for (int i = 0; i <length ; i++) {
            if(direction==1) {
                System.out.print("*\t");
            }else{
                System.out.println("*");
            }
        }
    }
}