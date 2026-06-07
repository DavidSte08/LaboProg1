package ch.samt.biblioteca.data;

import ch.samt.biblioteca.model.ItemBiblioteca;
import ch.samt.biblioteca.model.Libro;

import java.util.*;

public class Biblioteca {
    private String nomeBiblioteca;
    ArrayList<ItemBiblioteca> catalogo = new ArrayList<>();
    Set<String> codiciUsati = new HashSet<>();
    Map<String, ArrayList<ItemBiblioteca>> elementiPerAutore = new HashMap<>();

    public Biblioteca(String nomeBiblioteca) {
        this.nomeBiblioteca = nomeBiblioteca;
    }

    public boolean aggiungiItem(ItemBiblioteca item) {

        if (codiciUsati.contains(item.getCodice())) {
            return false;
        }
        else {
            codiciUsati.add(item.getCodice());
            catalogo.add(item);

        }
        if (item instanceof Libro) {
            Libro libro = (Libro) item;
            String autore = libro.getAutore();
            ArrayList<ItemBiblioteca> listaAutore = elementiPerAutore.get(autore);
        if (listaAutore == null) {
            listaAutore = new ArrayList<>();
            elementiPerAutore.put(autore,listaAutore);
        }
        listaAutore.add(item);
        }
        return true;

    }

    ArrayList<ItemBiblioteca> getCatalogo() {
        return catalogo;
    }

    ArrayList<ItemBiblioteca> getElementiPerAutore(String autore) {
        return elementiPerAutore.get(autore);

    }

    @Override
    public String toString() {
        return "Biblioteca{" +
                "nomeBiblioteca='" + nomeBiblioteca + '\'' +
                ", catalogo=" + catalogo +
                ", codiciUsati=" + codiciUsati +
                ", elementiPerAutore=" + elementiPerAutore +
                '}';
    }
}
