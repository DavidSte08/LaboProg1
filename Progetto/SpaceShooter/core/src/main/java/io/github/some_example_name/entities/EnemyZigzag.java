package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.weapons.Bullet;

/**
 * Nemico che scende a zigzag secco invertendo bruscamente direzione.
 *
 * <p>A differenza di {@link EnemyBasic} (sinusoide morbida), lo Zigzag
 * cambia direzione di scatto ogni volta che raggiunge un bordo laterale
 * o un timer. Spara due proiettili in diagonale quando apre il fuoco.</p>
 *
 * <p>Colore: arancione/giallo — riconoscibile a colpo d'occhio.</p>
 *
 * <p>Vale 150 punti, ha 1 vita.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 * @see Enemy
 */
public class EnemyZigzag extends Enemy {

    /** Velocità laterale (px/s). */
    private static final float SPEED_X = 140f;

    /** Velocità verticale (px/s). */
    private static final float SPEED_Y = 45f;

    /** Larghezza virtuale dello schermo per rimbalzare sui bordi. */
    private final float screenW;

    /** Direzione orizzontale corrente: +1 destra, -1 sinistra. */
    private float dirX;

    /**
     * Crea uno Zigzag con direzione iniziale casuale.
     *
     * @param x       coordinata X iniziale
     * @param y       coordinata Y iniziale
     * @param screenW larghezza virtuale dello schermo
     * @param goRight {@code true} se parte verso destra
     */
    public EnemyZigzag(float x, float y, float screenW, boolean goRight) {
        super(x, y, 48f, 48f, 1, 150);
        this.screenW  = screenW;
        this.dirX     = goRight ? 1f : -1f;
        this.shootInterval = 2.2f;
        this.shootTimer    = (float)(Math.random() * shootInterval);

        try {
            texture = new Texture(Gdx.files.internal("images/enemy_basic.png"));
        } catch (Exception e) {
            Gdx.app.error("EnemyZigzag", e.getMessage());
        }
    }

    /**
     * Muove il nemico verso il basso con cambio di direzione sui bordi.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void move(float delta) {
        x += dirX * SPEED_X * delta * slowMult;
        y -= SPEED_Y * delta * slowMult;

        // Rimbalzo sui bordi con cambio secco di direzione
        if (x <= 0f) {
            x = 0f;
            dirX = 1f;
        } else if (x + width >= screenW) {
            x = screenW - width;
            dirX = -1f;
        }

        if (y + height < 0f) alive = false;
    }

    /**
     * Spara due proiettili in diagonale (nella direzione di movimento e quella opposta).
     */
    @Override
    public void shoot() {
        float cx = x + width / 2f - 4f;
        float cy = y;
        // Un proiettile verso sinistra, uno verso destra, entrambi verso il basso
        bullets.add(new Bullet(cx, cy, false, -65f, -210f));
        bullets.add(new Bullet(cx, cy, false,  65f, -210f));
    }

    /**
     * Disegna il nemico con tinta arancione/gialla per distinguerlo dagli altri tipi.
     *
     * @param batch batch corrente
     */
    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(1f, 0.72f, 0.1f, 1f); // arancione
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
        for (Bullet b : bullets) b.draw(batch);
    }
}
