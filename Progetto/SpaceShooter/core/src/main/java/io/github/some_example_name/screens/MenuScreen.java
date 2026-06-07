package io.github.some_example_name.screens;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
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

/**
 * Menu principale ispirato ad "Alien Invasion":
 * titolo grande centrato, pulsanti colorati uno per voce,
 * sfondo con stelle. Nessuna scritta sovrapposta.
 *
 * Coordinate virtuali: 720 × 1280 (Y=0 in basso)
 *
 * Layout dall'alto:
 *   Y=1130..1220  "SPACE"
 *   Y=1055..1125  "SHOOTER"
 *   Y=980..1050   gap
 *   Y=780..844    Pulsante GIOCA
 *   Y=710..774    Pulsante COME GIOCARE
 *   Y=640..704    Pulsante RECORD
 *   Y=570..634    Pulsante ESCI
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class MenuScreen implements Screen {

    private static final float W = GameScreen.W;   // 720
    private static final float H = GameScreen.H;   // 1280

    // ── Coordinate FISSE titolo ───────────────────────────────────────────
    // "SPACE": baseline a 1215, scala 5.5 → occupa ~70px verso il basso
    private static final float Y_SPACE   = 1215f;
    // "SHOOTER": baseline a 1128, scala 4.0 → occupa ~52px verso il basso
    private static final float Y_SHOOTER = 1128f;

    // ── Coordinate FISSE pulsanti ─────────────────────────────────────────
    // Pulsanti spostati più al centro rispetto alla versione precedente
    private static final float BTN_W   = 460f;
    private static final float BTN_H   = 66f;
    private static final float BTN_GAP = 20f;
    private static final float BTN_X   = (W - BTN_W) / 2f;   // 130
    // Primo pulsante: bordo SUPERIORE a Y=800 (zona centro-alta schermo)
    private static final float BTN_Y0  = 800f;

    // ── Voci e texture pulsanti ───────────────────────────────────────────
    private static final String[] LABELS   = {"GIOCA", "COME GIOCARE", "RECORD", "ESCI"};
    private static final String[] BTN_IMGS = {
        "images/btn_gioca.png",
        "images/btn_come.png",
        "images/btn_record.png",
        "images/btn_esci.png"
    };

    // ── Font — scale fisse, MAI modificate dopo il costruttore ────────────
    private final BitmapFont fSpace;    // "SPACE"   scale 5.5
    private final BitmapFont fShooter;  // "SHOOTER" scale 4.0
    private final BitmapFont fBtn;      // etichette scale 2.4
    private final BitmapFont fSmall;    // testi piccoli scale 1.4
    private final BitmapFont fBig;      // numero record scale 5.0

    private final GlyphLayout   gl;
    private final ShapeRenderer sr;

    // ── Texture ───────────────────────────────────────────────────────────
    private Texture   texBg;
    private Texture   texPanel;
    private Texture[] texBtns = new Texture[4];

    // ── Stelle ────────────────────────────────────────────────────────────
    private static final int NS = 130;
    private final float[] sx = new float[NS], sy = new float[NS],
                           ss = new float[NS], sv = new float[NS],
                           sb = new float[NS];

    // ── Stato ─────────────────────────────────────────────────────────────
    private final Main               game;
    private final OrthographicCamera cam;
    private final Viewport           vp;
    private int     sel        = 0;
    private float   time       = 0f;
    private float   inputCd    = 0f;
    private boolean showRecord = false;

    public MenuScreen(Main game) {
        this.game = game;
        cam = new OrthographicCamera();
        vp  = new FitViewport(W, H, cam);
        cam.position.set(W / 2f, H / 2f, 0f);
        cam.update();

        sr = new ShapeRenderer();
        gl = new GlyphLayout();

        fSpace   = mkF(5.5f);
        fShooter = mkF(4.0f);
        fBtn     = mkF(2.4f);
        fSmall   = mkF(1.4f);
        fBig     = mkF(5.0f);

        texBg    = loadTex("images/background.png");
        texPanel = loadTex("images/panel.png");
        for (int i = 0; i < 4; i++) texBtns[i] = loadTex(BTN_IMGS[i]);

        for (int i = 0; i < NS; i++) {
            sx[i] = MathUtils.random(W);
            sy[i] = MathUtils.random(H);
            if      (i < NS/3)   { sv[i]=MathUtils.random(7f,18f);  ss[i]=MathUtils.random(0.7f,1.3f); }
            else if (i < NS*2/3) { sv[i]=MathUtils.random(22f,48f); ss[i]=MathUtils.random(1.3f,2.2f); }
            else                 { sv[i]=MathUtils.random(55f,110f);ss[i]=MathUtils.random(2.2f,3.5f); }
            sb[i] = MathUtils.random(0.25f, 0.90f);
        }
    }

    private BitmapFont mkF(float s) { BitmapFont f = new BitmapFont(); f.getData().setScale(s); return f; }
    private Texture loadTex(String p) {
        try { return new Texture(Gdx.files.internal(p)); }
        catch (Exception e) { Gdx.app.error("Menu", p+": "+e.getMessage()); return null; }
    }

    // ─────────────────────────────────────────────────────────────────────
    // render
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        time += delta;
        if (inputCd > 0f) inputCd -= delta;
        handleInput();

        for (int i = 0; i < NS; i++) {
            sy[i] -= sv[i] * delta;
            if (sy[i] < -4f) { sy[i] = H+2f; sx[i] = MathUtils.random(W); }
        }

        Gdx.gl.glClearColor(0.01f, 0.01f, 0.055f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        vp.apply();
        cam.update();

        // Stelle
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < NS; i++) {
            float b = sb[i]*(0.55f+0.45f*MathUtils.sin(time*1.5f+i*0.55f));
            sr.setColor(b, b, Math.min(b*1.1f+0.03f,1f), 1f);
            sr.ellipse(sx[i]-ss[i]/2f, sy[i]-ss[i]/2f, ss[i], ss[i], 6);
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Batch
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        if (texBg != null) game.batch.draw(texBg, 0f, 0f, W, H);

        if (showRecord) drawRecord();
        else { drawTitle(); drawButtons(); }

        game.batch.end();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Titolo
    // ─────────────────────────────────────────────────────────────────────

    private void drawTitle() {
        // Ombra "SPACE"
        fSpace.setColor(0f, 0.02f, 0.18f, 0.75f);
        cx(fSpace, "SPACE", Y_SPACE + 3f);
        fSpace.setColor(0.25f, 0.72f, 1f, 1f);
        cx(fSpace, "SPACE", Y_SPACE);

        // Ombra "SHOOTER"
        fShooter.setColor(0f, 0.02f, 0.18f, 0.75f);
        cx(fShooter, "SHOOTER", Y_SHOOTER + 3f);
        fShooter.setColor(1f, 1f, 1f, 1f);
        cx(fShooter, "SHOOTER", Y_SHOOTER);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Pulsanti colorati
    // ─────────────────────────────────────────────────────────────────────

    private void drawButtons() {
        for (int i = 0; i < LABELS.length; i++) {
            float by = BTN_Y0 - i * (BTN_H + BTN_GAP);   // bordo superiore

            // Texture colorata specifica per ogni voce
            if (texBtns[i] != null) game.batch.draw(texBtns[i], BTN_X, by, BTN_W, BTN_H);

            // Bordo luminoso extra se selezionato
            if (i == sel) {
                // Piccole frecce animate
                float bounce = 5f * MathUtils.sin(time * 5.5f);
                fBtn.setColor(1f, 1f, 1f, 0.9f);
                fBtn.draw(game.batch, ">", BTN_X - 34f + bounce,      by + BTN_H * 0.67f);
                fBtn.draw(game.batch, "<", BTN_X + BTN_W + 6f - bounce, by + BTN_H * 0.67f);
            }

            // Testo centrato nel pulsante
            gl.setText(fBtn, LABELS[i]);
            float tx = BTN_X + (BTN_W - gl.width)  / 2f;
            float ty = by    + (BTN_H + gl.height) / 2f;

            // Ombra testo
            fBtn.setColor(0f, 0f, 0f, 0.55f);
            fBtn.draw(game.batch, LABELS[i], tx + 1.5f, ty - 1.5f);
            // Testo bianco sempre (su sfondo colorato)
            fBtn.setColor(1f, 1f, 1f, 1f);
            fBtn.draw(game.batch, LABELS[i], tx, ty);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Record panel
    // ─────────────────────────────────────────────────────────────────────

    private void drawRecord() {
        // Pannello: larghezza 480, altezza 340
        // py=470 (bordo inferiore), top=810
        // font.draw: Y = baseline (bordo superiore del testo)
        // testo scende dalla baseline di ~(13*scale) pixel
        //
        // Elementi dall'alto verso il basso:
        //  baseline 760: "PUNTEGGIO RECORD" (h≈31px → fino a 729)
        //  baseline 670: numero highscore   (h≈65px → fino a 605)
        //  baseline 593: "punti"            (h≈18px → fino a 575)
        //  Y=488..540:   pulsante TORNA     (bottom=488, top=540)
        //  baseline 558: messaggio zero     (se presente, tra punti e tasto)
        float pw = 480f, ph = 340f;
        float px = (W - pw) / 2f;
        float py = (H - ph) / 2f;

        if (texPanel != null) game.batch.draw(texPanel, px, py, pw, ph);

        // Titolo — baseline 760
        float titleY = py + ph - 50f;   // 470+340-50 = 760
        fBtn.setColor(0f, 0f, 0f, 0.55f);
        cx(fBtn, "PUNTEGGIO RECORD", titleY + 2f);
        fBtn.setColor(0.30f, 0.82f, 1f, 1f);
        cx(fBtn, "PUNTEGGIO RECORD", titleY);

        // Numero — baseline 670 (gap 90 dal titolo)
        String hs = String.valueOf(game.scoreManager.getHighScore());
        float numY = 670f;
        fBig.setColor(0f, 0f, 0f, 0.60f);
        cx(fBig, hs, numY + 3f);
        fBig.setColor(1f, 0.85f, 0.18f, 1f);
        cx(fBig, hs, numY);

        // "punti" — baseline 593 (= 670 - 65px altezza numero - 12px gap)
        float ptsY = numY - 65f - 12f;
        fSmall.setColor(0.50f, 0.50f, 0.72f, 1f);
        cx(fSmall, "punti", ptsY);

        // Pulsante TORNA — bordo inferiore a py+18=488, top=540
        float bw = 240f, bh = 52f;
        float bx = (W - bw) / 2f;
        float byr = py + 18f;
        if (texBtns[2] != null) game.batch.draw(texBtns[2], bx, byr, bw, bh);
        gl.setText(fBtn, "TORNA");
        fBtn.setColor(1f, 1f, 1f, 1f);
        fBtn.draw(game.batch, "TORNA",
            bx + (bw - gl.width) / 2f,
            byr + (bh + gl.height) / 2f);

        // Messaggio zero — baseline 558 (tra punti=575 e pulsante top=540)
        if (game.scoreManager.getHighScore() == 0) {
            fSmall.setColor(0.42f, 0.42f, 0.62f, 0.82f);
            cx(fSmall, "Nessuna partita ancora completata", py + 95f);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Utility
    // ─────────────────────────────────────────────────────────────────────

    private void cx(BitmapFont f, String text, float y) {
        gl.setText(f, text);
        f.draw(game.batch, text, (W - gl.width) / 2f, y);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Input
    // ─────────────────────────────────────────────────────────────────────

    private void handleInput() {
        if (showRecord) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched())
                showRecord = false;
            return;
        }

        boolean up   = Gdx.input.isKeyJustPressed(Input.Keys.UP)  || Gdx.input.isKeyJustPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyJustPressed(Input.Keys.DOWN)|| Gdx.input.isKeyJustPressed(Input.Keys.S);

        if (Controllers.getControllers().size > 0 && inputCd <= 0f) {
            Controller c = Controllers.getControllers().first();
            float ay = c.getAxis(1);
            if (ay < -0.5f) { up   = true; inputCd = 0.2f; }
            if (ay >  0.5f) { down = true; inputCd = 0.2f; }
        }

        if (up)   sel = (sel - 1 + LABELS.length) % LABELS.length;
        if (down) sel = (sel + 1) % LABELS.length;

        boolean ok = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                  || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        if (Controllers.getControllers().size > 0)
            ok |= Controllers.getControllers().first().getButton(0);

        if (Gdx.input.justTouched()) {
            float tx = Gdx.input.getX() / (float)Gdx.graphics.getWidth() * W;
            float ty = (1f - Gdx.input.getY() / (float)Gdx.graphics.getHeight()) * H;
            for (int i = 0; i < LABELS.length; i++) {
                float by = BTN_Y0 - i * (BTN_H + BTN_GAP);
                if (tx >= BTN_X && tx <= BTN_X + BTN_W && ty >= by && ty <= by + BTN_H) {
                    sel = i; ok = true; break;
                }
            }
        }

        if (ok) activate(sel);
    }

    private void activate(int idx) {
        switch (idx) {
            case 0: game.setScreen(new GameScreen(game));      break;
            case 1: game.setScreen(new HowToPlayScreen(game)); break;
            case 2: showRecord = true;                         break;
            case 3: Gdx.app.exit();                            break;
        }
    }

    @Override public void resize(int w, int h) { vp.update(w, h, true); }
    @Override public void show()   { sel=0; showRecord=false; game.audioManager.playMenuMusic(); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        fSpace.dispose(); fShooter.dispose(); fBtn.dispose(); fSmall.dispose(); fBig.dispose();
        sr.dispose();
        if (texBg    != null) texBg.dispose();
        if (texPanel != null) texPanel.dispose();
        for (Texture t : texBtns) if (t != null) t.dispose();
    }
}
