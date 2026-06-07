# Class Diagram — Space Shooter
**Autore:** David Stefanovic — I2BD — SAMT 2025/2026

---

## Gerarchia di ereditarietà

```
GameObject  (abstract)
├── Player
├── Enemy   (abstract)
│   ├── EnemyBasic
│   ├── EnemyZigzag
│   ├── EnemySweeper
│   ├── EnemyDiver
│   └── EnemyBoss
├── PowerUp
└── Bullet   [package: weapons]
```

---

## Descrizione delle classi

### `GameObject` — *entities*, abstract
Classe base astratta per tutte le entità di gioco. Mantiene posizione (x, y),
dimensioni (width, height), texture e flag `alive`. Fornisce `getBounds()` per
le collisioni e `draw()` per il rendering. Dichiara `update(float)` astratto.

### `Player` — *extends GameObject*
Navicella del giocatore. Legge input da tastiera (WASD/frecce/spazio) e da
gamepad tramite Controllers API. Gestisce sparo con cooldown (0.5s), vite (max 5),
punteggio, invulnerabilità post-danno (1.8s) e inventory a 2 slot (Q=SPEED, E=SLOW).

### `Enemy` — *extends GameObject*, abstract
Classe base astratta per tutti i nemici. Definisce il contratto: `move(float)` e
`shoot()` sono astratti. Gestisce lista proiettili, timer di sparo, health e points.
Espone `setSlowMult(float)` per il power-up SLOW del giocatore.

### `EnemyBasic` — *extends Enemy*
Scende con movimento sinusoidale (Math.sin). Tinta colore personalizzabile.
1 vita, 100 punti. Sparo singolo verso il basso.

### `EnemyZigzag` — *extends Enemy*
Rimbalza lateralmente sui bordi con cambio di direzione secco.
Spara due proiettili in diagonale. 1 vita, 150 punti. Colore arancione.

### `EnemySweeper` — *extends Enemy*
Scende lento in linea retta. Spara un ventaglio di 5 proiettili su 70°.
2 vite, 200 punti. Colore verde acqua.

### `EnemyDiver` — *extends Enemy*
Bifasico: HOVER (oscilla lateralmente fino a 2/3 schermo) poi DIVE (picchiata
mirata sulla X del giocatore a 360 px/s). Spara un proiettile appena prima del
tuffo. 1 vita, 250 punti. Colore viola → magenta in picchiata.

### `EnemyBoss` — *extends Enemy*
3 fasi in base alla percentuale di salute:
- Fase 1 (>60%): rimbalzo lento, sparo singolo
- Fase 2 (30-60%): più veloce, ventaglio 3 proiettili
- Fase 3 (<30%): sinusoide + cerchio 8 proiettili
30 vite, 5000 punti. Appare ogni 5 ondate.

### `PowerUp` — *extends GameObject*
Tre tipi (enum Type): LIFE (vita immediata), SPEED (slot Q — doppio sparo 7s),
SLOW (slot E — nemici al 28% velocità per 6s). Cade verso il basso dopo la
morte di un nemico (probabilità 18%). Usa `apply(Player)` per applicare l'effetto.

### `Bullet` — *extends GameObject*, package weapons
Proiettili con texture statiche condivise (evita memory leak). Supporta
traiettoria vettoriale (vx, vy) per proiettili diagonali del boss.

---

## Classi Screen

### `GameScreen` — *implements Screen*
Loop principale: aggiorna Player, List\<Enemy\>, List\<PowerUp\>, orphanBullets.
Gestisce 5 tipi di collisione con Rectangle.overlaps(). HUD con vite, punteggio,
combo, barra boss, inventory Q/E. Banner ondata, flash danno, pausa con ESC/P.

### `MenuScreen` — *implements Screen*
Menu con 4 pulsanti colorati, stelle animate parallasse, pannello Record.
Navigazione tastiera + gamepad + touch. Musica menu.

### `PauseScreen` — *implements Screen*
Sovrapposta a GameScreen (non la ricrea). Riceve il riferimento a GameScreen
nel costruttore e lo ripristina con game.setScreen(gameScreen). Pausa musica
con pauseMusic()/resumeMusic() senza riavviare dall'inizio.

### `HowToPlayScreen` — *implements Screen*
3 pagine navigabili (Controlli, Nemici, Power-Up). Frecce laterali + pallini
indicatori. Pulsante INDIETRO in basso.

---

## Classi Utility

### `AudioManager` — *utils*
3 Music (menu/gioco/boss) + 7 Sound. Switch automatico alla musica boss alle
wave 5 e 10. pauseMusic()/resumeMusic() mantengono la posizione nella traccia.
Tutti i caricamenti in try-catch: file mancante → log, non crash.

### `ScoreManager` — *utils*
Legge e scrive `highscore.txt` tramite `Gdx.files.local()` (portabile su tutti
i OS). submitScore() salva solo se il nuovo punteggio supera il record.
NumberFormatException catturata con reset a 0 se il file è corrotto.

### `WaveManager` — *utils*
6 formazioni che ruotano ciclicamente: LINEA, V, PINZA, SERPENTE, GRIGLIA, MISTA.
Difficoltà crescente da wave 1 a 9. Ogni 5 wave genera EnemyBoss.
Riceve playerX da GameScreen ogni frame per i Diver.

---

## Relazioni tra classi

| Classe | Relazione | Con |
|--------|-----------|-----|
| Main | crea e possiede | AudioManager, ScoreManager, SpriteBatch |
| GameScreen | aggrega | Player, List\<Enemy\>, List\<PowerUp\>, List\<Bullet\> (orphans) |
| GameScreen | usa | WaveManager, AudioManager, ScoreManager |
| WaveManager | crea | Enemy (tutti i tipi) |
| Player | crea | Bullet |
| Enemy | crea | Bullet |
| PowerUp | chiama metodi su | Player |
| PauseScreen | riferimento a | GameScreen |
| Tutte le Screen | accedono via Main a | AudioManager, ScoreManager, SpriteBatch |
