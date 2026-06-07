package io.github.some_example_name;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test per la logica di gioco di Space Shooter.
 *
 * <p>Testa le classi principali ({@link ScoreManagerMock}, {@link PlayerMock},
 * {@link WaveManagerMock}) senza dipendere da LibGDX o da un contesto grafico.
 * Tutti i test usano classi mock interne che replicano la logica reale.</p>
 *
 * <p>Per eseguire: {@code ./gradlew core:test}</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class GameLogicTest {

    // ════════════════════════════════════════════════════════════════════
    // Mock: ScoreManager
    // ════════════════════════════════════════════════════════════════════

    /** Mock di ScoreManager che non usa Gdx.files. */
    static class ScoreManagerMock {
        private int highScore = 0;

        public int getHighScore() { return highScore; }

        public void submitScore(int score) {
            if (score > highScore) highScore = score;
        }

        public void reset() { highScore = 0; }
    }

    @Test
    public void testHighscoreIniziale() {
        ScoreManagerMock sm = new ScoreManagerMock();
        assertEquals("Highscore iniziale deve essere 0", 0, sm.getHighScore());
    }

    @Test
    public void testSubmitScoreMaggiore() {
        ScoreManagerMock sm = new ScoreManagerMock();
        sm.submitScore(1000);
        assertEquals(1000, sm.getHighScore());
    }

    @Test
    public void testSubmitScoreMinoreNonSovrascrive() {
        ScoreManagerMock sm = new ScoreManagerMock();
        sm.submitScore(5000);
        sm.submitScore(2000);
        assertEquals("Il record non deve scendere", 5000, sm.getHighScore());
    }

    @Test
    public void testSubmitScoreUguale() {
        ScoreManagerMock sm = new ScoreManagerMock();
        sm.submitScore(3000);
        sm.submitScore(3000);
        assertEquals(3000, sm.getHighScore());
    }

    @Test
    public void testReset() {
        ScoreManagerMock sm = new ScoreManagerMock();
        sm.submitScore(9999);
        sm.reset();
        assertEquals("Dopo il reset l'highscore deve essere 0", 0, sm.getHighScore());
    }

    @Test
    public void testSubmitScoreSequenziale() {
        ScoreManagerMock sm = new ScoreManagerMock();
        sm.submitScore(100);
        sm.submitScore(500);
        sm.submitScore(300);
        sm.submitScore(800);
        sm.submitScore(600);
        assertEquals("Deve conservare solo il massimo", 800, sm.getHighScore());
    }

    // ════════════════════════════════════════════════════════════════════
    // Mock: Player
    // ════════════════════════════════════════════════════════════════════

    /** Mock di Player senza LibGDX. */
    static class PlayerMock {
        private int     lives  = 3;
        private int     score  = 0;
        private boolean alive  = true;
        private static final int MAX_LIVES = 5;

        public int     getLives() { return lives; }
        public int     getScore() { return score; }
        public boolean isAlive()  { return alive; }

        public void addScore(int pts) {
            if (pts < 0) throw new IllegalArgumentException("Punti negativi: " + pts);
            score += pts;
        }

        public boolean takeDamage() {
            if (!alive) throw new IllegalStateException("Il giocatore è già morto");
            lives--;
            if (lives <= 0) { lives = 0; alive = false; return false; }
            return true;
        }

        public void addLife() {
            if (lives < MAX_LIVES) lives++;
        }
    }

    @Test
    public void testScoreIniziale() {
        PlayerMock p = new PlayerMock();
        assertEquals(0, p.getScore());
    }

    @Test
    public void testAddScore() {
        PlayerMock p = new PlayerMock();
        p.addScore(100);
        p.addScore(250);
        assertEquals(350, p.getScore());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddScoreNegativo() {
        new PlayerMock().addScore(-1);
    }

    @Test
    public void testTakeDamageSopravvive() {
        PlayerMock p = new PlayerMock();
        assertTrue("Con 3 vite, primo danno → sopravvive", p.takeDamage());
        assertEquals(2, p.getLives());
        assertTrue(p.takeDamage());
        assertEquals(1, p.getLives());
    }

    @Test
    public void testTakeDamageMuore() {
        PlayerMock p = new PlayerMock();
        p.takeDamage(); p.takeDamage();
        assertFalse("Terzo danno → muore", p.takeDamage());
        assertEquals(0, p.getLives());
        assertFalse(p.isAlive());
    }

    @Test(expected = IllegalStateException.class)
    public void testDannoSuGiocatoreGiaMorto() {
        PlayerMock p = new PlayerMock();
        p.takeDamage(); p.takeDamage(); p.takeDamage();
        p.takeDamage(); // deve lanciare IllegalStateException
    }

    @Test
    public void testAddLife() {
        PlayerMock p = new PlayerMock();
        p.takeDamage(); // 2 vite
        p.addLife();
        assertEquals(3, p.getLives());
    }

    @Test
    public void testAddLifeLimiteMax() {
        PlayerMock p = new PlayerMock();
        for (int i = 0; i < 10; i++) p.addLife();
        assertEquals("Non deve superare MAX_LIVES=5", 5, p.getLives());
    }

    // ════════════════════════════════════════════════════════════════════
    // Mock: WaveManager
    // ════════════════════════════════════════════════════════════════════

    /** Mock di WaveManager senza LibGDX. */
    static class WaveManagerMock {
        private int     currentWave = 0;
        private boolean bossWave    = false;

        public void nextWave() {
            currentWave++;
            bossWave = (currentWave % 5 == 0);
        }

        public int     getCurrentWave() { return currentWave; }
        public boolean isBossWave()     { return bossWave; }
    }

    @Test
    public void testWave1NonBoss() {
        WaveManagerMock wm = new WaveManagerMock();
        wm.nextWave();
        assertEquals(1, wm.getCurrentWave());
        assertFalse("Wave 1 non è boss", wm.isBossWave());
    }

    @Test
    public void testWave5Boss() {
        WaveManagerMock wm = new WaveManagerMock();
        for (int i = 0; i < 5; i++) wm.nextWave();
        assertEquals(5, wm.getCurrentWave());
        assertTrue("Wave 5 è boss", wm.isBossWave());
    }

    @Test
    public void testWave10Boss() {
        WaveManagerMock wm = new WaveManagerMock();
        for (int i = 0; i < 10; i++) wm.nextWave();
        assertEquals(10, wm.getCurrentWave());
        assertTrue("Wave 10 è boss", wm.isBossWave());
    }

    @Test
    public void testWave3NonBoss() {
        WaveManagerMock wm = new WaveManagerMock();
        for (int i = 0; i < 3; i++) wm.nextWave();
        assertFalse("Wave 3 non è boss", wm.isBossWave());
    }

    @Test
    public void testContatoreOndate() {
        WaveManagerMock wm = new WaveManagerMock();
        for (int i = 1; i <= 10; i++) {
            wm.nextWave();
            assertEquals("Wave corrente deve essere " + i, i, wm.getCurrentWave());
        }
    }
}
