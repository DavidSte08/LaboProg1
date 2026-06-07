package io.github.some_example_name.entities;

// NOTA AI: Questo file e' stato sviluppato con il supporto di Claude (Anthropic).
// Il codice e' stato verificato e in parte modificato manualmente da David Stefanovic.
// Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.weapons.Bullet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe astratta base per tutti i nemici.
 *
 * <p>Estende {@link GameObject} e definisce il contratto comune:
 * salute, punti, lista di proiettili e i metodi astratti {@link #move(float)}
 * e {@link #shoot()} che le sottoclassi implementano con comportamenti diversi.</p>
 *
 * <p><strong>Polimorfismo:</strong> {@code GameScreen} chiama {@code enemy.update()},
 * {@code enemy.move()} e {@code enemy.shoot()} su ogni elemento della lista
 * {@code List<Enemy>} senza sapere se è un {@link EnemyBasic} o un {@link EnemyBoss}.</p>
 *
 * @author David Stefanovic
 * @version 1.0
 * @see EnemyBasic
 * @see EnemyBoss
 */
public abstract class Enemy extends GameObject {

    /** Punti vita del nemico. */
    protected int health;

    /** Punti assegnati al giocatore quando questo nemico viene distrutto. */
    protected int points;

    /** Timer per il cooldown dello sparo. */
    protected float shootTimer;

    /** Intervallo tra uno sparo e l'altro (secondi). Modificabile nelle sottoclassi. */
    protected float shootInterval;

    /** Proiettili sparati da questo nemico. */
    protected final List<Bullet> bullets;

    /**
     * Moltiplicatore velocità applicato dal power-up SLOW del giocatore.
     * 1.0 = normale, 0.3 = rallentato. Impostato da GameScreen ogni frame.
     */
    protected float slowMult = 1.0f;

    /**
     * Costruisce un nemico con le proprietà di base specificate.
     *
     * @param x       coordinata X iniziale
     * @param y       coordinata Y iniziale
     * @param width   larghezza in pixel
     * @param height  altezza in pixel
     * @param health  punti vita iniziali
     * @param points  punti assegnati alla distruzione
     */
    public Enemy(float x, float y, float width, float height, int health, int points) {
        super(x, y, width, height);
        this.health        = health;
        this.points        = points;
        this.shootTimer    = 0f;
        this.shootInterval = 2.0f;
        this.bullets       = new ArrayList<>();
    }

    /**
     * Definisce il pattern di movimento specifico del nemico.
     *
     * @param delta secondi dall'ultimo frame
     */
    public abstract void move(float delta);

    /**
     * Definisce il pattern di sparo specifico del nemico.
     */
    public abstract void shoot();

    /**
     * Aggiorna il nemico: chiama {@link #move(float)}, gestisce il timer di sparo
     * e aggiorna i proiettili.
     *
     * @param delta secondi dall'ultimo frame
     */
    @Override
    public void update(float delta) {
        if (!alive) return;
        move(delta);
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shoot();
            shootTimer = 0f;
        }
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(delta);
            if (!b.isAlive()) it.remove();
        }
    }

    /**
     * Disegna il nemico e i suoi proiettili.
     *
     * @param batch batch corrente
     */
    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        for (Bullet b : bullets) b.draw(batch);
    }

    /**
     * Applica danno al nemico. Se la salute scende a zero lo elimina.
     *
     * @param damage quantità di danno
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) alive = false;
    }

    /**
     * Imposta il moltiplicatore di velocità (usato da GameScreen per il power-up SLOW).
     * @param m valore tra 0 e 1 (1 = normale, 0.3 = rallentato)
     */
    public void setSlowMult(float m) { this.slowMult = m; }

    /** @return salute rimanente */
    public int getHealth()  { return health; }

    /** @return punti assegnati alla distruzione */
    public int getPoints()  { return points; }

    /** @return proiettili attivi sparati da questo nemico */
    public List<Bullet> getBullets() { return bullets; }
}
