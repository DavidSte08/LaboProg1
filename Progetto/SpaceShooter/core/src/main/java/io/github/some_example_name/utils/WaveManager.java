package io.github.some_example_name.utils;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import io.github.some_example_name.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera le ondate di nemici con formazioni e composizioni diverse.
 *
 * <p>Ogni ondata normale usa una delle seguenti formazioni:</p>
 * <ul>
 *   <li><strong>LINEA</strong> — nemici in fila orizzontale</li>
 *   <li><strong>V</strong> — formazione a V con il vertice al centro</li>
 *   <li><strong>PINZA</strong> — due gruppi ai lati che si avvicinano</li>
 *   <li><strong>SERPENTE</strong> — nemici sfalsati in diagonale</li>
 *   <li><strong>GRIGLIA</strong> — matrice 2 righe × N colonne</li>
 *   <li><strong>MISTA</strong> — mix di tipi diversi di nemico</li>
 * </ul>
 *
 * <p>Con l'aumentare della wave, il numero di nemici e la difficoltà crescono
 * e compaiono tipi di nemico più pericolosi (Zigzag, Sweeper, Diver).</p>
 *
 * <p>Ogni 5 ondate viene generato un {@link EnemyBoss}.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class WaveManager {

    private int   currentWave;
    private boolean bossWave;

    private final float  screenW;
    private final float  screenH;
    private final Random rng;

    /** X del giocatore aggiornata da GameScreen per i Diver. */
    private float playerX;

    /**
     * Crea un WaveManager.
     *
     * @param screenW larghezza virtuale
     * @param screenH altezza virtuale
     */
    public WaveManager(float screenW, float screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.rng     = new Random();
        this.playerX = screenW / 2f; // default centro
    }

    /**
     * Aggiorna la posizione X del giocatore usata dai Diver.
     *
     * @param px X corrente del giocatore
     */
    public void setPlayerX(float px) { this.playerX = px; }

    /**
     * Genera la prossima ondata.
     *
     * @return lista di nemici (mai {@code null})
     */
    public List<Enemy> nextWave() {
        currentWave++;
        bossWave = (currentWave % 5 == 0);
        List<Enemy> list = new ArrayList<>();

        if (bossWave) {
            list.add(new EnemyBoss(screenW, screenH));
        } else {
            buildNormalWave(list);
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Costruzione ondata normale
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Sceglie e costruisce una formazione in base alla wave corrente.
     * Le prime wave usano formazioni semplici; quelle avanzate introducono
     * nemici speciali e formazioni più complesse.
     */
    private void buildNormalWave(List<Enemy> list) {
        // Difficoltà base cresce con la wave (da 0 a ~1.0 alla wave 9)
        float difficulty = Math.min((currentWave - 1) / 8f, 1f);

        // Scegli formazione in base alla wave
        int formation = (currentWave - 1) % 6; // ruota tra 6 formazioni

        switch (formation) {
            case 0: buildLine(list, difficulty);    break;
            case 1: buildV(list, difficulty);       break;
            case 2: buildPincer(list, difficulty);  break;
            case 3: buildSnake(list, difficulty);   break;
            case 4: buildGrid(list, difficulty);    break;
            case 5: buildMixed(list, difficulty);   break;
        }
    }

    // ── Formazione LINEA ──────────────────────────────────────────────────

    /**
     * Fila orizzontale. Nelle wave avanzate usa Zigzag al posto dei Basic.
     */
    private void buildLine(List<Enemy> list, float diff) {
        int count   = baseCount(diff);
        float spacing = screenW / (count + 1f);

        for (int i = 0; i < count; i++) {
            float ex = spacing * (i + 1f) - 24f;
            float ey = screenH - 80f - rng.nextInt(60);

            if (diff >= 0.5f && i % 3 == 1) {
                // Ogni terzo nemico è uno Zigzag nelle wave alte
                list.add(new EnemyZigzag(ex, ey, screenW, i % 2 == 0));
            } else {
                list.add(new EnemyBasic(ex, ey, 1f, 1f, 1f));
            }
        }
    }

    // ── Formazione V ──────────────────────────────────────────────────────

    /**
     * Formazione a V: il centro parte più in basso, le ali più in alto.
     * Crea un effetto "freccia" che si apre verso il giocatore.
     */
    private void buildV(List<Enemy> list, float diff) {
        int arms    = 3 + (int)(diff * 3); // 3-6 per lato
        float cx    = screenW / 2f;
        float baseY = screenH - 100f;

        for (int i = 0; i < arms; i++) {
            // Braccio sinistro
            float lx = cx - (i + 1) * (screenW / (arms * 2 + 2f));
            float ly = baseY - i * 60f;
            list.add(new EnemyBasic(lx, ly, 0.7f, 0.9f, 1f)); // azzurrino

            // Braccio destro (speculare)
            float rx = cx + (i + 1) * (screenW / (arms * 2 + 2f));
            list.add(new EnemyBasic(rx, ly, 0.7f, 0.9f, 1f));
        }
        // Punta della V: uno Sweeper nelle wave alte
        if (diff >= 0.35f) {
            list.add(new EnemySweeper(cx - 26f, baseY + 40f));
        } else {
            list.add(new EnemyBasic(cx - 24f, baseY + 40f));
        }
    }

    // ── Formazione PINZA ──────────────────────────────────────────────────

    /**
     * Due gruppi ai lati dello schermo che scendono in parallelo,
     * dando l'impressione di stringersi intorno al giocatore.
     */
    private void buildPincer(List<Enemy> list, float diff) {
        int perSide = 2 + (int)(diff * 3); // 2-5 per lato
        float gapX  = screenW * 0.08f;

        for (int i = 0; i < perSide; i++) {
            float ey   = screenH - 80f - i * 80f;
            float leftX  = gapX + i * 18f;
            float rightX = screenW - gapX - 48f - i * 18f;

            // Lato sinistro: Zigzag che va verso destra
            list.add(new EnemyZigzag(leftX,  ey, screenW, true));
            // Lato destro: Zigzag che va verso sinistra
            list.add(new EnemyZigzag(rightX, ey, screenW, false));
        }

        // Centro: un Sweeper se la wave è avanzata
        if (diff >= 0.6f) {
            list.add(new EnemySweeper(screenW / 2f - 26f, screenH - 80f));
        }
    }

    // ── Formazione SERPENTE ───────────────────────────────────────────────

    /**
     * Nemici disposti in diagonale alternata, come le vertebre di un serpente.
     * Scendono in momenti diversi creando un flusso continuo difficile da evitare.
     */
    private void buildSnake(List<Enemy> list, float diff) {
        int count  = 6 + (int)(diff * 6); // 6-12
        float step = screenW / (count / 2f);

        for (int i = 0; i < count; i++) {
            float ex = (i % 2 == 0)
                ? (i / 2f) * step
                : screenW - (i / 2f) * step - 48f;
            float ey = screenH - 80f - (i * 55f);

            // Nelle wave alte il serpente ha qualche Diver
            if (diff >= 0.7f && i % 4 == 3) {
                list.add(new EnemyDiver(ex, ey, screenH, playerX));
            } else {
                // Tinta che degrada dal rosso all'arancione lungo il serpente
                float t  = (float) i / count;
                list.add(new EnemyBasic(ex, ey, 1f, 0.4f + t * 0.5f, 0.1f));
            }
        }
    }

    // ── Formazione GRIGLIA ────────────────────────────────────────────────

    /**
     * Matrice a 2 righe con spaziatura uniforme.
     * Nella seconda riga i nemici partono più in alto creando profondità.
     */
    private void buildGrid(List<Enemy> list, float diff) {
        int cols    = 3 + (int)(diff * 3); // 3-6 colonne
        cols        = Math.min(cols, 7);
        float spacing = screenW / (cols + 1f);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < cols; col++) {
                float ex = spacing * (col + 1f) - 24f;
                float ey = screenH - 80f - row * 110f;

                // Seconda riga: Sweeper nelle wave alte, Basic in quelle basse
                if (row == 1 && diff >= 0.45f) {
                    list.add(new EnemySweeper(ex - 2f, ey));
                } else {
                    // Tinta: prima riga bianca, seconda riga rosa
                    float r = row == 0 ? 1f : 1f;
                    float g = row == 0 ? 1f : 0.5f;
                    float b = row == 0 ? 1f : 0.8f;
                    list.add(new EnemyBasic(ex, ey, r, g, b));
                }
            }
        }
    }

    // ── Formazione MISTA ──────────────────────────────────────────────────

    /**
     * Mix libero di tutti i tipi di nemico disponibili in base alla difficoltà.
     * È la formazione più caotica e imprevedibile.
     */
    private void buildMixed(List<Enemy> list, float diff) {
        int count = baseCount(diff);

        for (int i = 0; i < count; i++) {
            float ex = rng.nextFloat() * (screenW - 60f) + 10f;
            float ey = screenH - 80f - rng.nextInt(180);

            // Probabilità dei tipi in base alla difficoltà
            float roll = rng.nextFloat();

            if (diff >= 0.7f && roll < 0.20f) {
                list.add(new EnemyDiver(ex, ey, screenH, playerX));
            } else if (diff >= 0.4f && roll < 0.40f) {
                list.add(new EnemySweeper(ex, ey));
            } else if (diff >= 0.25f && roll < 0.60f) {
                list.add(new EnemyZigzag(ex, ey, screenW, rng.nextBoolean()));
            } else {
                // Basic con tinta casuale tra rosso/viola/ciano
                float[][] tints = {
                    {1f, 1f, 1f},   // bianco
                    {1f, 0.5f, 0.5f}, // rosato
                    {0.6f, 0.6f, 1f}, // bluastro
                    {1f, 0.8f, 0.3f}, // giallo
                };
                float[] t = tints[rng.nextInt(tints.length)];
                list.add(new EnemyBasic(ex, ey, t[0], t[1], t[2]));
            }
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────

    /**
     * Calcola il numero base di nemici proporzionale alla difficoltà.
     *
     * @param diff valore 0-1 che rappresenta l'avanzamento della partita
     * @return numero di nemici (min 4, max 14)
     */
    private int baseCount(float diff) {
        return Math.min(4 + (int)(diff * 10f), 14);
    }

    /** @return numero ondata corrente (1-based) */
    public int getCurrentWave() { return currentWave; }

    /** @return {@code true} se l'ondata corrente contiene un boss */
    public boolean isBossWave() { return bossWave; }
}
