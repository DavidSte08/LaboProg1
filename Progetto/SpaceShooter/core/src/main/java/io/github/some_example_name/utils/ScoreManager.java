package io.github.some_example_name.utils;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Gestore del punteggio massimo con persistenza su file locale.
 *
 * <p>Usa {@link FileHandle} di LibGDX per leggere e scrivere l'highscore
 * in un file di testo ({@code highscore.txt}) nella cartella locale
 * dell'applicazione. Soddisfa il criterio <strong>R/W File</strong>.</p>
 *
 * <p>Tutte le operazioni I/O sono protette da {@code try-catch}:
 * un file corrotto o mancante non causa crash ma resetta il valore a 0.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class ScoreManager {

    /** Nome del file di salvataggio. */
    private static final String FILE = "highscore.txt";

    /** Punteggio massimo corrente. */
    private int highScore;

    /** Crea un nuovo ScoreManager con highscore iniziale a 0. */
    public ScoreManager() { this.highScore = 0; }

    /**
     * Carica l'highscore dal file locale.
     * In caso di file mancante o dati non validi il valore rimane 0.
     */
    public void load() {
        try {
            FileHandle fh = Gdx.files.local(FILE);
            if (fh.exists()) {
                highScore = Integer.parseInt(fh.readString().trim());
                Gdx.app.log("ScoreManager", "Highscore caricato: " + highScore);
            }
        } catch (NumberFormatException e) {
            Gdx.app.error("ScoreManager", "File corrotto — reset a 0");
            highScore = 0;
        } catch (Exception e) {
            Gdx.app.error("ScoreManager", "Errore lettura: " + e.getMessage());
        }
    }

    /**
     * Salva l'highscore corrente sul file locale.
     * In caso di errore I/O viene registrato un log senza crash.
     */
    public void save() {
        try {
            Gdx.files.local(FILE).writeString(String.valueOf(highScore), false);
            Gdx.app.log("ScoreManager", "Highscore salvato: " + highScore);
        } catch (Exception e) {
            Gdx.app.error("ScoreManager", "Errore scrittura: " + e.getMessage());
        }
    }

    /**
     * Confronta il punteggio con l'highscore e lo aggiorna se è maggiore.
     *
     * @param score punteggio da confrontare
     * @return {@code true} se è stato stabilito un nuovo record
     */
    public boolean submitScore(int score) {
        if (score > highScore) {
            highScore = score;
            save();
            return true;
        }
        return false;
    }

    /**
     * Azzera l'highscore e salva il valore zero sul file.
     */
    public void reset() { highScore = 0; save(); }

    /**
     * Restituisce il punteggio massimo corrente.
     *
     * @return highscore
     */
    public int getHighScore() { return highScore; }
}
