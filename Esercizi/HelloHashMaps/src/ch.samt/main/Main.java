package ch.samt.main;

import ch.samt.dictionary.Dictionary;
import ch.samt.dictionary.Entry;

public class Main {
    public static void main(String[] args) {

        Dictionary d = new Dictionary();

        d.aggiungi(new Entry("Cane", "Dog"));
        d.aggiungi(new Entry("Gatto", "Cat"));

        System.out.println(d.cerca("Gatto"));

        d.stampaTutto();
    }
}