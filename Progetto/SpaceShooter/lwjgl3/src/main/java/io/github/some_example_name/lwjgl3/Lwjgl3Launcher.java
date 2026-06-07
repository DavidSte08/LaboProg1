package io.github.some_example_name.lwjgl3;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.some_example_name.Main;

/**
 * Entry point desktop (LWJGL3).
 *
 * <p>Avvia il gioco in <strong>fullscreen nativo</strong> usando la risoluzione
 * corrente del monitor. Se il fullscreen non è disponibile, torna a una
 * finestra 1280×720. Il {@link com.badlogic.gdx.utils.viewport.FitViewport}
 * usato nelle schermate adatta automaticamente il contenuto a qualsiasi
 * risoluzione mantenendo le proporzioni 9:16.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 */
public class Lwjgl3Launcher {

    /**
     * Punto di ingresso principale.
     *
     * @param args argomenti da riga di comando (non usati)
     */
    public static void main(String[] args) {
        // StartupHelper gestisce macOS e alcuni edge-case Windows
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), buildConfig());
    }

    private static Lwjgl3ApplicationConfiguration buildConfig() {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();

        cfg.setTitle("Space Shooter");
        cfg.useVsync(true);

        // ── Fullscreen nativo ────────────────────────────────────────────
        com.badlogic.gdx.Graphics.DisplayMode dm =
            Lwjgl3ApplicationConfiguration.getDisplayMode();

        if (dm != null) {
            cfg.setFullscreenMode(dm);
        } else {
            cfg.setWindowedMode(1280, 720);
        }

        cfg.setForegroundFPS(dm != null ? dm.refreshRate + 1 : 60);

        cfg.setWindowIcon(
            "libgdx128.png", "libgdx64.png",
            "libgdx32.png",  "libgdx16.png"
        );

        return cfg;
    }
}
