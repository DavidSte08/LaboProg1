package ch.samt.dictionary;

public class Entry {
    private String parolaItaliano;
    private String parolaInglese;

    public Entry(String parolaItaliano, String parolaInglese) {
        this.parolaItaliano = parolaItaliano;
        this.parolaInglese = parolaInglese;
    }

    public String getParolaItaliano() {
        return parolaItaliano;
    }

    public void setParolaItaliano(String parolaItaliano) {
        this.parolaItaliano = parolaItaliano;
    }

    public String getParolaInglese() {
        return parolaInglese;
    }

    public void setParolaInglese(String parolaInglese) {
        this.parolaInglese = parolaInglese;
    }

    @Override
    public String toString() {
        return "ITA: " + parolaItaliano + " -> ING: " + parolaInglese;
    }

}
