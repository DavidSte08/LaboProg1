public class Punto {

    double x;
    double y;

    public Punto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Punto() {
        this.x = -5.0;
        this.y = -2.0;
    }

    public double distanzaCentro(){
        double distanza = Math.sqrt((x*x + y*y));
        int distanzaInt = (int) distanza;
        return distanzaInt;
    }

    public String determinaQuadrante(){
        if(x > 0 && y > 0) {
            int Quad = 1;
            return Quad+"";
        }else if(x < 0 && y > 0) {
            int Quad = 2;
            return Quad+"";
        }else if(x < 0 && y < 0) {
            int Quad = 3;
            return Quad+"";
        }else {
            int Quad = 4;
            return Quad+"";
        }
    }

    @Override
    public String toString() {
        return "Punto: {" + "x=" + this.x + ", y=" + this.y + "}"+
                " Distanza dal centro: " + distanzaCentro()+ " Quadrante: "+ determinaQuadrante();
    }

}
