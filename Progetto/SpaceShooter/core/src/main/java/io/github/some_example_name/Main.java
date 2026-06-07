package io.github.some_example_name;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.screens.MenuScreen;
import io.github.some_example_name.utils.AudioManager;
import io.github.some_example_name.utils.ScoreManager;

/**
 * Classe principale del gioco Space Shooter.
 *
 * <p>Estende {@link Game} di LibGDX e gestisce il ciclo di vita dell'applicazione.
 * Inizializza le risorse condivise tra le schermate ({@link SpriteBatch},
 * {@link AudioManager}, {@link ScoreManager}) e avvia il menu principale.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class Main extends Game {

    /** Batch condiviso per il rendering degli sprite in tutte le schermate. */
    public SpriteBatch batch;

    /** Gestore centralizzato dell'audio (musica + effetti sonori). */
    public AudioManager audioManager;

    /** Gestore del punteggio massimo con persistenza su file. */
    public ScoreManager scoreManager;

    /**
     * Inizializza il gioco: crea le risorse condivise e avvia la schermata del menu.
     * Chiamato automaticamente da LibGDX all'avvio dell'applicazione.
     */
    @Override
    public void create() {
        batch        = new SpriteBatch();
        audioManager = new AudioManager();
        scoreManager = new ScoreManager();
        scoreManager.load();
        setScreen(new MenuScreen(this));
    }

    /**
     * Aggiorna e renderizza il frame corrente delegando alla schermata attiva.
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Notifica la schermata corrente del ridimensionamento della finestra.
     *
     * @param width  nuova larghezza in pixel
     * @param height nuova altezza in pixel
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    /**
     * Libera tutte le risorse allocate alla chiusura del gioco.
     */
    @Override
    public void dispose() {
        batch.dispose();
        audioManager.dispose();
        super.dispose();
    }
}
