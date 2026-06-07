package io.github.some_example_name.weapons;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.some_example_name.entities.GameObject;

/**
 * Proiettile sparato dal giocatore o dai nemici.
 *
 * <p>Estende {@link GameObject} e si muove con velocità vettoriale (vx, vy).
 * Le texture sono <strong>condivise</strong> tramite campi statici per evitare
 * memory leak: creare una {@code new Texture} per ogni proiettile (potenzialmente
 * centinaia per sessione) esaurirebbe la VRAM. Le texture statiche vengono
 * rilasciate una sola volta tramite {@link #disposeTextures()}.</p>
 *
 * <p>I proiettili del giocatore sono rettangolari (laser 8×24), quelli nemici
 * sono rotondi (plasma 10×10) — forme diverse aiutano il giocatore a
 * distinguerli visivamente a colpo d'occhio.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class Bullet extends GameObject {

    // ── Texture condivise (una sola istanza per tipo) ─────────────────────
    private static Texture texPlayer = null;
    private static Texture texEnemy  = null;

    /** Velocità orizzontale (px/s). */
    private final float vx;

    /** Velocità verticale (px/s). */
    private final float vy;

    /** {@code true} se sparato dal giocatore. */
    private final boolean fromPlayer;

    /** Velocità verticale di default per proiettili giocatore. */
    private static final float PLAYER_SPEED = 420f;

    /** Velocità verticale di default per proiettili nemici. */
    private static final float ENEMY_SPEED  = 210f;

    /** Margine oltre i bordi virtuali prima di eliminare il proiettile (px). */
    private static final float MARGIN = 40f;

    /** Larghezza virtuale usata per i controlli di bounds. */
    private static float worldW = 720f;

    /** Altezza virtuale usata per i controlli di bounds. */
    private static float worldH = 1280f;

    /**
     * Inizializza le dimensioni virtuali. Chiamare una volta da {@code GameScreen}.
     *
     * @param w larghezza virtuale
     * @param h altezza virtuale
     */
    public static void setWorldSize(float w, float h) {
        worldW = w;
        worldH = h;
    }

    /**
     * Pre-carica le texture condivise. No-op se già caricate.
     */
    public static void loadTextures() {
        if (texPlayer == null) {
            try {
                texPlayer = new Texture(Gdx.files.internal("images/bullet_player.png"));
            } catch (Exception e) {
                Gdx.app.error("Bullet", "bullet_player.png: " + e.getMessage());
            }
        }
        if (texEnemy == null) {
            try {
                texEnemy = new Texture(Gdx.files.internal("images/bullet_enemy.png"));
            } catch (Exception e) {
                Gdx.app.error("Bullet", "bullet_enemy.png: " + e.getMessage());
            }
        }
    }

    /**
     * Rilascia le texture condivise. Chiamare alla chiusura del gioco.
     */
    public static void disposeTextures() {
        if (texPlayer != null) { texPlayer.dispose(); texPlayer = null; }
        if (texEnemy  != null) { texEnemy.dispose();  texEnemy  = null; }
    }

    /**
     * Crea un proiettile verticale semplice.
     *
     * @param x          coordinata X
     * @param y          coordinata Y
     * @param fromPlayer {@code true} se sparato dal giocatore
     */
    public Bullet(float x, float y, boolean fromPlayer) {
        this(x, y, fromPlayer, 0f, fromPlayer ? PLAYER_SPEED : -ENEMY_SPEED);
    }

    /**
     * Crea un proiettile con velocità vettoriale arbitraria (es. cerchio del boss).
     *
     * @param x          coordinata X
     * @param y          coordinata Y
     * @param fromPlayer {@code true} se sparato dal giocatore
     * @param vx         velocità orizzontale (px/s)
     * @param vy         velocità verticale (px/s)
     */
    public Bullet(float x, float y, boolean fromPlayer, float vx, float vy) {
        // Laser del giocatore: 8×24 rettangolare; plasma nemici: 10×10 rotondo
        super(x, y, fromPlayer ? 8f : 10f, fromPlayer ? 24f : 10f);
        this.fromPlayer = fromPlayer;
        this.vx = vx;
        this.vy = vy;
        this.texture = fromPlayer ? texPlayer : texEnemy;
    }

    /**
     * Aggiorna posizione e disattiva se fuori dai bordi virtuali.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void update(float delta) {
        x += vx * delta;
        y += vy * delta;
        if (y > worldH + MARGIN || y < -MARGIN || x < -MARGIN || x > worldW + MARGIN) {
            alive = false;
        }
    }

    /**
     * Override di dispose(): la texture è condivisa, non liberarla per istanza.
     * Usare {@link #disposeTextures()} per il rilascio globale.
     */
    @Override
    public void dispose() { /* texture condivisa — no-op intenzionale */ }

    /** @return {@code true} se sparato dal giocatore */
    public boolean isFromPlayer() { return fromPlayer; }
}
