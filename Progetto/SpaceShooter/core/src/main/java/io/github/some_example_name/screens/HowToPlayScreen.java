package io.github.some_example_name.screens;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
 * Schermata "Come Giocare" — 3 pagine navigabili con layout fisso.
 * Ispirata alla schermata GUIDA di "Alien Invasion":
 * titolo grande giallo in cima, sezioni con titoletti colorati,
 * testo bianco chiaro ben spaziato, pulsante INDIETRO in basso.
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class HowToPlayScreen implements Screen {

    private static final float W = GameScreen.W;
    private static final float H = GameScreen.H;

    private final Main               game;
    private final OrthographicCamera cam;
    private final Viewport           vp;
    private final GlyphLayout        gl;
    private final ShapeRenderer      sr;

    // Font — scale FISSE
    private final BitmapFont fPageTitle;  // "CONTROLLI" ecc.  scale 3.8
    private final BitmapFont fSection;    // titoletti sezione  scale 2.2
    private final BitmapFont fBody;       // testo corpo        scale 1.75
    private final BitmapFont fNav;        // frecce nav         scale 3.0
    private final BitmapFont fBtn;        // pulsante torna     scale 2.2

    private Texture texBg, texBtnBack;

    // ── Pagine ────────────────────────────────────────────────────────────
    private static final int PAGES = 3;
    private int   page  = 0;
    private float time  = 0f;

    // ── Struttura pagine ──────────────────────────────────────────────────
    // Ogni pagina: titolo + array di sezioni
    // Ogni sezione: [titolo_sezione, riga1, riga2, riga3, ...]
    // Una riga vuota "" = spazio aggiuntivo
    private static final String[]   PAGE_TITLES = {
        "CONTROLLI",
        "NEMICI & ONDATE",
        "POWER-UP & PUNTEGGIO"
    };

    // Colori titoletti sezione per ogni pagina (R,G,B in 0-1)
    private static final float[][] SEC_COLORS = {
        {0.3f, 0.85f, 1f},     // azzurro
        {1f, 0.85f, 0.2f},     // giallo
        {0.5f, 1f, 0.5f},      // verde
    };

    // Contenuto pagine: array di stringhe — titolo sezione in [MAIUSCOLO], corpo in minuscolo
    private static final String[][] PAGE0 = {
        {"MOVIMENTO",    "Frecce  /  WASD   --   Muovi la navicella"},
        {"SPARO",        "SPAZIO   --   Spara verso l'alto"},
        {"POWER-UP",     "Q   --   Attiva doppio sparo (slot 1)",
                         "E   --   Attiva rallentamento nemici (slot 2)"},
        {"PAUSA",        "ESC  o  P   --   Mette il gioco in pausa"},
        {"CONTROLLER",   "Stick sinistro: muovi      Tasto A: spara",
                         "Tasto B: attiva Q          Tasto X: attiva E"},
    };

    private static final String[][] PAGE1 = {
        {"NEMICO BASE  (rosso)",    "Scende a sinusoide -- 1 vita -- 100 punti"},
        {"ZIGZAG  (arancione)",     "Rimbalza ai bordi laterali -- 150 punti"},
        {"SWEEPER  (verde)",        "Lento, spara ventaglio di 5 proiettili -- 200 pt"},
        {"DIVER  (viola)",          "Si ferma e poi si lancia in picchiata su di te -- 250 pt"},
        {"BOSS  (ogni 5 ondate)",   "3 fasi crescenti: sparo singolo -- ventaglio -- cerchio",
                                    "5000 punti"},
    };

    private static final String[][] PAGE2 = {
        {"VITA  ",              "Raccogliere aggiunge subito 1 vita (massimo 5)"},
        {"DOPPIO SPARO  ",     "Va nello Slot 1 -- premi Q per attivarlo (7 secondi)"},
        {"RALLENTA NEMICI  ",  "Va nello Slot 2 -- premi E per attivarlo (6 secondi)",
                                 "I nemici rallentano al 28% della velocità normale"},
        {"COMBO",                "Elimina nemici in rapida sequenza per moltiplicare i punti"},
        {"RECORD",               "Il punteggio massimo si salva automaticamente sul file"},
    };

    private static final String[][][] PAGES_CONTENT = {PAGE0, PAGE1, PAGE2};

    // Layout verticale fisso
    // Y coordinate:
    private static final float Y_TITLE   = 1210f;  // baseline titolo pagina
    private static final float Y_START   = 1120f;  // Y da cui inizia il primo blocco
    private static final float SEC_GAP   = 28f;    // spazio tra fine blocco e titolo sezione successiva
    private static final float BODY_GAP  = 26f;    // spazio tra righe corpo
    private static final float TITLE_GAP = 32f;    // spazio tra titolo sezione e prima riga corpo
    private static final float LEFT_X    = 52f;    // margine sinistro

    public HowToPlayScreen(Main game) {
        this.game = game;
        cam = new OrthographicCamera();
        vp  = new FitViewport(W, H, cam);
        cam.position.set(W / 2f, H / 2f, 0f);
        cam.update();

        gl = new GlyphLayout();
        sr = new ShapeRenderer();

        fPageTitle = mkF(3.8f);
        fSection   = mkF(2.1f);
        fBody      = mkF(1.72f);
        fNav       = mkF(3.0f);
        fBtn       = mkF(2.1f);

        texBg      = loadTex("images/background.png");
        texBtnBack = loadTex("images/btn_normal.png");
    }

    private BitmapFont mkF(float s) { BitmapFont f = new BitmapFont(); f.getData().setScale(s); return f; }
    private Texture loadTex(String p) {
        try { return new Texture(Gdx.files.internal(p)); }
        catch (Exception e) { Gdx.app.error("HowTo", p+": "+e.getMessage()); return null; }
    }

    @Override
    public void render(float delta) {
        time += delta;
        handleInput();

        Gdx.gl.glClearColor(0.01f, 0.01f, 0.055f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        vp.apply();
        cam.update();

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        if (texBg != null) game.batch.draw(texBg, 0f, 0f, W, H);

        drawPageTitle();
        drawContent();
        drawNavigation();
        drawBackButton();

        game.batch.end();

        drawPageDots();
    }

    // ── Titolo pagina ──────────────────────────────────────────────────────

    private void drawPageTitle() {
        // Ombra
        fPageTitle.setColor(0f, 0f, 0f, 0.65f);
        cx(fPageTitle, PAGE_TITLES[page], Y_TITLE + 3f);
        // Colore giallo/oro come nell'esempio
        fPageTitle.setColor(1f, 0.9f, 0.2f, 1f);
        cx(fPageTitle, PAGE_TITLES[page], Y_TITLE);
    }

    // ── Contenuto ─────────────────────────────────────────────────────────

    private void drawContent() {
        String[][] sections = PAGES_CONTENT[page];
        float[] sc = SEC_COLORS[page % SEC_COLORS.length];

        float y = Y_START;

        for (String[] section : sections) {
            if (y < 215f) break;  // non scende sotto la zona nav

            // Titoletto sezione
            fSection.setColor(sc[0], sc[1], sc[2], 1f);
            fSection.draw(game.batch, section[0], LEFT_X, y);

            y -= TITLE_GAP;

            // Righe corpo
            for (int r = 1; r < section.length; r++) {
                if (y < 215f) break;
                fBody.setColor(0.88f, 0.88f, 0.96f, 1f);
                fBody.draw(game.batch, section[r], LEFT_X + 20f, y);
                y -= BODY_GAP;
            }

            y -= SEC_GAP;
        }
    }

    // ── Navigazione ────────────────────────────────────────────────────────

    private void drawNavigation() {
        // Frecce e indicatore: baseline 198
        // batch.draw testo: Y=baseline (testo scende di ~26px)
        // Frecce: 198..172  Indicatore "1/3": 198..180
        float bounce = 5f * MathUtils.sin(time * 4f);

        if (page > 0) {
            fNav.setColor(0.3f, 0.8f, 1f, 1f);
            fNav.draw(game.batch, "<", 24f - bounce, 198f);
        }
        if (page < PAGES - 1) {
            fNav.setColor(0.3f, 0.8f, 1f, 1f);
            gl.setText(fNav, ">");
            fNav.draw(game.batch, ">", W - 24f - gl.width + bounce, 198f);
        }

        // Indicatore "1 / 3" centrato, stessa riga delle frecce
        fBody.setColor(0.45f, 0.50f, 0.70f, 1f);
        cx(fBody, (page+1) + " / " + PAGES, 198f);
    }

    // ── Pulsante indietro ─────────────────────────────────────────────────

    private void drawBackButton() {
        // Pulsante: bottom=22, top=80
        float bw = 280f, bh = 58f;
        float bx = (W - bw) / 2f;
        float by = 22f;
        if (texBtnBack != null) game.batch.draw(texBtnBack, bx, by, bw, bh);
        gl.setText(fBtn, "INDIETRO");
        fBtn.setColor(0.75f, 0.75f, 0.92f, 1f);
        fBtn.draw(game.batch, "INDIETRO",
            bx + (bw - gl.width) / 2f,
            by + (bh + gl.height) / 2f);
    }

    // ── Pallini pagina ────────────────────────────────────────────────────

    private void drawPageDots() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float cx0 = W/2f - (PAGES-1)*20f;
        for (int i = 0; i < PAGES; i++) {
            if (i == page) {
                sr.setColor(0.35f, 0.82f, 1f, 1f);
                sr.circle(cx0 + i*40f, 158f, 7f, 14);
                sr.setColor(0.35f, 0.82f, 1f, 0.22f);
                sr.circle(cx0 + i*40f, 158f, 11f, 14);
            } else {
                sr.setColor(0.25f, 0.30f, 0.48f, 0.85f);
                sr.circle(cx0 + i*40f, 158f, 5f, 12);
            }
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // ── Input ─────────────────────────────────────────────────────────────

    private void handleInput() {
        boolean left  = Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)|| Gdx.input.isKeyJustPressed(Input.Keys.D);
        boolean back  = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                     || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE);

        if (left  && page > 0)       page--;
        if (right && page < PAGES-1) page++;

        if (Gdx.input.justTouched()) {
            float tx = Gdx.input.getX() / (float)Gdx.graphics.getWidth() * W;
            float ty = (1f - Gdx.input.getY() / (float)Gdx.graphics.getHeight()) * H;
            if (!back) {
                if (tx < 100f && page > 0)       page--;
                if (tx > W-100f && page < PAGES-1) page++;
                if (ty < 90f) back = true;
            }
        }

        if (back) game.setScreen(new MenuScreen(game));
    }

    private void cx(BitmapFont f, String t, float y) {
        gl.setText(f, t);
        f.draw(game.batch, t, (W - gl.width) / 2f, y);
    }

    @Override public void resize(int w, int h) { vp.update(w, h, true); }
    @Override public void show()   { page = 0; }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        fPageTitle.dispose(); fSection.dispose(); fBody.dispose();
        fNav.dispose(); fBtn.dispose();
        sr.dispose();
        if (texBg != null)      texBg.dispose();
        if (texBtnBack != null) texBtnBack.dispose();
    }
}
