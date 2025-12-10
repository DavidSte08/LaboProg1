package ch.samt.biblioteca.data;

import ch.samt.biblioteca.model.ItemBiblioteca;

import java.util.*;

public class Biblioteca {
    ArrayList<ItemBiblioteca> catalogo = new ArrayList<>();
    Set<String> codiciUsati = new HashSet<>();
    Map<String, ArrayList<ItemBiblioteca>> elementiPerAutore = new HashMap<>();

    public Biblioteca(ArrayList<ItemBiblioteca> catalogo, Set<String> codiciUsati, Map<String, ArrayList<ItemBiblioteca>> elementiPerAutore) {
        this.catalogo = catalogo;
        this.codiciUsati = codiciUsati;
        this.elementiPerAutore = elementiPerAutore;
    }

    public boolean aggiungiItem(ItemBiblioteca item) {
        if(codiciUsati.contains(item.getCodice())) {
            return false;
        }else {
            codiciUsati.add(item.getCodice());
            catalogo.add(item);
            return true;
        }
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
                "catalogo=" + catalogo +
                ", codiciUsati=" + codiciUsati +
                ", elementiPerAutore=" + elementiPerAutore +
                '}';
    }
}
