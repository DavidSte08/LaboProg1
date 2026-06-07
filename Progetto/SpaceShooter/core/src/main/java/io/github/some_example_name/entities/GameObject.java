package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Classe base astratta per tutti gli oggetti di gioco.
 *
 * <p><strong>Ereditarietà:</strong> tutte le entità del gioco ({@link Player},
 * {@link Enemy}, {@link PowerUp}) e i proiettili estendono questa classe,
 * ereditandone posizione, dimensioni, sprite e metodi comuni.</p>
 *
 * <p><strong>Incapsulamento:</strong> i campi sono {@code protected} per
 * consentire l'accesso alle sottoclassi senza esporli pubblicamente.
 * I valori si leggono e si modificano tramite getter/setter.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public abstract class GameObject {

    /** Coordinata X nel mondo di gioco. */
    protected float x;

    /** Coordinata Y nel mondo di gioco. */
    protected float y;

    /** Larghezza dell'oggetto in pixel. */
    protected float width;

    /** Altezza dell'oggetto in pixel. */
    protected float height;

    /** Texture (sprite grafico) dell'oggetto. */
    protected Texture texture;

    /** Indica se l'oggetto è ancora attivo nel gioco. */
    protected boolean alive;

    /**
     * Costruisce un {@code GameObject} con posizione e dimensioni specificate.
     *
     * @param x      coordinata X iniziale
     * @param y      coordinata Y iniziale
     * @param width  larghezza in pixel
     * @param height altezza in pixel
     */
    public GameObject(float x, float y, float width, float height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
        this.alive  = true;
    }

    /**
     * Aggiorna la logica dell'oggetto per il frame corrente.
     * Ogni sottoclasse implementa il proprio comportamento.
     *
     * @param delta secondi trascorsi dall'ultimo frame
     */
    public abstract void update(float delta);

    /**
     * Disegna l'oggetto sullo schermo se è vivo e ha una texture.
     *
     * @param batch il {@link SpriteBatch} corrente
     */
    public void draw(SpriteBatch batch) {
        if (alive && texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Restituisce il rettangolo di collisione (bounding box) dell'oggetto.
     *
     * @return {@link Rectangle} che rappresenta l'area di collisione
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Libera la texture dalla memoria grafica.
     * Da chiamare quando l'oggetto non serve più.
     */
    public void dispose() {
        if (texture != null) texture.dispose();
    }

    // ── Getter / Setter ───────────────────────────────────────────────────

    /** @return coordinata X corrente */
    public float getX() { return x; }

    /** @return coordinata Y corrente */
    public float getY() { return y; }

    /** @return larghezza in pixel */
    public float getWidth() { return width; }

    /** @return altezza in pixel */
    public float getHeight() { return height; }

    /** @return {@code true} se l'oggetto è ancora attivo */
    public boolean isAlive() { return alive; }

    /**
     * Imposta lo stato di vita dell'oggetto.
     *
     * @param alive {@code false} per rimuovere l'oggetto dal gioco
     */
    public void setAlive(boolean alive) { this.alive = alive; }
}
