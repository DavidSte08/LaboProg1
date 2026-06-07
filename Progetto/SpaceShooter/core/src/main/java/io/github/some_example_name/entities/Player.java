package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.weapons.Bullet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Navicella del giocatore.
 *
 * <p>Gestisce input da tastiera e gamepad, sparo, vite, punteggio,
 * invulnerabilità e due slot di inventory per i power-up attivi:</p>
 * <ul>
 *   <li><strong>Slot 1 — SPEED</strong>: doppio sparo, attivato con <strong>Q</strong></li>
 *   <li><strong>Slot 2 — SLOW</strong>: rallenta nemici, attivato con <strong>E</strong></li>
 * </ul>
 * <p>Si può tenere al massimo un power-up per slot; raccoglierne un secondo dello
 * stesso tipo sovrascrive il timer se già attivo, oppure riempie lo slot se era vuoto.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class Player extends GameObject {

    private float speed;
    private int   lives;
    private int   score;

    private static final int   MAX_LIVES          = 5;
    private static final float SHOOT_COOLDOWN      = 0.50f;
    private static final float INVINCIBILITY_TIME  = 1.8f;

    /** Durata del doppio sparo in secondi. */
    public static final float DOUBLE_SHOT_DURATION = 7f;

    /** Durata del rallentamento nemici in secondi. */
    public static final float SLOW_DURATION = 6f;

    private float shootTimer;
    private float invTimer;

    // ── Inventory ─────────────────────────────────────────────────────────
    /** {@code true} se il giocatore ha un SPEED in attesa di attivazione. */
    private boolean hasPendingSpeed;

    /** {@code true} se il giocatore ha un SLOW in attesa di attivazione. */
    private boolean hasPendingSlow;

    /** Timer doppio sparo attivo (> 0 → attivo). */
    private float doubleShotTimer;

    /** Timer rallentamento nemici attivo (> 0 → attivo). */
    private float slowTimer;

    private final List<Bullet> bullets;
    private final float screenW;
    private final float screenH;

    /**
     * Crea il giocatore centrato nella parte inferiore dello schermo.
     *
     * @param screenW larghezza virtuale
     * @param screenH altezza virtuale
     */
    public Player(float screenW, float screenH) {
        super(screenW / 2f - 32f, 80f, 64f, 64f);
        this.screenW    = screenW;
        this.screenH    = screenH;
        this.speed      = 270f;
        this.lives      = 3;
        this.score      = 0;
        this.bullets    = new ArrayList<>();
        this.shootTimer = SHOOT_COOLDOWN;

        try {
            texture = new Texture(Gdx.files.internal("images/player.png"));
        } catch (Exception e) {
            Gdx.app.error("Player", "player.png: " + e.getMessage());
        }
    }

    /**
     * Aggiorna input, timer e proiettili.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void update(float delta) {
        shootTimer += delta;
        if (invTimer       > 0f) invTimer       -= delta;
        if (doubleShotTimer > 0f) doubleShotTimer -= delta;
        if (slowTimer       > 0f) slowTimer       -= delta;

        // ── Tastiera ─────────────────────────────────────────────────────
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)  || Gdx.input.isKeyPressed(Input.Keys.A)) x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) x += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)    || Gdx.input.isKeyPressed(Input.Keys.W)) y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)  || Gdx.input.isKeyPressed(Input.Keys.S)) y -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) shoot();

        // Q → slot 1: doppio sparo
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) activatePendingSpeed();
        // E → slot 2: rallenta nemici
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) activatePendingSlow();

        // ── Controller ───────────────────────────────────────────────────
        if (Controllers.getControllers().size > 0) {
            Controller ctrl = Controllers.getControllers().first();
            float ax = ctrl.getAxis(0);
            float ay = ctrl.getAxis(1);
            if (Math.abs(ax) > 0.15f) x += ax * speed * delta;
            if (Math.abs(ay) > 0.15f) y -= ay * speed * delta;
            if (ctrl.getButton(0)) shoot();
            if (ctrl.getButton(1)) activatePendingSpeed(); // Tasto B → SPEED
            if (ctrl.getButton(2)) activatePendingSlow();  // Tasto X → SLOW
        }

        // ── Clamp ai bordi ────────────────────────────────────────────────
        x = Math.max(0f, Math.min(x, screenW - width));
        y = Math.max(0f, Math.min(y, screenH - height));

        // ── Aggiorna proiettili ──────────────────────────────────────────
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(delta);
            if (!b.isAlive()) it.remove();
        }
    }

    /**
     * Disegna la navicella (con lampeggio se invulnerabile) e i proiettili.
     *
     * @param batch batch corrente
     */
    @Override
    public void draw(SpriteBatch batch) {
        boolean show = invTimer <= 0f || (int)(invTimer * 10f) % 2 == 0;
        if (show) super.draw(batch);
        for (Bullet b : bullets) b.draw(batch);
    }

    /** Spara uno o due proiettili a seconda del power-up attivo. */
    public void shoot() {
        if (shootTimer < SHOOT_COOLDOWN) return;
        shootTimer = 0f;
        float cx = x + width / 2f;
        float by = y + height;
        if (doubleShotTimer > 0f) {
            bullets.add(new Bullet(cx - 14f, by, true));
            bullets.add(new Bullet(cx +  6f, by, true));
        } else {
            bullets.add(new Bullet(cx - 4f, by, true));
        }
    }

    /**
     * Applica un danno. Ignora se invulnerabile.
     *
     * @return {@code true} se ancora vivo
     * @throws IllegalStateException se già morto
     */
    public boolean takeDamage() {
        if (!alive) throw new IllegalStateException("Il giocatore è già morto");
        if (invTimer > 0f) return true;
        lives--;
        invTimer = INVINCIBILITY_TIME;
        if (lives <= 0) { lives = 0; alive = false; return false; }
        return true;
    }

    /** Aggiunge una vita (max {@value #MAX_LIVES}). */
    public void addLife() {
        if (lives < MAX_LIVES) lives++;
    }

    /**
     * Aggiunge punti.
     * @param points punti ≥ 0
     * @throws IllegalArgumentException se negativi
     */
    public void addScore(int points) {
        if (points < 0) throw new IllegalArgumentException("Punti negativi: " + points);
        score += points;
    }

    // ── Inventory Slot 1: SPEED ───────────────────────────────────────────

    /**
     * Raccoglie il power-up SPEED: se lo slot è vuoto lo mette in attesa,
     * se il doppio sparo è già attivo ricarica il timer.
     */
    public void collectSpeed() {
        if (doubleShotTimer > 0f) {
            doubleShotTimer = DOUBLE_SHOT_DURATION; // ricarica
        } else {
            hasPendingSpeed = true;
        }
    }

    /** Attiva il doppio sparo dall'inventory (tasto Q). */
    public void activatePendingSpeed() {
        if (!hasPendingSpeed) return;
        hasPendingSpeed = false;
        doubleShotTimer = DOUBLE_SHOT_DURATION;
    }

    // ── Inventory Slot 2: SLOW ────────────────────────────────────────────

    /**
     * Raccoglie il power-up SLOW: se lo slot è vuoto lo mette in attesa,
     * se il rallentamento è già attivo ricarica il timer.
     */
    public void collectSlow() {
        if (slowTimer > 0f) {
            slowTimer = SLOW_DURATION; // ricarica
        } else {
            hasPendingSlow = true;
        }
    }

    /** Attiva il rallentamento nemici dall'inventory (tasto E). */
    public void activatePendingSlow() {
        if (!hasPendingSlow) return;
        hasPendingSlow = false;
        slowTimer = SLOW_DURATION;
    }

    /** @return {@code true} se il rallentamento nemici è attualmente attivo */
    public boolean isSlowActive()       { return slowTimer > 0f; }

    /** @return secondi rimanenti del rallentamento */
    public float   getSlowTimer()       { return slowTimer; }

    /** @return {@code true} se il giocatore ha un SLOW in attesa */
    public boolean hasPendingSlow()     { return hasPendingSlow; }

    // ── Getter vari ───────────────────────────────────────────────────────

    public List<Bullet> getBullets()    { return bullets; }
    public int   getLives()             { return lives; }
    public int   getScore()             { return score; }
    public boolean isInvincible()       { return invTimer > 0f; }
    public boolean isDoubleShotActive() { return doubleShotTimer > 0f; }
    public float   getDoubleShotTimer() { return doubleShotTimer; }
    public boolean hasPendingSpeed()    { return hasPendingSpeed; }

    /** Usato dai test — attivazione diretta senza inventory. */
    public void activateDoubleShot()    { doubleShotTimer = DOUBLE_SHOT_DURATION; }
}
