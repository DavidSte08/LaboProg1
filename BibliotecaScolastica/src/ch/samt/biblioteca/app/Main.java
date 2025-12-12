package ch.samt.biblioteca.app;

import ch.samt.biblioteca.data.Biblioteca;
import ch.samt.biblioteca.model.Dvd;
import ch.samt.biblioteca.model.Libro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {

        Biblioteca biblioteca = new Biblioteca("Biblioteca1");

        Libro Libro1 = new Libro("L1","Libro111",2025,"1","Jon",99);
        Libro Libro2 = new Libro("L2","Libro222",2020,"2","Jon",99);
        Dvd Dvd1 = new Dvd("D1","DVD1",2025,"1","Tom",3);

        System.out.println(biblioteca.aggiungiItem(Libro1));
        System.out.println(biblioteca.aggiungiItem(Libro1)); //Aggiunta duplicato
        System.out.println(biblioteca.aggiungiItem(Libro2));
        System.out.println(biblioteca.aggiungiItem(Dvd1));

        System.out.println(biblioteca);

    }
}