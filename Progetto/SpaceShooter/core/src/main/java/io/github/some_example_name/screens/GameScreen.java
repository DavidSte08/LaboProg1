package io.github.some_example_name.screens;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.entities.*;
import io.github.some_example_name.utils.WaveManager;
import io.github.some_example_name.weapons.Bullet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Schermata principale di gioco.
 *
 * <p>Gestisce il loop completo con:</p>
 * <ul>
 *   <li>Aggiornamento entità e collisioni</li>
 *   <li>HUD avanzato con vite, punteggio, combo, barra boss e doppio-sparo</li>
 *   <li>Banner di annuncio ondata con animazione</li>
 *   <li>Pausa con ESC o P</li>
 *   <li>Flash rosso sullo schermo al danno</li>
 *   <li>Overlay fine partita con countdown</li>
 * </ul>
 *
 * <p><strong>Responsive:</strong> {@link FitViewport} 720×1280 con letterbox automatico.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class GameScreen implements Screen {

    /** Larghezza virtuale del mondo (px). */
    public static final float W = 720f;

    /** Altezza virtuale del mondo (px). */
    public static final float H = 1280f;

    private static final float POWERUP_CHANCE = 0.18f;
    private static final int   TOTAL_WAVES    = 10;
    private static final float END_DELAY      = 4.5f;

    // ── Durata e comportamento banner ondata ──────────────────────────────
    private static final float WAVE_BANNER_DURATION = 2.2f;

    private final Main game;

    private final OrthographicCamera camera;
    private final Viewport            viewport;

    private final Player        player;
    private final List<Enemy>   enemies;
    private final List<PowerUp> powerUps;

    /**
     * Proiettili di nemici già morti che devono continuare a muoversi.
     * Quando un nemico viene rimosso dalla lista, i suoi proiettili attivi
     * vengono trasferiti qui invece di sparire.
     */
    private final List<io.github.some_example_name.weapons.Bullet> orphanBullets;
    private final WaveManager   waveManager;

    // ── Font ──────────────────────────────────────────────────────────────
    private final BitmapFont  fontHUD;
    private final BitmapFont  fontLarge;
    private final BitmapFont  fontSmall;
    private final BitmapFont  fontMicro;
    private final BitmapFont  fontCombo;
    private final BitmapFont  fontBoss;
    private final GlyphLayout layout;

    // ── Texture ───────────────────────────────────────────────────────────
    private Texture texBackground;
    private Texture texHudBar;
    private Texture texHeart;
    private Texture texBossBarBg;
    private Texture texBossBarFill;
    private Texture texPanel;
    private Texture texWaveBanner;
    private Texture texCombo;

    private final ShapeRenderer shapes;

    // ── Stato partita ─────────────────────────────────────────────────────
    private boolean gameOver;
    private boolean victory;
    private float   endTimer;
    private float   time;
    private float   bgOffset;

    // ── Banner ondata ─────────────────────────────────────────────────────
    private float   waveBannerTimer = 0f;  // > 0 → banner visibile
    private String  waveBannerText  = "";

    // ── Flash danno ───────────────────────────────────────────────────────
    private float damageFlash = 0f;   // 0→1 flash rosso bordo schermo

    // ── Sistema combo ─────────────────────────────────────────────────────
    private int   comboCount    = 0;
    private float comboTimer    = 0f;  // Finestra combo (reset se scade)
    private float comboPopTimer = 0f;  // Mostra popup "+N combo"
    private int   comboPopScore = 0;
    private static final float COMBO_WINDOW = 1.8f;  // secondi tra uccisioni
    private static final float COMBO_POP_DURATION = 1.2f;

    /**
     * Crea la schermata di gioco e avvia la prima ondata.
     *
     * @param game istanza principale del gioco
     */
    public GameScreen(Main game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new FitViewport(W, H, camera);
        camera.position.set(W / 2f, H / 2f, 0f);
        camera.update();

        fontHUD   = new BitmapFont();         fontLarge = new BitmapFont();         fontSmall = new BitmapFont();         fontMicro = new BitmapFont(); fontMicro.getData().setScale(1.3f);
        fontCombo = new BitmapFont(); fontCombo.getData().setScale(1.5f);
        fontBoss  = new BitmapFont(); fontBoss.getData().setScale(1.4f);
        layout    = new GlyphLayout();
        shapes    = new ShapeRenderer();

        texBackground  = loadTex("images/background.png");
        texHudBar      = loadTex("images/hud_bar.png");
        texHeart       = loadTex("images/icon_heart.png");
        texBossBarBg   = loadTex("images/boss_bar_bg.png");
        texBossBarFill = loadTex("images/boss_bar_fill.png");
        texPanel       = loadTex("images/panel.png");
        texWaveBanner  = loadTex("images/wave_banner.png");
        texCombo       = loadTex("images/icon_combo.png");

        enemies       = new ArrayList<>();
        powerUps      = new ArrayList<>();
        orphanBullets = new ArrayList<>();

        Bullet.setWorldSize(W, H);
        Bullet.loadTextures();

        player      = new Player(W, H);
        waveManager = new WaveManager(W, H);

        spawnNext(); // spawnNext() gestisce già la musica
    }

    private Texture loadTex(String path) {
        try { return new Texture(Gdx.files.internal(path)); }
        catch (Exception e) { Gdx.app.error("GameScreen", path + ": " + e.getMessage()); return null; }
    }

    private void spawnNext() {
        List<Enemy> next = waveManager.nextWave();
        enemies.addAll(next);
        waveBannerTimer = WAVE_BANNER_DURATION;
        waveBannerText  = waveManager.isBossWave()
            ? "!! BOSS WAVE " + waveManager.getCurrentWave() + " !!"
            : "ONDATA  " + waveManager.getCurrentWave() + "  /  " + TOTAL_WAVES;
        comboCount = 0;
        // Musica: boss wave → musica boss, wave normale → musica gioco
        if (waveManager.isBossWave()) {
            game.audioManager.switchToBossMusic();
        } else {
            game.audioManager.switchToGameMusic();
        }
        game.audioManager.playWave();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Screen.render
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        time += delta;

        // ── Pausa ─────────────────────────────────────────────────────────
        if (!gameOver && !victory) {
            boolean pauseKey = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                            || Gdx.input.isKeyJustPressed(Input.Keys.P);
            boolean pauseBtn = Controllers.getControllers().size > 0
                            && Controllers.getControllers().first().getButton(7); // Start
            if (pauseKey || pauseBtn) {
                game.audioManager.stopMusic();
                game.setScreen(new PauseScreen(game, this));
                return;
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0.04f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();

        if (!gameOver && !victory) {
            update(delta);
        } else {
            endTimer += delta;
            if (endTimer >= END_DELAY) {
                game.scoreManager.submitScore(player.getScore());
                game.setScreen(new MenuScreen(game));
                return;
            }
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        drawBackground(delta);
        player.draw(game.batch);
        for (Enemy e   : enemies)  e.draw(game.batch);
        for (io.github.some_example_name.weapons.Bullet ob : orphanBullets) ob.draw(game.batch);
        for (PowerUp p : powerUps) p.draw(game.batch);

        drawHUD();
        drawWaveBanner(delta);
        drawComboPopup(delta);

        if (gameOver || victory) drawEndScreen();

        game.batch.end();

        drawShapeEffects(delta);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Aggiornamento logica
    // ─────────────────────────────────────────────────────────────────────

    private void update(float delta) {
        // Aggiorna posizione giocatore nel WaveManager (usata dai Diver)
        waveManager.setPlayerX(player.getX() + 32f);

        // Applica slowMult a tutti i nemici attivi
        float slowM = player.isSlowActive() ? 0.28f : 1.0f;
        for (io.github.some_example_name.entities.Enemy en : enemies) {
            en.setSlowMult(slowM);
        }

        // Timer combo: se scade, azzera la serie
        if (comboCount > 0) {
            comboTimer -= delta;
            if (comboTimer <= 0f) comboCount = 0;
        }
        if (comboPopTimer > 0f) comboPopTimer -= delta;
        if (damageFlash  > 0f) damageFlash  -= delta * 3f;
        if (waveBannerTimer > 0f) waveBannerTimer -= delta;

        player.update(delta);

        Iterator<Enemy> ei = enemies.iterator();
        while (ei.hasNext()) {
            Enemy en = ei.next();
            en.update(delta);

            // 1. Proiettili giocatore → nemico
            Iterator<Bullet> bi = player.getBullets().iterator();
            while (bi.hasNext()) {
                Bullet b = bi.next();
                if (!b.isAlive()) continue;
                if (b.getBounds().overlaps(en.getBounds())) {
                    en.takeDamage(1);
                    b.setAlive(false);
                    if (!en.isAlive()) {
                        // Trasferisci i proiettili rimasti agli orfani
                        orphanBullets.addAll(en.getBullets());
                        en.getBullets().clear();
                        // Combo: uccisione in sequenza
                        comboCount++;
                        comboTimer = COMBO_WINDOW;
                        int bonus  = en.getPoints() * Math.max(1, comboCount / 3);
                        player.addScore(bonus);
                        comboPopScore = bonus;
                        comboPopTimer = COMBO_POP_DURATION;
                        game.audioManager.playExplosion();
                        maybeSpawnPowerUp(en.getX(), en.getY());
                        break;
                    } else if (en instanceof EnemyBoss) {
                        // Boss ancora vivo: suono danno specifico
                        game.audioManager.playBossDamage();
                    }
                }
            }

            // 2. Contatto nemico → giocatore
            if (en.isAlive() && en.getBounds().overlaps(player.getBounds())) {
                en.setAlive(false);
                // Trasferisci proiettili rimasti
                orphanBullets.addAll(en.getBullets());
                en.getBullets().clear();
                if (!player.takeDamage()) {
                    triggerGameOver(ei);
                    return;
                }
                damageFlash = 1f;
                game.audioManager.playHit();
            }

            // 3. Proiettili nemici → giocatore
            for (Bullet b : en.getBullets()) {
                if (!b.isAlive()) continue;
                if (b.getBounds().overlaps(player.getBounds())) {
                    b.setAlive(false);
                    if (!player.takeDamage()) {
                        triggerGameOver(ei);
                        return;
                    }
                    damageFlash = 1f;
                    game.audioManager.playHit();
                }
            }

            if (!en.isAlive()) ei.remove();
        }

        // ── Proiettili orfani (di nemici già morti) ────────────────────────
        Iterator<io.github.some_example_name.weapons.Bullet> oi = orphanBullets.iterator();
        while (oi.hasNext()) {
            io.github.some_example_name.weapons.Bullet ob = oi.next();
            ob.update(delta);
            if (!ob.isAlive()) { oi.remove(); continue; }
            if (ob.getBounds().overlaps(player.getBounds())) {
                ob.setAlive(false);
                oi.remove();
                if (!player.takeDamage()) {
                    gameOver = true;
                    game.audioManager.stopMusic();
                    game.audioManager.playDeath();
                    return;
                }
                damageFlash = 1f;
                game.audioManager.playHit();
            }
        }

        // Power-up
        Iterator<PowerUp> pi = powerUps.iterator();
        while (pi.hasNext()) {
            PowerUp p = pi.next();
            p.update(delta);
            if (p.isAlive() && p.getBounds().overlaps(player.getBounds())) {
                p.apply(player);
                game.audioManager.playPowerUpCollect();
            }
            if (!p.isAlive()) pi.remove();
        }

        // Ondata completata
        if (enemies.isEmpty()) {
            if (waveManager.getCurrentWave() >= TOTAL_WAVES) {
                victory = true;
                game.audioManager.stopMusic();
                game.audioManager.playVictory();
            } else {
                spawnNext();
            }
        }
    }

    /** Avvia game over rimuovendo il nemico corrente dall'iteratore. */
    private void triggerGameOver(Iterator<Enemy> ei) {
        gameOver = true;
        game.audioManager.stopMusic();
        game.audioManager.playDeath();
        ei.remove();
    }

    private void maybeSpawnPowerUp(float x, float y) {
        if (MathUtils.random() >= POWERUP_CHANCE) return;
        // Sceglie il tipo in modo intelligente:
        // LIFE 30%, SPEED 35%, SLOW 35%
        float r = MathUtils.random();
        PowerUp.Type t;
        if      (r < 0.30f) t = PowerUp.Type.LIFE;
        else if (r < 0.65f) t = PowerUp.Type.SPEED;
        else                t = PowerUp.Type.SLOW;
        powerUps.add(new PowerUp(x, y, t));
    }

    // ─────────────────────────────────────────────────────────────────────
    // Rendering
    // ─────────────────────────────────────────────────────────────────────

    private void drawBackground(float delta) {
        // Scroll lento e lineare: 18 px/s, fermo se partita finita
        if (!gameOver && !victory) bgOffset = (bgOffset + 18f * delta) % H;
        if (texBackground == null) return;
        // Due copie della stessa texture accostate verticalmente per il loop continuo.
        // La copia B parte esattamente dove finisce la A, senza sovrapposizione.
        float copyA_y = -bgOffset;          // scende da 0 verso il basso
        float copyB_y = copyA_y + H;        // immediatamente sopra la A
        game.batch.draw(texBackground, 0f, copyA_y, W, H);
        game.batch.draw(texBackground, 0f, copyB_y, W, H);
    }

    private void drawHUD() {
        // Barra superiore
        if (texHudBar != null) game.batch.draw(texHudBar, 0f, H - 68f, W, 68f);

        // Score
        // SCORE — etichetta + valore con ombra
                fontHUD.setColor(0f, 0f, 0f, 0.55f);
        fontHUD.draw(game.batch, "SCORE", 22f, H - 9f);
        fontHUD.setColor(0.72f, 0.72f, 0.95f, 1f);
        fontHUD.draw(game.batch, "SCORE", 20f, H - 10f);

                fontHUD.setColor(0f, 0f, 0f, 0.55f);
        fontHUD.draw(game.batch, String.valueOf(player.getScore()), 22f, H - 31f);
        fontHUD.setColor(0.35f, 0.9f, 1f, 1f);
        fontHUD.draw(game.batch, String.valueOf(player.getScore()), 20f, H - 32f);

        // Ondata centro
        String waveStr = "ONDATA  " + waveManager.getCurrentWave() + " / " + TOTAL_WAVES;
                layout.setText(fontHUD, waveStr);
        float waveX = (W - layout.width) / 2f;
        fontHUD.setColor(0f, 0f, 0f, 0.55f);
        fontHUD.draw(game.batch, waveStr, waveX + 1.5f, H - 21f);
        fontHUD.setColor(0.85f, 0.85f, 1f, 1f);
        fontHUD.draw(game.batch, waveStr, waveX, H - 22f);

        // Record
                String hsLbl = "RECORD";
        layout.setText(fontHUD, hsLbl);
        float recX = W - layout.width - 20f;
        fontHUD.setColor(0f, 0f, 0f, 0.55f);
        fontHUD.draw(game.batch, hsLbl, recX + 1.5f, H - 9f);
        fontHUD.setColor(0.55f, 0.55f, 0.82f, 1f);
        fontHUD.draw(game.batch, hsLbl, recX, H - 10f);

                String hsVal = String.valueOf(game.scoreManager.getHighScore());
        layout.setText(fontHUD, hsVal);
        float recValX = W - layout.width - 20f;
        fontHUD.setColor(0f, 0f, 0f, 0.55f);
        fontHUD.draw(game.batch, hsVal, recValX + 1.5f, H - 31f);
        fontHUD.setColor(0.95f, 0.88f, 0.35f, 1f);
        fontHUD.draw(game.batch, hsVal, recValX, H - 32f);

        // Vite
        drawLives();

        // Indicatore doppio sparo

        // Combo
        drawComboHUD();

        // Boss warning
        if (waveManager.isBossWave() && !enemies.isEmpty() && waveBannerTimer <= 0f) {
            float alpha = 0.5f + 0.5f * MathUtils.sin(time * 7f);
            fontLarge.setColor(1f, 0.15f, 0.15f, alpha);
                        String warn = "!! BOSS !!";
            layout.setText(fontLarge, warn);
            fontLarge.draw(game.batch, warn, (W - layout.width) / 2f, H - 82f);
                    }

        drawBossBar();
    }

    private void drawLives() {
        // Layout preciso (Y=0 in basso):
        // font.draw usa Y come BASELINE (bordo superiore testo)
        // batch.draw usa Y come BORDO INFERIORE della texture
        //
        //  Y=192 → baseline "VITE"  (testo scende a 175)
        //  Y=160 → bordo inferiore cuori, top=186  ← non tocca testo (186<192✓)
        //  Y=154 → baseline slot Q  (testo scende a 137)
        //  Y=131 → baseline slot E  (testo scende a 114)

        // Cuori (batch.draw: Y = bordo inferiore)
        if (texHeart == null) {
            fontMicro.setColor(1f, 0.3f, 0.3f, 1f);
            fontMicro.draw(game.batch, String.valueOf(player.getLives()), 20f, 10f);
        } else {
            for (int i = 0; i < player.getLives(); i++) {
                float alpha = player.isInvincible() ? (0.3f + 0.7f * MathUtils.sin(time * 20f)) : 1f;
                game.batch.setColor(1f, 0.32f, 0.32f, alpha);
                game.batch.draw(texHeart, 20f + i * 32f, 10f, 26f, 26f);
            }
            game.batch.setColor(Color.WHITE);
        }


        // Slot 1 [Q] — baseline 154, testo scende a 137, cuori bottom=160 → gap 6px ✓
        drawSlot(20f, 100f,
            player.hasPendingSpeed(), player.isDoubleShotActive(),
            player.getDoubleShotTimer(), io.github.some_example_name.entities.Player.DOUBLE_SHOT_DURATION,
            0.15f, 0.88f, 1f,
            "[Q] Doppio sparo", "Doppio sparo");

        // Slot 2 [E] — rallenta nemici  (SEMPRE VISIBILE)
        drawSlot(20f, 70f,
            player.hasPendingSlow(), player.isSlowActive(),
            player.getSlowTimer(), io.github.some_example_name.entities.Player.SLOW_DURATION,
            0.15f, 0.82f, 0.42f,
            "[E] Rallenta nemici", "Nemici lenti");
    }

    /**
     * Slot HUD inventory. fontMicro scala 1.3 fissa.
     * Stato VUOTO: grigio tenue (sempre visibile come reminder del tasto).
     * Stato PRONTO: lampeggiante nel colore del power-up.
     * Stato ATTIVO: colore pieno + countdown secondi.
     */
    private void drawSpeedInventory() { /* chiamato da drawLives */ }

    private void drawSlot(float x, float y,
                          boolean pending, boolean active,
                          float timer, float maxTimer,
                          float cr, float cg, float cb,
                          String labelWaiting, String labelActive) {
        if (pending) {
            float f = 0.65f + 0.35f * MathUtils.sin(time * 5f);
            fontMicro.setColor(0f, 0f, 0f, 0.65f);
            fontMicro.draw(game.batch, labelWaiting, x+1.5f, y-1.5f);
            fontMicro.setColor(cr, cg, cb, f);
            fontMicro.draw(game.batch, labelWaiting, x, y);
        } else if (active) {
            float f = 0.78f + 0.22f * MathUtils.sin(time * 8f);
            String txt = labelActive + "  " + (int)(timer+1) + "s";
            fontMicro.setColor(0f, 0f, 0f, 0.65f);
            fontMicro.draw(game.batch, txt, x+1.5f, y-1.5f);
            fontMicro.setColor(cr, cg, cb, f);
            fontMicro.draw(game.batch, txt, x, y);
        } else {
            // Vuoto ma visibile: ricorda al giocatore che esiste lo slot
            fontMicro.setColor(0.28f, 0.28f, 0.42f, 0.55f);
            fontMicro.draw(game.batch, labelWaiting, x, y);
        }
    }



    /** HUD combo — usa fontCombo (scala 1.5 fissa) e fontMicro (scala 1.3 fissa). */
    private void drawComboHUD() {
        if (comboCount < 2) return;
        int multi = Math.max(1, comboCount / 3);

        // fontCombo ha scala 1.5 fissa
        fontCombo.setColor(1f, 0.85f, 0.1f, 1f);
        String comboStr = "COMBO x" + comboCount;
        layout.setText(fontCombo, comboStr);
        fontCombo.draw(game.batch, comboStr, W - layout.width - 20f, 160f);

        if (multi > 1) {
            // fontMicro ha scala 1.3 fissa
            fontMicro.setColor(1f, 0.5f, 0.1f, 1f);
            String multiStr = "BONUS x" + multi;
            layout.setText(fontMicro, multiStr);
            fontMicro.draw(game.batch, multiStr, W - layout.width - 20f, 134f);
        }
    }

    /** Popup "+score" che appare sopra il nemico eliminato con combo. */
    private void drawComboPopup(float delta) {
        if (comboPopTimer <= 0f || comboCount < 2) return;
        float alpha = Math.min(comboPopTimer / COMBO_POP_DURATION, 1f);
        float rise  = (1f - comboPopTimer / COMBO_POP_DURATION) * 60f;
                fontHUD.setColor(1f, 0.9f, 0.1f, alpha);
        String pop = "+" + comboPopScore;
        layout.setText(fontHUD, pop);
        fontHUD.draw(game.batch, pop, (W - layout.width) / 2f, H / 2f + 100f + rise);
                fontHUD.setColor(Color.WHITE);
    }

    /** Banner animato all'inizio di ogni ondata. */
    private void drawWaveBanner(float delta) {
        if (waveBannerTimer <= 0f) return;
        float alpha = Math.min(waveBannerTimer, 1f) * Math.min(WAVE_BANNER_DURATION - waveBannerTimer + 0.5f, 1f);
        alpha = MathUtils.clamp(alpha, 0f, 1f);
        float slide = (1f - Math.min(waveBannerTimer / 0.4f, 1f)) * 40f; // slide in dall'alto

        if (texWaveBanner != null) {
            game.batch.setColor(1f, 1f, 1f, alpha);
            game.batch.draw(texWaveBanner, 50f, H / 2f - 45f + slide, 620f, 90f);
            game.batch.setColor(Color.WHITE);
        }

        fontLarge.getData().setScale(waveManager.isBossWave() ? 2.6f : 3f);
        fontLarge.setColor(waveManager.isBossWave() ? 1f : 0.4f,
                           waveManager.isBossWave() ? 0.2f : 0.88f,
                           waveManager.isBossWave() ? 0.2f : 1f,
                           alpha);
        layout.setText(fontLarge, waveBannerText);
        fontLarge.draw(game.batch, waveBannerText,
            (W - layout.width) / 2f, H / 2f + 22f + slide);
            }

    private void drawBossBar() {
        if (enemies.isEmpty() || !(enemies.get(0) instanceof EnemyBoss)) return;
        EnemyBoss boss  = (EnemyBoss) enemies.get(0);
        float barW      = 420f;
        float barH      = 22f;
        float barX      = (W - barW) / 2f;
        float barY      = 28f;
        float fillPct   = MathUtils.clamp((float) boss.getHealth() / boss.getMaxHealth(), 0f, 1f);

        if (texBossBarBg != null)
            game.batch.draw(texBossBarBg, barX - 2f, barY - 2f, barW + 4f, barH + 4f);

        if (texBossBarFill != null) {
            int fillW = (int)(416f * fillPct);
            if (fillW > 0)
                game.batch.draw(texBossBarFill,
                    barX + 2f, barY + 2f, (float) fillW, 18f,
                    0, 0, fillW, 18, false, false);
        }

        // fontBoss ha scala 1.4 fissa
        fontBoss.setColor(1f, 0.3f, 0.3f, 1f);
        fontBoss.draw(game.batch, "BOSS", barX, barY + barH + 20f);

        fontMicro.setColor(0.8f, 0.8f, 1f, 1f);
        String ph = "Fase " + boss.getPhase() + "   " + boss.getHealth() + "/" + boss.getMaxHealth();
        layout.setText(fontMicro, ph);
        fontMicro.draw(game.batch, ph, barX + barW - layout.width, barY + barH + 20f);
    }

    private void drawEndScreen() {
        float pw = 580f, ph = 360f;
        float px = (W - pw) / 2f;
        float py = H / 2f - ph / 2f;
        if (texPanel != null) game.batch.draw(texPanel, px, py, pw, ph);

        if (gameOver) {
            float pulse = 1f + 0.04f * MathUtils.sin(endTimer * 4f);
                        fontLarge.setColor(1f, 0.12f, 0.12f, 1f);
            drawCentered(fontLarge, "GAME OVER", H / 2f + 100f);

                        fontHUD.setColor(0.9f, 0.9f, 1f, 1f);
            drawCentered(fontHUD, "Punteggio: " + player.getScore(), H / 2f + 40f);

            if (player.getScore() > 0 && player.getScore() >= game.scoreManager.getHighScore()) {
                float a = 0.5f + 0.5f * MathUtils.sin(endTimer * 5f);
                fontHUD.setColor(1f, 0.9f, 0.15f, a);
                drawCentered(fontHUD, "NUOVO RECORD!", H / 2f - 10f);
            }
        } else {
            float pulse = 1f + 0.03f * MathUtils.sin(endTimer * 3f);
                        fontLarge.setColor(0.25f, 1f, 0.45f, 1f);
            drawCentered(fontLarge, "HAI VINTO!", H / 2f + 100f);

                        fontHUD.setColor(0.9f, 0.9f, 1f, 1f);
            drawCentered(fontHUD, "Punteggio: " + player.getScore(), H / 2f + 40f);

            float a = 0.5f + 0.5f * MathUtils.sin(endTimer * 5f);
            fontHUD.setColor(1f, 0.9f, 0.15f, a);
            drawCentered(fontHUD, "OTTIMO LAVORO!", H / 2f - 10f);
        }

        int secsLeft = Math.max(0, (int)(END_DELAY - endTimer) + 1);
                fontSmall.setColor(0.5f, 0.5f, 0.72f, 1f);
        drawCentered(fontSmall, "Torno al menu tra " + secsLeft + "s...", py + 28f);

                            }

    /** ShapeRenderer: linea HUD, flash danno, barra doppio sparo. */
    private void drawShapeEffects(float delta) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // Linea HUD
        float la = 0.45f + 0.2f * MathUtils.sin(time * 2f);
        shapes.setColor(0.3f, 0.6f, 1f, la);
        shapes.rectLine(0f, H - 68f, W, H - 68f, 1.5f);

        // Flash danno (bordo rosso schermo)
        if (damageFlash > 0f) {
            float fa = damageFlash * 0.55f;
            float brd = 18f;
            shapes.setColor(1f, 0.05f, 0.05f, fa);
            shapes.rect(0f, 0f, W, brd);
            shapes.rect(0f, H - brd, W, brd);
            shapes.rect(0f, 0f, brd, H);
            shapes.rect(W - brd, 0f, brd, H);
        }



        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawCentered(BitmapFont font, String text, float y) {
        layout.setText(font, text);
        font.draw(game.batch, text, (W - layout.width) / 2f, y);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Screen lifecycle
    // ─────────────────────────────────────────────────────────────────────

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }

    @Override
    public void show() {
        // Riprende la musica corretta quando si torna dalla pausa
        if (!gameOver && !victory) {
            if (waveManager.isBossWave()) {
                game.audioManager.switchToBossMusic();
            } else {
                game.audioManager.switchToGameMusic();
            }
        }
    }

    @Override public void pause()  { game.audioManager.pauseMusic(); }
    @Override public void resume() { if (!gameOver && !victory) game.audioManager.resumeMusic(); }
    @Override public void hide()   { game.audioManager.pauseMusic(); }

    @Override
    public void dispose() {
        fontHUD.dispose(); fontLarge.dispose(); fontSmall.dispose();
        fontMicro.dispose(); fontCombo.dispose(); fontBoss.dispose();
        shapes.dispose();
        player.dispose();
        for (Enemy e   : enemies)  e.dispose();
        for (io.github.some_example_name.weapons.Bullet ob : orphanBullets) ob.dispose();
        for (PowerUp p : powerUps) p.dispose();
        if (texBackground  != null) texBackground.dispose();
        if (texHudBar      != null) texHudBar.dispose();
        if (texHeart       != null) texHeart.dispose();
        if (texBossBarBg   != null) texBossBarBg.dispose();
        if (texBossBarFill != null) texBossBarFill.dispose();
        if (texPanel       != null) texPanel.dispose();
        if (texWaveBanner  != null) texWaveBanner.dispose();
        if (texCombo       != null) texCombo.dispose();
        Bullet.disposeTextures();
    }
}
