package ch.samt.biblioteca.model;

import java.util.Objects;

public class ItemBiblioteca {
    private String codice;
    private String titolo;
    private int annoPubblicazione;
    protected String scaffale;

    public ItemBiblioteca(String codice, String titolo, int annoPubblicazione, String scaffale) {
        this.codice = codice;
        this.titolo = titolo;
        this.annoPubblicazione = annoPubblicazione;
        this.scaffale = scaffale;
    }

    public String getCodice() {
        return codice;
    }

    public String getTitolo() {
        return titolo;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public String getScaffale() {
        return scaffale;
    }

    public void setScaffale(String scaffale) {
        this.scaffale = scaffale;
    }

    @Override
    public String toString() {
        return "ItemBiblioteca{" +
                "codice='" + codice + '\'' +
                ", titolo='" + titolo + '\'' +
                ", annoPubblicazione=" + annoPubblicazione +
                ", scaffale='" + scaffale + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemBiblioteca that = (ItemBiblioteca) o;
        return annoPubblicazione == that.annoPubblicazione && Objects.equals(codice, that.codice) && Objects.equals(titolo, that.titolo) && Objects.equals(scaffale, that.scaffale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codice, titolo, annoPubblicazione, scaffale);
    }
}
