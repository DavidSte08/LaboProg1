package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.weapons.Bullet;

/**
 * Nemico che entra dall'alto lentamente, poi si lancia in picchiata verso il giocatore.
 *
 * <p>Ha due fasi distinte:</p>
 * <ol>
 *   <li><strong>HOVER</strong> — scende piano fino a una Y di aggancio, poi si ferma
 *       e oscilla lateralmente per un breve periodo.</li>
 *   <li><strong>DIVE</strong> — si lancia in picchiata a velocità elevata verso la
 *       posizione X del giocatore, sparando un proiettile appena prima di tuffarsi.</li>
 * </ol>
 *
 * <p>Ha 1 vita ma è difficile da evitare nella fase DIVE. Vale 250 punti.</p>
 * <p>Colore: viola/magenta — ricorda un predatore.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 * @see Enemy
 */
public class EnemyDiver extends Enemy {

    /** Fasi comportamentali del Diver. */
    private enum Phase { HOVER, DIVE }

    /** Fase corrente. */
    private Phase phase;

    /** Velocità di discesa nella fase HOVER (px/s). */
    private static final float HOVER_SPEED_Y = 40f;

    /** Velocità nella fase DIVE (px/s). */
    private static final float DIVE_SPEED    = 360f;

    /** Y sotto la quale il Diver entra in modalità HOVER (zona alta dello schermo). */
    private final float hoverY;

    /** Tempo trascorso in hover prima del tuffo. */
    private float hoverTimer;

    /** Durata dell'hover prima del tuffo (secondi). */
    private static final float HOVER_DURATION = 1.4f;

    /** Oscillazione laterale durante l'hover. */
    private float timeAccum;

    /** Velocità del tuffo nella direzione X (calcolata al momento del lancio). */
    private float diveVX;

    /** Velocità del tuffo nella direzione Y. */
    private float diveVY;

    /** X del giocatore memorizzata al momento del lancio (passata dal WaveManager). */
    private float targetX;

    /**
     * Crea un Diver nella posizione specificata.
     *
     * @param x       coordinata X iniziale
     * @param y       coordinata Y iniziale
     * @param screenH altezza virtuale — usata per calcolare la Y di hover
     * @param targetX X del giocatore al momento della creazione
     */
    public EnemyDiver(float x, float y, float screenH, float targetX) {
        super(x, y, 48f, 48f, 1, 250);
        this.hoverY   = screenH * 0.68f; // Aggancia a circa 2/3 dall'alto
        this.phase    = Phase.HOVER;
        this.targetX  = targetX;
        this.shootInterval = 999f; // Spara solo al momento del tuffo (gestito manualmente)
        this.shootTimer    = 0f;

        try {
            texture = new Texture(Gdx.files.internal("images/enemy_basic.png"));
        } catch (Exception e) {
            Gdx.app.error("EnemyDiver", e.getMessage());
        }
    }

    /**
     * Aggiorna il comportamento in base alla fase corrente.
     * Sovrascrive {@link Enemy#update(float)} per gestire il cambio di fase
     * senza dipendere dal timer di sparo standard.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void update(float delta) {
        if (!alive) return;
        move(delta);
        // Aggiorna proiettili
        java.util.Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(delta);
            if (!b.isAlive()) it.remove();
        }
    }

    /**
     * Logica di movimento bifasica: hover + tuffo.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void move(float delta) {
        timeAccum += delta;

        if (phase == Phase.HOVER) {
            // Scende verso la hoverY
            if (y > hoverY) {
                y -= HOVER_SPEED_Y * delta * slowMult;
            } else {
                // Raggiunta la Y di hover: oscilla lateralmente e conta il tempo
                x += (float) Math.sin(timeAccum * 4f) * 50f * delta * slowMult;
                x = Math.max(0f, Math.min(x, 720f - width));
                hoverTimer += delta;
                if (hoverTimer >= HOVER_DURATION) {
                    // Transizione al DIVE: calcola direzione verso il giocatore
                    shootBeforeDive();
                    float dx = (targetX - x);
                    float dy = -hoverY; // verso il basso
                    float len = (float) Math.sqrt(dx * dx + dy * dy);
                    if (len < 1f) len = 1f;
                    diveVX = (dx / len) * DIVE_SPEED;
                    diveVY = (dy / len) * DIVE_SPEED;
                    phase = Phase.DIVE;
                }
            }
        } else {
            // DIVE: vola dritto nella direzione calcolata
            x += diveVX * delta * slowMult;
            y += diveVY * delta * slowMult;
            // Clamp laterale anche in picchiata
            x = Math.max(0f, Math.min(x, 720f - width));
            if (y + height < 0f || y > 1280f + 50f) alive = false;
        }
    }

    /**
     * Spara un proiettile verso il basso appena prima di tuffarsi.
     */
    private void shootBeforeDive() {
        float cx = x + width / 2f - 4f;
        bullets.add(new Bullet(cx, y, false, 0f, -350f));
    }

    /**
     * Non usato (lo sparo è gestito direttamente in {@link #move(float)}).
     */
    @Override
    public void shoot() { /* Gestito internamente al cambio di fase */ }

    /**
     * Disegna il Diver con tinta viola/magenta e i suoi proiettili.
     * Nella fase DIVE brilla leggermente di bianco per segnalare il pericolo.
     *
     * @param batch batch corrente
     */
    @Override
    public void draw(SpriteBatch batch) {
        if (phase == Phase.DIVE) {
            batch.setColor(1f, 0.5f, 1f, 1f); // magenta brillante in picchiata
        } else {
            batch.setColor(0.7f, 0.2f, 0.9f, 1f); // viola in hover
        }
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
        for (Bullet b : bullets) b.draw(batch);
    }

    /**
     * Aggiorna la X del giocatore (chiamabile dal WaveManager se necessario).
     *
     * @param px X corrente del giocatore
     */
    public void setTargetX(float px) { this.targetX = px; }

    /** @return {@code true} se il Diver è nella fase di picchiata */
    public boolean isDiving() { return phase == Phase.DIVE; }
}
