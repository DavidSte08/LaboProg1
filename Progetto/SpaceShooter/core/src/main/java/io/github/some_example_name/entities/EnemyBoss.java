package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.some_example_name.weapons.Bullet;

/**
 * Boss finale con tre fasi di attacco progressive.
 *
 * <ul>
 *   <li><strong>Fase 1</strong> (vita &gt; 60 %): movimento laterale lento, sparo singolo</li>
 *   <li><strong>Fase 2</strong> (vita 30–60 %): più veloce, ventaglio di 3 proiettili</li>
 *   <li><strong>Fase 3</strong> (vita &lt; 30 %): moto sinusoidale, cerchio di 8 proiettili</li>
 * </ul>
 *
 * <p>Dimostra <strong>polimorfismo</strong>: {@code GameScreen} chiama
 * {@code enemy.move()} e {@code enemy.shoot()} senza sapere che si tratta
 * di un boss — la logica a fasi è interamente incapsulata qui.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 * @see Enemy
 */
public class EnemyBoss extends Enemy {

    /** Salute massima del boss. */
    private static final int MAX_HEALTH = 30;

    /** Velocità di movimento laterale corrente (px/s). */
    private float speed;

    /** Direzione corrente: +1 destra, -1 sinistra. */
    private float direction;

    /** Larghezza dello schermo virtuale per invertire la direzione. */
    private final float screenW;

    /** Altezza dello schermo virtuale usata per calcolare la Y di riposo. */
    private final float screenH;

    /** Fase corrente (1, 2 o 3). */
    private int phase;

    /** Tempo accumulato per il moto in fase 3 (oscillazione verticale). */
    private float timeAccum;

    /** Y di riposo del boss (zona superiore dello schermo). */
    private final float restY;

    /**
     * Crea il boss centrato nella zona superiore dello schermo.
     *
     * @param screenW larghezza virtuale dello schermo
     * @param screenH altezza virtuale dello schermo
     */
    public EnemyBoss(float screenW, float screenH) {
        super(screenW / 2f - 64,
              screenH - 200f,   // Y: 200px dal bordo superiore (corretto: era screenW*0.85)
              128, 128,
              MAX_HEALTH, 5000);
        this.screenW  = screenW;
        this.screenH  = screenH;
        this.restY    = screenH - 200f;
        this.speed    = 72f;
        this.direction = 1f;
        this.phase    = 1;
        this.shootInterval = 2.0f;
        // Ritardo iniziale randomico per evitare sparo immediato a inizio wave
        this.shootTimer = (float)(Math.random() * shootInterval);

        try {
            texture = new Texture(Gdx.files.internal("images/enemy_boss.png"));
        } catch (Exception e) {
            Gdx.app.error("EnemyBoss", "enemy_boss.png non trovato: " + e.getMessage());
        }
    }

    /**
     * Aggiorna la fase in base alla percentuale di salute rimanente.
     * Modifica velocità e intervallo di sparo di conseguenza.
     */
    private void updatePhase() {
        float pct = (float) health / MAX_HEALTH;
        if (pct > 0.6f) {
            phase = 1; speed = 72f;  shootInterval = 2.0f;
        } else if (pct > 0.3f) {
            phase = 2; speed = 120f; shootInterval = 1.3f;
        } else {
            phase = 3; speed = 165f; shootInterval = 0.8f;
        }
    }

    /**
     * Muove il boss in base alla fase corrente.
     * <ul>
     *   <li>Fasi 1–2: rimbalzo laterale puro</li>
     *   <li>Fase 3: rimbalzo laterale + oscillazione sinusoidale verticale</li>
     * </ul>
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void move(float delta) {
        updatePhase();
        timeAccum += delta;

        // Movimento orizzontale con rimbalzo
        x += speed * direction * delta * slowMult;
        if (x <= 0f) {
            x = 0f;
            direction = 1f;
        } else if (x + width >= screenW) {
            x = screenW - width;
            direction = -1f;
        }

        // Oscillazione verticale solo in fase 3
        if (phase == 3) {
            y = restY - 80f + (float) Math.sin(timeAccum * 2.0) * 80f;
        } else {
            y = restY; // Rimane nella zona superiore nelle fasi 1-2
        }
    }

    /**
     * Spara in base alla fase corrente.
     * <ul>
     *   <li>Fase 1: proiettile singolo verso il basso</li>
     *   <li>Fase 2: ventaglio di 3 proiettili</li>
     *   <li>Fase 3: cerchio di 8 proiettili in tutte le direzioni</li>
     * </ul>
     */
    @Override
    public void shoot() {
        // Origine al centro-basso del boss
        float cx = x + width / 2f - 4f;
        float cy = y;

        switch (phase) {
            case 1:
                bullets.add(new Bullet(cx, cy, false, 0f, -240f));
                break;
            case 2:
                bullets.add(new Bullet(cx, cy, false, -75f, -220f));
                bullets.add(new Bullet(cx, cy, false,    0f, -240f));
                bullets.add(new Bullet(cx, cy, false,  75f, -220f));
                break;
            case 3:
                int n = 8;
                for (int i = 0; i < n; i++) {
                    double angle = 2.0 * Math.PI / n * i;
                    float bvx = (float) Math.cos(angle) * 210f;
                    float bvy = (float) Math.sin(angle) * 210f;
                    bullets.add(new Bullet(cx, cy, false, bvx, bvy));
                }
                break;
            default:
                break;
        }
    }

    /**
     * Restituisce la fase di attacco corrente del boss.
     *
     * @return fase (1, 2 o 3)
     */
    public int getPhase() { return phase; }

    /**
     * Restituisce la salute massima del boss.
     *
     * @return salute massima
     */
    public int getMaxHealth() { return MAX_HEALTH; }
}
