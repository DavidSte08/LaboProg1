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
 * Schermata di pausa sovrapposta al gioco.
 *
 * <p>Viene mostrata quando il giocatore preme ESC o P durante la partita.
 * Presenta tre opzioni navigabili:</p>
 * <ol>
 *   <li><strong>RIPRENDI</strong> — torna alla partita in corso</li>
 *   <li><strong>MENU</strong> — abbandona la partita e torna al menu</li>
 *   <li><strong>ESCI</strong> — chiude l'applicazione</li>
 * </ol>
 *
 * <p>La schermata precedente (GameScreen) viene passata nel costruttore
 * e ripristinata quando il giocatore sceglie "Riprendi".</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class PauseScreen implements Screen {

    private static final float W = GameScreen.W;
    private static final float H = GameScreen.H;

    private final Main   game;
    private final Screen gameScreen; // Riferimento alla partita da riprendere

    private final OrthographicCamera camera;
    private final Viewport            viewport;

    private final BitmapFont  fontTitle;
    private final BitmapFont  fontMenu;
    private final BitmapFont  fontSub;
    private final GlyphLayout layout;
    private final ShapeRenderer shapes;

    private Texture texOverlay;
    private Texture texPanel;
    private Texture texBtnNormal;
    private Texture texBtnSelected;

    private static final String[] LABELS = { "RIPRENDI", "MENU PRINCIPALE", "ESCI" };
    private int   selected     = 0;
    private float time         = 0f;
    private float inputCooldown = 0f;

    private static final float BTN_W  = 420f;
    private static final float BTN_H  = 68f;
    private static final float BTN_GAP = 20f;
    private static final float PANEL_W = 520f;
    private static final float PANEL_H = 480f;

    /**
     * Crea la schermata di pausa.
     *
     * @param game       istanza principale del gioco
     * @param gameScreen la schermata di gioco da cui si è entrati in pausa
     */
    public PauseScreen(Main game, Screen gameScreen) {
        this.game       = game;
        this.gameScreen = gameScreen;

        camera   = new OrthographicCamera();
        viewport = new FitViewport(W, H, camera);
        camera.position.set(W / 2f, H / 2f, 0f);
        camera.update();

        fontTitle = new BitmapFont(); fontTitle.getData().setScale(4.5f);
        fontMenu  = new BitmapFont(); fontMenu.getData().setScale(2.6f);
        fontSub   = new BitmapFont(); fontSub.getData().setScale(1.6f);
        layout    = new GlyphLayout();
        shapes    = new ShapeRenderer();

        texOverlay    = loadTex("images/pause_overlay.png");
        texPanel      = loadTex("images/panel_large.png");
        texBtnNormal  = loadTex("images/btn_normal.png");
        texBtnSelected = loadTex("images/btn_selected.png");
    }

    private Texture loadTex(String path) {
        try { return new Texture(Gdx.files.internal(path)); }
        catch (Exception e) { Gdx.app.error("PauseScreen", path + ": " + e.getMessage()); return null; }
    }

    @Override
    public void render(float delta) {
        time += delta;
        if (inputCooldown > 0f) inputCooldown -= delta;
        handleInput();

        Gdx.gl.glClearColor(0f, 0f, 0.04f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Overlay scuro sull'intera schermata
        if (texOverlay != null)
            game.batch.draw(texOverlay, 0f, 0f, W, H);

        // Pannello centrale
        float px = (W - PANEL_W) / 2f;
        float py = (H - PANEL_H) / 2f;
        if (texPanel != null)
            game.batch.draw(texPanel, px, py, PANEL_W, PANEL_H);

        // Titolo PAUSA
        float pulse = 1f + 0.02f * MathUtils.sin(time * 3f);
        fontTitle.getData().setScale(4.5f * pulse);
        fontTitle.setColor(0.5f, 0.9f, 1f, 1f);
        drawCentered(fontTitle, "PAUSA", py + PANEL_H -15);
        fontTitle.getData().setScale(4.5f);

        // Linea separatrice
        drawSeparator(py + PANEL_H - 100f);

        // Pulsanti
        float btnX  = (W - BTN_W) / 2f;
        float startY = py + PANEL_H - 160f;
        for (int i = 0; i < LABELS.length; i++) {
            float by  = startY - i * (BTN_H + BTN_GAP);
            boolean sel = (i == selected);

            Texture t = sel ? texBtnSelected : texBtnNormal;
            if (t != null) game.batch.draw(t, btnX, by, BTN_W, BTN_H);

            if (sel) {
                fontMenu.setColor(0.4f, 0.95f, 1f, 1f);
                float ap = MathUtils.sin(time * 6f) * 5f;
                fontMenu.draw(game.batch, "›", btnX - 38f + ap, by + BTN_H * 0.68f);
                fontMenu.draw(game.batch, "‹", btnX + BTN_W + 12f - ap, by + BTN_H * 0.68f);
            } else {
                fontMenu.setColor(0.7f, 0.7f, 0.88f, 1f);
            }

            layout.setText(fontMenu, LABELS[i]);
            fontMenu.draw(game.batch, LABELS[i],
                btnX + (BTN_W - layout.width) / 2f,
                by + (BTN_H + layout.height) / 2f);
        }

        // Hint tasto pausa
        fontSub.setColor(0.45f, 0.5f, 0.7f, 1f);
        drawCentered(fontSub, "ESC / P  per riprendere", py + 30f);

        game.batch.end();
    }

    /** Disegna una linea separatrice sottile tramite ShapeRenderer. */
    private void drawSeparator(float y) {
        game.batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        float alpha = 0.5f + 0.2f * MathUtils.sin(time * 2f);
        shapes.setColor(0.3f, 0.6f, 1f, alpha);
        shapes.rectLine((W - 440f) / 2f, y, (W + 440f) / 2f, y, 1.5f);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        game.batch.begin();
    }

    private void handleInput() {
        boolean up   = Gdx.input.isKeyJustPressed(Input.Keys.UP)   || Gdx.input.isKeyJustPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S);

        if (Controllers.getControllers().size > 0 && inputCooldown <= 0f) {
            Controller ctrl = Controllers.getControllers().first();
            float ay = ctrl.getAxis(1);
            if (ay < -0.5f) { up   = true; inputCooldown = 0.2f; }
            if (ay >  0.5f) { down = true; inputCooldown = 0.2f; }
        }

        if (up)   selected = (selected - 1 + LABELS.length) % LABELS.length;
        if (down) selected = (selected + 1) % LABELS.length;

        // ESC o P → riprendi direttamente
        boolean resumeKey = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                         || Gdx.input.isKeyJustPressed(Input.Keys.P);

        boolean confirm = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                       || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        if (Controllers.getControllers().size > 0)
            confirm |= Controllers.getControllers().first().getButton(0);

        if (Gdx.input.justTouched()) {
            float tx = Gdx.input.getX() / (float) Gdx.graphics.getWidth() * W;
            float ty = (1f - Gdx.input.getY() / (float) Gdx.graphics.getHeight()) * H;
            float btnX  = (W - BTN_W) / 2f;
            float startY = (H - PANEL_H) / 2f + PANEL_H - 160f;
            for (int i = 0; i < LABELS.length; i++) {
                float by = startY - i * (BTN_H + BTN_GAP);
                if (tx >= btnX && tx <= btnX + BTN_W && ty >= by && ty <= by + BTN_H) {
                    selected = i; confirm = true; break;
                }
            }
        }

        if (resumeKey) resumeGame();
        else if (confirm) activate(selected);
    }

    private void activate(int idx) {
        switch (idx) {
            case 0: resumeGame(); break;
            case 1:
                gameScreen.dispose();
                game.setScreen(new MenuScreen(game));
                break;
            case 2: Gdx.app.exit(); break;
        }
    }

    /** Riprende la partita ripristinando la schermata di gioco. */
    private void resumeGame() {
        game.setScreen(gameScreen);
        game.audioManager.resumeMusic();
    }

    private void drawCentered(BitmapFont f, String text, float y) {
        layout.setText(f, text);
        f.draw(game.batch, text, (W - layout.width) / 2f, y);
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void show()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        fontTitle.dispose(); fontMenu.dispose(); fontSub.dispose();
        shapes.dispose();
        if (texOverlay    != null) texOverlay.dispose();
        if (texPanel      != null) texPanel.dispose();
        if (texBtnNormal  != null) texBtnNormal.dispose();
        if (texBtnSelected != null) texBtnSelected.dispose();
    }
}
