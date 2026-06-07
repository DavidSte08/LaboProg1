package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Power-up raccoglibile che cade verso il basso dopo la morte di un nemico.
 *
 * <p>Tipi disponibili:</p>
 * <ul>
 *   <li>{@link Type#LIFE}  — aggiunge subito una vita (max 5)</li>
 *   <li>{@link Type#SPEED} — va in inventory slot 1; premi Q per attivare il doppio sparo</li>
 *   <li>{@link Type#SLOW}  — va in inventory slot 2; premi E per rallentare tutti i nemici</li>
 * </ul>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class PowerUp extends GameObject {

    /** Tipi di power-up disponibili. */
    public enum Type {
        /** Vita extra immediata. */
        LIFE,
        /** Doppio sparo — va in inventory, si attiva con Q. */
        SPEED,
        /** Rallenta tutti i nemici — va in inventory, si attiva con E. */
        SLOW
    }

    private final Type  type;
    private static final float FALL_SPEED = 130f;

    /**
     * Crea un power-up del tipo specificato.
     *
     * @param x    coordinata X
     * @param y    coordinata Y
     * @param type tipo di power-up
     */
    public PowerUp(float x, float y, Type type) {
        super(x, y, 36f, 36f);
        this.type = type;
        String img;
        switch (type) {
            case LIFE:  img = "images/powerup_life.png";  break;
            case SPEED: img = "images/powerup_speed.png"; break;
            default:    img = "images/powerup_slow.png";  break;
        }
        try { texture = new Texture(Gdx.files.internal(img)); }
        catch (Exception e) { Gdx.app.error("PowerUp", img + ": " + e.getMessage()); }
    }

    /**
     * Fa cadere il power-up verso il basso ed elimina se esce dallo schermo.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void update(float delta) {
        y -= FALL_SPEED * delta;
        if (y + height < 0f) alive = false;
    }

    /**
     * Applica l'effetto al giocatore e rimuove il power-up dalla scena.
     *
     * @param player il giocatore su cui applicare l'effetto
     */
    public void apply(Player player) {
        switch (type) {
            case LIFE:  player.addLife();     break;
            case SPEED: player.collectSpeed(); break;
            case SLOW:  player.collectSlow();  break;
        }
        alive = false;
    }

    /** @return tipo del power-up */
    public Type getType() { return type; }
}
