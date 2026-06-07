package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.weapons.Bullet;

/**
 * Nemico lento e coriaceo che spara una raffica a ventaglio.
 *
 * <p>Scende verticalmente a bassa velocità in linea retta, ma quando spara
 * lancia 5 proiettili a ventaglio verso il basso. Ha 2 punti vita e vale
 * 200 punti. La sua pericolosità è nella raffica, non nella mobilità.</p>
 *
 * <p>Colore: verde acqua — indica che è più resistente del base.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 * @see Enemy
 */
public class EnemySweeper extends Enemy {

    /** Velocità di discesa (px/s) — più lenta del base. */
    private static final float SPEED_Y = 38f;

    /** Numero di proiettili del ventaglio. */
    private static final int   FAN_BULLETS = 5;

    /** Angolo totale del ventaglio in gradi. */
    private static final float FAN_SPREAD  = 70f;

    /**
     * Crea uno Sweeper nella posizione specificata.
     *
     * @param x coordinata X iniziale
     * @param y coordinata Y iniziale
     */
    public EnemySweeper(float x, float y) {
        super(x, y, 52f, 52f, 2, 200);
        this.shootInterval = 3.2f;
        this.shootTimer    = (float)(Math.random() * shootInterval);

        try {
            texture = new Texture(Gdx.files.internal("images/enemy_basic.png"));
        } catch (Exception e) {
            Gdx.app.error("EnemySweeper", e.getMessage());
        }
    }

    /**
     * Scende dritto verso il basso senza oscillazione.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void move(float delta) {
        y -= SPEED_Y * delta * slowMult;
        x = Math.max(0f, Math.min(x, 720f - width));
        if (y + height < 0f) alive = false;
    }

    /**
     * Spara un ventaglio di 5 proiettili distribuiti su un arco di 70°.
     * L'angolo centrale è 270° (dritto verso il basso).
     */
    @Override
    public void shoot() {
        float cx     = x + width / 2f - 4f;
        float cy     = y;
        float speed  = 200f;
        float startA = 270f - FAN_SPREAD / 2f; // angolo del primo proiettile

        for (int i = 0; i < FAN_BULLETS; i++) {
            double angle = Math.toRadians(startA + (FAN_SPREAD / (FAN_BULLETS - 1)) * i);
            float vx = (float) Math.cos(angle) * speed;
            float vy = (float) Math.sin(angle) * speed;
            bullets.add(new Bullet(cx, cy, false, vx, vy));
        }
    }

    /**
     * Disegna lo Sweeper con tinta verde-acqua e i suoi proiettili.
     *
     * @param batch batch corrente
     */
    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(0.2f, 0.9f, 0.7f, 1f); // verde acqua
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
        for (Bullet b : bullets) b.draw(batch);
    }
}
