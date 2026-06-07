package io.github.some_example_name.utils;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Gestore centralizzato dell'audio del gioco.
 *
 * <p>Gestisce tre tracce musicali separate e sette effetti sonori.
 * Il cambio tra musica di gioco e musica boss avviene tramite
 * {@link #switchToBossMusic()} e {@link #switchToGameMusic()},
 * che applicano un cross-fade immediato (stop + play).</p>
 *
 * <p>Tutti i caricamenti sono protetti da {@code try-catch}:
 * un file mancante non causa crash ma viene ignorato silenziosamente.</p>
 *
 * <p>Tracce musicali:</p>
 * <ul>
 *   <li>{@code musica_menu.ogg}  — menu principale (loop)</li>
 *   <li>{@code musica_gioco.ogg} — gioco normale (loop)</li>
 *   <li>{@code musica_boss.ogg}  — wave boss (loop)</li>
 * </ul>
 *
 * <p>Effetti sonori:</p>
 * <ul>
 *   <li>{@code esplosione.ogg}  — nemico eliminato</li>
 *   <li>{@code enemy_bullet.mp3}— sparo dei nemici</li>
 *   <li>{@code damage.ogg}      — danno al giocatore</li>
 *   <li>{@code boss_damage.ogg} — danno al boss</li>
 *   <li>{@code death.ogg}       — game over</li>
 *   <li>{@code fine.ogg}        — vittoria</li>
 *   <li>{@code wave.ogg}        — annuncio nuova ondata</li>
 * </ul>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class AudioManager {

    // ── Tracce musicali ───────────────────────────────────────────────────
    private Music musicMenu;
    private Music musicGame;
    private Music musicBoss;

    /** Traccia attualmente attiva (puntatore a una delle tre sopra). */
    private Music currentMusic;

    // ── Effetti sonori ────────────────────────────────────────────────────
    private Sound sfxExplosion;
    private Sound sfxEnemyBullet;
    private Sound sfxDamage;
    private Sound sfxBossDamage;
    private Sound sfxDeath;
    private Sound sfxVictory;
    private Sound sfxWave;

    // ── Volumi ────────────────────────────────────────────────────────────
    private float musicVolume = 0.50f;
    private float sfxVolume   = 0.85f;

    /**
     * Inizializza e carica tutte le risorse audio.
     */
    public AudioManager() {
        loadMusic();
        loadSfx();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Caricamento
    // ─────────────────────────────────────────────────────────────────────

    private void loadMusic() {
        musicMenu = loadMusicFile("sounds/musica_menu.ogg", true);
        musicGame = loadMusicFile("sounds/musica_gioco.ogg", true);
        musicBoss = loadMusicFile("sounds/musica_boss.ogg", true);
    }

    private void loadSfx() {
        sfxExplosion  = loadSoundFile("sounds/esplosione.ogg");
        sfxEnemyBullet = loadSoundFile("sounds/enemy_bullet.ogg");
        sfxDamage     = loadSoundFile("sounds/damage.ogg");
        sfxBossDamage = loadSoundFile("sounds/boss_damage.ogg");
        sfxDeath      = loadSoundFile("sounds/death.ogg");
        sfxVictory    = loadSoundFile("sounds/fine.ogg");
        sfxWave       = loadSoundFile("sounds/wave.ogg");
    }

    private Music loadMusicFile(String path, boolean loop) {
        try {
            Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
            m.setLooping(loop);
            m.setVolume(musicVolume);
            return m;
        } catch (Exception e) {
            Gdx.app.error("AudioManager", path + ": " + e.getMessage());
            return null;
        }
    }

    private Sound loadSoundFile(String path) {
        try {
            return Gdx.audio.newSound(Gdx.files.internal(path));
        } catch (Exception e) {
            Gdx.app.error("AudioManager", path + ": " + e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Controllo musica
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Avvia la musica del menu principale.
     * Ferma qualsiasi altra traccia in riproduzione.
     */
    public void playMenuMusic() {
        switchTo(musicMenu);
    }

    /**
     * Avvia la musica di gioco normale.
     * Ferma qualsiasi altra traccia in riproduzione.
     */
    public void playMusic() {
        switchTo(musicGame);
    }

    /**
     * Passa alla musica del boss, fermando la traccia normale.
     * Chiamare quando inizia una boss wave.
     */
    public void switchToBossMusic() {
        switchTo(musicBoss);
    }

    /**
     * Torna alla musica di gioco normale, fermando la musica boss.
     * Chiamare al termine di una boss wave (boss sconfitto).
     */
    public void switchToGameMusic() {
        switchTo(musicGame);
    }

    /**
     * Ferma la traccia corrente senza liberare le risorse.
     */
    public void stopMusic() {
        if (currentMusic != null) currentMusic.stop();
    }

    /**
     * Mette in pausa la traccia corrente (riprende dal punto corrente).
     */
    public void pauseMusic() {
        if (currentMusic != null) currentMusic.pause();
    }

    /**
     * Riprende la traccia corrente dalla posizione in cui era stata messa in pausa.
     */
    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) currentMusic.play();
    }

    /** Cambia traccia: ferma quella corrente e avvia la nuova. */
    private void switchTo(Music next) {
        if (next == null) return;
        if (currentMusic != null && currentMusic != next) currentMusic.stop();
        currentMusic = next;
        currentMusic.setVolume(musicVolume);
        if (!currentMusic.isPlaying()) currentMusic.play();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Effetti sonori
    // ─────────────────────────────────────────────────────────────────────

    /** Esplosione nemico base eliminato. */
    public void playExplosion() {
        play(sfxExplosion, sfxVolume);
    }

    /** Sparo proiettile nemico (volume ridotto per non coprire tutto). */
    public void playEnemyBullet() {
        play(sfxEnemyBullet, sfxVolume * 0.45f);
    }

    /** Danno subito dal giocatore. */
    public void playHit() {
        play(sfxDamage, sfxVolume);
    }

    /** Danno inflitto al boss. */
    public void playBossDamage() {
        play(sfxBossDamage, sfxVolume * 0.9f);
    }

    /** Suono game over / morte del giocatore. */
    public void playDeath() {
        play(sfxDeath, sfxVolume);
    }

    /** Suono vittoria (completamento di tutte le ondate). */
    public void playVictory() {
        play(sfxVictory, sfxVolume);
    }

    /** Suono raccolta power-up (usa wave.ogg). */
    public void playPowerUpCollect() {
        play(sfxWave, sfxVolume * 0.65f);
    }

    /** Annuncio nuova ondata. */
    public void playWave() {
        play(sfxWave, sfxVolume * 0.8f);
    }

    /**
     * Compatibilità con chiamate precedenti: riproduce il suono di sparo nemico.
     * @deprecated Usare {@link #playEnemyBullet()} per chiarezza.
     */
    @Deprecated
    public void playShoot() {
        playEnemyBullet();
    }

    /**
     * Compatibilità con chiamate precedenti: riproduce l'esplosione.
     * @deprecated Usare {@link #playExplosion()} direttamente.
     */
    @Deprecated
    public void playPowerUp() {
        play(sfxWave, sfxVolume * 0.7f); // usa wave.ogg anche per i power-up
    }

    private void play(Sound s, float vol) {
        if (s != null) s.play(vol);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Volume
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Imposta il volume delle musiche (0.0 – 1.0).
     *
     * @param v volume da applicare
     */
    public void setMusicVolume(float v) {
        musicVolume = Math.max(0f, Math.min(1f, v));
        if (musicMenu != null) musicMenu.setVolume(musicVolume);
        if (musicGame != null) musicGame.setVolume(musicVolume);
        if (musicBoss != null) musicBoss.setVolume(musicVolume);
    }

    /**
     * Imposta il volume degli effetti sonori (0.0 – 1.0).
     *
     * @param v volume da applicare
     */
    public void setSfxVolume(float v) {
        sfxVolume = Math.max(0f, Math.min(1f, v));
    }

    // ─────────────────────────────────────────────────────────────────────
    // Dispose
    // ─────────────────────────────────────────────────────────────────────

    /** Libera tutte le risorse audio. */
    public void dispose() {
        if (musicMenu != null) musicMenu.dispose();
        if (musicGame != null) musicGame.dispose();
        if (musicBoss != null) musicBoss.dispose();

        if (sfxExplosion   != null) sfxExplosion.dispose();
        if (sfxEnemyBullet != null) sfxEnemyBullet.dispose();
        if (sfxDamage      != null) sfxDamage.dispose();
        if (sfxBossDamage  != null) sfxBossDamage.dispose();
        if (sfxDeath       != null) sfxDeath.dispose();
        if (sfxVictory     != null) sfxVictory.dispose();
        if (sfxWave        != null) sfxWave.dispose();
    }
}
