package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.weapons.Bullet;

/**
 * Nemico base con movimento sinusoidale e sparo singolo.
 *
 * <p>Scende verticalmente oscillando orizzontalmente con una sinusoide.
 * È il nemico più comune: vale 100 punti e ha 1 punto vita.</p>
 *
 * <p>Supporta una <strong>tinta colore</strong> impostabile alla creazione
 * per variare visivamente i nemici nelle diverse formazioni, senza duplicare
 * la texture.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 * @see Enemy
 */
public class EnemyBasic extends Enemy {

    private static final float SPEED_Y   = 58f;
    private static final float AMPLITUDE = 65f;
    private static final float FREQUENCY = 1.4f;

    private float timeAccum;
    private final float startX;

    /** Tinta R (0-1) applicata al disegno della texture. */
    private final float tintR;
    /** Tinta G (0-1). */
    private final float tintG;
    /** Tinta B (0-1). */
    private final float tintB;

    /**
     * Crea un nemico base con tinta bianca (aspetto standard).
     *
     * @param x coordinata X iniziale
     * @param y coordinata Y iniziale
     */
    public EnemyBasic(float x, float y) {
        this(x, y, 1f, 1f, 1f);
    }

    /**
     * Crea un nemico base con tinta colore personalizzata.
     *
     * @param x  coordinata X iniziale
     * @param y  coordinata Y iniziale
     * @param r  componente rossa (0-1)
     * @param g  componente verde (0-1)
     * @param b  componente blu (0-1)
     */
    public EnemyBasic(float x, float y, float r, float g, float b) {
        super(x, y, 48f, 48f, 1, 100);
        this.startX     = x;
        this.timeAccum  = 0f;
        this.tintR      = r;
        this.tintG      = g;
        this.tintB      = b;
        this.shootInterval = 2.8f;
        this.shootTimer    = (float)(Math.random() * shootInterval);

        try {
            texture = new Texture(Gdx.files.internal("images/enemy_basic.png"));
        } catch (Exception e) {
            Gdx.app.error("EnemyBasic", "enemy_basic.png: " + e.getMessage());
        }
    }

    /**
     * Muove il nemico verso il basso con oscillazione sinusoidale.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void move(float delta) {
        timeAccum += delta;
        y -= SPEED_Y * delta * slowMult;
        x  = startX + (float) Math.sin(timeAccum * FREQUENCY) * AMPLITUDE;
        // Clamp ai bordi: non esce mai lateralmente
        x = Math.max(0f, Math.min(x, 720f - width));
        if (y + height < 0f) alive = false;
    }

    /**
     * Spara un singolo proiettile verso il basso.
     */
    @Override
    public void shoot() {
        bullets.add(new Bullet(x + width / 2f - 4f, y, false));
    }

    /**
     * Disegna il nemico con la tinta impostata alla creazione.
     *
     * @param batch batch corrente
     */
    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(tintR, tintG, tintB, 1f);
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
        for (Bullet b : bullets) b.draw(batch);
    }
}
