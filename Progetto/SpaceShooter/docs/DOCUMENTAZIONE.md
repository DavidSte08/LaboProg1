# Documentazione Progetto — Space Shooter (LibGDX)

**Studente:** [Nome Cognome]  
**Classe:** [Classe]  
**Anno scolastico:** 2025/2026  
**Framework:** LibGDX 1.14.0 — Java 11  
**Consegna:** GitHub — 07.06.2026

---

## Indice

1. [Descrizione del gioco](#1-descrizione-del-gioco)
2. [Struttura del progetto](#2-struttura-del-progetto)
3. [Architettura e design delle classi](#3-architettura-e-design-delle-classi)
4. [I quattro pilastri OOP](#4-i-quattro-pilastri-oop)
5. [Classi entità](#5-classi-entità)
6. [Classi schermate](#6-classi-schermate)
7. [Classi utility](#7-classi-utility)
8. [Sistema di collisioni](#8-sistema-di-collisioni)
9. [Sistema audio](#9-sistema-audio)
10. [Sistema R/W File](#10-sistema-rw-file)
11. [Gestione delle eccezioni](#11-gestione-delle-eccezioni)
12. [Sistema responsive (FitViewport)](#12-sistema-responsive-fitviewport)
13. [Controller (gamepad)](#13-controller-gamepad)
14. [Unit test](#14-unit-test)
15. [JavaDoc](#15-javadoc)
16. [Class Diagram](#16-class-diagram)
17. [Fonti e dichiarazione AI](#17-fonti-e-dichiarazione-ai)

---

## 1. Descrizione del gioco

Space Shooter è un gioco sparatutto a scorrimento verticale (genere "shoot 'em up").
Il giocatore pilota una navicella spaziale e affronta 10 ondate di nemici alieni.
Ogni 5 ondate appare un boss con tre fasi di attacco crescenti.
L'obiettivo è sopravvivere a tutte le ondate e ottenere il punteggio più alto possibile.

**Meccaniche principali:**
- Movimento in tutte le direzioni (tastiera WASD/frecce o gamepad)
- Sparo con cooldown (SPAZIO o tasto A gamepad)
- Power-up raccoglibili: vita extra, doppio sparo (attivabile con Q), bonus punti
- Sistema combo: eliminare nemici in rapida sequenza moltiplica i punti
- Pausa con ESC o P
- Punteggio salvato su file e persistente tra le sessioni

---

## 2. Struttura del progetto

```
SpaceShooterFinal/
├── core/src/main/java/io/github/some_example_name/
│   ├── Main.java                        ← Entry point LibGDX (estende Game)
│   ├── entities/
│   │   ├── GameObject.java              ← Classe base astratta (ereditarietà)
│   │   ├── Player.java                  ← Giocatore
│   │   ├── Enemy.java                   ← Nemico astratto
│   │   ├── EnemyBasic.java              ← Nemico sinusoidale
│   │   ├── EnemyZigzag.java             ← Nemico a rimbalzo
│   │   ├── EnemySweeper.java            ← Nemico a ventaglio
│   │   ├── EnemyDiver.java              ← Nemico a picchiata
│   │   ├── EnemyBoss.java               ← Boss a 3 fasi
│   │   └── PowerUp.java                 ← Power-up raccoglibili
│   ├── weapons/
│   │   └── Bullet.java                  ← Proiettili (con texture condivise)
│   ├── screens/
│   │   ├── MenuScreen.java              ← Menu principale animato
│   │   ├── GameScreen.java              ← Loop di gioco principale
│   │   ├── PauseScreen.java             ← Schermata di pausa
│   │   └── HowToPlayScreen.java         ← Guida 3 pagine con animazione slide
│   └── utils/
│       ├── AudioManager.java            ← 3 musiche + 7 effetti sonori
│       ├── ScoreManager.java            ← R/W highscore su file
│       └── WaveManager.java             ← 6 formazioni + 5 tipi di nemico
├── core/src/test/java/.../
│   └── GameLogicTest.java               ← 15 unit test JUnit 4
├── assets/
│   ├── images/                          ← 26 PNG generati con Python Pillow
│   └── sounds/                          ← 10 file OGG (MP3 convertiti con FFmpeg)
└── lwjgl3/                              ← Modulo desktop (fullscreen nativo)
```

**Totale:** 18 classi Java, ~4.000 righe di codice, 26 asset grafici, 10 file audio.

---

## 3. Architettura e design delle classi

Il progetto usa il pattern architetturale di LibGDX basato su **Game + Screen**:

- `Main` estende `Game` ed è il punto di ingresso. Inizializza le risorse condivise
  (`SpriteBatch`, `AudioManager`, `ScoreManager`) e avvia `MenuScreen`.
- Ogni schermata (`MenuScreen`, `GameScreen`, `PauseScreen`, `HowToPlayScreen`)
  implementa l'interfaccia `Screen` di LibGDX con i metodi
  `render()`, `resize()`, `show()`, `hide()`, `pause()`, `resume()`, `dispose()`.
- Le entità di gioco formano una gerarchia di ereditarietà con `GameObject` come radice.

**Flusso principale:**
```
Main.create()
  → MenuScreen (musica menu)
      → GameScreen (nuova partita)
          → PauseScreen (ESC/P) → torna a GameScreen
          → MenuScreen (abbandona o fine partita)
      → HowToPlayScreen (istruzioni)
          → MenuScreen (torna)
```

---

## 4. I quattro pilastri OOP

### Ereditarietà

Gerarchia completa:
```
GameObject  (astratta)
├── Player
├── Enemy   (astratta)
│   ├── EnemyBasic
│   ├── EnemyZigzag
│   ├── EnemySweeper
│   ├── EnemyDiver
│   └── EnemyBoss
├── PowerUp
└── Bullet   [package weapons]
```

`GameObject` definisce i campi comuni (`x`, `y`, `width`, `height`, `texture`, `alive`)
e i metodi `update()` (astratto), `draw()`, `getBounds()`, `dispose()`.
Tutte le sottoclassi ereditano questi comportamenti senza ridefinirli.

`Enemy` aggiunge un ulteriore livello astratto con `move()` e `shoot()` astratti,
che ogni tipo di nemico implementa con il proprio comportamento.

### Polimorfismo

In `GameScreen`, la lista nemici è `List<Enemy>`. Il codice chiama:
```java
for (Enemy en : enemies) {
    en.update(delta);   // diverso per ogni tipo
    en.draw(batch);     // stesso metodo, comportamento diverso (colore tinta)
}
```
Senza sapere se l'oggetto è un `EnemyBasic`, uno `EnemyZigzag`, un `EnemyBoss`…
Ogni tipo esegue il proprio `move()` e `shoot()`.

Stesso principio per `PowerUp.apply(player)`: tutti i power-up usano lo stesso
metodo, ma l'effetto cambia (LIFE → addLife, SPEED → collectSpeed, SCORE → addScore).

### Incapsulamento

Tutti i campi di `GameObject` sono `protected` — accessibili alle sottoclassi
ma non dall'esterno. Quelli di `Player`, `ScoreManager`, `AudioManager` sono
`private` con getter/setter pubblici documentati.

Esempio:
```java
// Campo privato — nessuno può modificarlo direttamente
private int lives;

// Solo tramite metodi controllati
public boolean takeDamage() { ... }  // controlla invulnerabilità
public void addLife()       { if (lives < MAX_LIVES) lives++; }
public int  getLives()      { return lives; }
```

### Gestione eccezioni

Tutti i caricamenti di asset (texture, audio) sono in `try-catch`:
```java
try {
    texture = new Texture(Gdx.files.internal("images/player.png"));
} catch (Exception e) {
    Gdx.app.error("Player", "player.png non trovato: " + e.getMessage());
}
```
Se un file manca il gioco non crasha — registra un errore e continua.

Metodi con precondizioni lanciano eccezioni appropriate:
```java
public boolean takeDamage() {
    if (!alive) throw new IllegalStateException("Il giocatore è già morto");
    if (invTimer > 0f) return true; // invulnerabile: ignora
    ...
}

public void addScore(int points) {
    if (points < 0) throw new IllegalArgumentException("Punti negativi: " + points);
    score += points;
}
```

---

## 5. Classi entità

### `GameObject` — *astratta*

Classe base di tutto. Campi: `x`, `y`, `width`, `height`, `texture`, `alive`.
Metodo astratto `update(float delta)` — ogni sottoclasse lo implementa.
`getBounds()` ritorna un `Rectangle` usato per rilevare le collisioni.
`draw(SpriteBatch)` disegna la texture solo se `alive == true`.

### `Player`

Legge input da tastiera (WASD/frecce + SPAZIO) e da gamepad
(`Controllers.getControllers().first()`).

Meccaniche chiave:
- **Sparo** con cooldown di 0.50 secondi. Se il doppio sparo è attivo,
  genera due proiettili paralleli invece di uno.
- **Invulnerabilità post-danno** di 1.8 secondi con effetto lampeggio visivo.
- **Inventory power-up**: raccogliere uno SPEED lo mette in attesa;
  il giocatore preme Q (o tasto B gamepad) per attivarlo quando vuole.
  Questo dà scelta tattica invece di attivazione automatica.
- Vite massime: 5. Punteggio: sale con `addScore(int)`.

### `Enemy` — *astratta*

Aggiunge a `GameObject`: `health`, `points`, `shootTimer`, `shootInterval`,
lista `bullets`. Implementa `update()` che chiama `move()` + timer sparo + aggiorna proiettili.
`move()` e `shoot()` sono astratti.

### `EnemyBasic`

Movimento sinusoidale: scende verticalmente con oscillazione orizzontale calcolata
con `Math.sin(timeAccum * FREQUENCY) * AMPLITUDE`. Clamp ai bordi (0 ↔ 720px).
Sparo: proiettile singolo verso il basso. 1 vita, 100 punti. Tinta colore
personalizzabile alla creazione (usata dal WaveManager per variare l'aspetto
dei nemici nelle diverse formazioni senza duplicare texture).

### `EnemyZigzag`

Scende verso il basso rimbalzando lateralmente sui bordi con cambio secco
di direzione (non sinusoide). Quando spara lancia due proiettili diagonali.
Colore arancione. 1 vita, 150 punti.

### `EnemySweeper`

Scende lentamente in linea retta (più lento del base). Quando spara genera
un ventaglio di 5 proiettili distribuiti su 70° verso il basso.
2 vite (il più resistente tra i normali). Colore verde acqua. 200 punti.

### `EnemyDiver`

Due fasi di comportamento distinte:
1. **HOVER**: scende piano fino a circa 2/3 dello schermo, poi oscilla
   lateralmente per 1.4 secondi.
2. **DIVE**: si lancia in picchiata a 360 px/s esattamente verso la X del
   giocatore (aggiornata ogni frame da `WaveManager.setPlayerX()`),
   sparando un proiettile appena prima del tuffo.
Diventa magenta in picchiata come avvertimento visivo. 1 vita, 250 punti.

### `EnemyBoss`

Tre fasi in base alla percentuale di salute:
- **Fase 1** (>60% vita): rimbalzo laterale lento, sparo singolo verso il basso.
- **Fase 2** (30-60% vita): più veloce, ventaglio di 3 proiettili.
- **Fase 3** (<30% vita): moto sinusoidale verticale + rimbalzo, cerchio
  di 8 proiettili in tutte le direzioni.
30 vite, 5000 punti. Ogni 5 ondate.

### `PowerUp`

Tre tipi (enum `Type`): `LIFE`, `SPEED`, `SCORE`.
Cade verso il basso dopo la morte di un nemico (probabilità 18%).
`apply(Player)` ha effetti diversi per tipo — polimorfismo per tipo.
SPEED chiama `player.collectSpeed()` invece di attivare subito.

### `Bullet`

Usa **texture statiche condivise** — una sola `Texture` per tipo (giocatore/nemico)
invece di crearne una per ogni proiettile. Questo evita memory leak (centinaia
di proiettili per partita esaurirebbero la VRAM).
Velocità vettoriale (vx, vy) permette traiettorie diagonali (boss fase 3).
I bounds per la disattivazione usano le coordinate virtuali (720×1280),
non i pixel fisici dello schermo.

---

## 6. Classi schermate

### `MenuScreen`

Schermata principale con 4 voci: GIOCA, COME GIOCARE, RECORD, ESCI.
Animazioni: 150 stelle parallasse su 3 layer a velocità diverse,
titolo pulsante, rombi decorativi, pulsanti con sottotitolo descrittivo
sulla voce selezionata. Navigazione con frecce/WASD, conferma con INVIO/SPAZIO,
supporto touch (tap sui pulsanti). Musica `musica_menu.ogg`.

### `GameScreen`

Il cuore del gioco. Gestisce ogni frame:
1. Controlla pausa (ESC/P)
2. Aggiorna player, nemici, power-up, proiettili orfani
3. Rileva tutte le collisioni (4 tipi)
4. Disegna sfondo con scrolling lineare (18 px/s)
5. Disegna tutte le entità
6. Disegna HUD
7. Disegna overlay di pausa/game over/vittoria

**Proiettili orfani**: quando un nemico muore, i suoi proiettili già sparati
vengono trasferiti in `orphanBullets` (lista separata) invece di sparire.
Continuano a muoversi e collidono con il giocatore normalmente.

**Sistema combo**: eliminare nemici entro 1.8 secondi l'uno dall'altro accumula
una serie. Da 3 uccisioni in poi il punteggio viene moltiplicato.
Un popup dorato animato mostra il bonus guadagnato.

**Flash danno**: bordo rosso sullo schermo (ShapeRenderer) quando il
giocatore viene colpito.

**Banner ondata**: all'inizio di ogni wave appare un banner centrato
con animazione slide-in + fade-out.

### `PauseScreen`

Sovrapposta a `GameScreen` (non la ricrea). Riceve `gameScreen` nel costruttore
e lo ripristina con `game.setScreen(gameScreen)` quando si riprende.
La musica viene messa in pausa (non riavviata) e riprende dal punto esatto.
Tre voci: RIPRENDI, MENU PRINCIPALE, ESCI.

### `HowToPlayScreen`

Tre pagine navigabili con frecce sinistra/destra o tap laterale.
Animazione slide laterale al cambio pagina.
Icone reali degli oggetti (navicella, nemici, power-up) a sinistra di ogni riga.
Contenuto: controlli, tutti e 5 i tipi di nemico con punti, sistema power-up
con spiegazione del tasto Q.

---

## 7. Classi utility

### `AudioManager`

Gestisce 3 tracce musicali separate e 7 effetti sonori.

Musiche (loop):
- `musica_menu.ogg` — menu principale
- `musica_gioco.ogg` — ondate normali
- `musica_boss.ogg` — ondate con boss

Il cambio musica avviene automaticamente:
- Alla wave 5 e 10 → `switchToBossMusic()`
- Alla wave successiva al boss → `switchToGameMusic()`
- Pausa → `pauseMusic()` (mantiene la posizione)
- Riprende → `resumeMusic()` (riparte dal punto)

Effetti sonori: esplosione nemico, danno al giocatore, danno al boss,
sparo nemico, game over, vittoria, annuncio ondata / raccolta power-up.

Tutti i caricamenti in `try-catch` — file mancante → log, non crash.

### `ScoreManager`

Salva e carica l'highscore usando `Gdx.files.local("highscore.txt")`,
che scrive nella cartella locale dell'applicazione.
File di testo semplice con un numero intero.
Gestisce file corrotti (`NumberFormatException`) con fallback a 0.
`submitScore(int)` aggiorna e salva solo se il nuovo punteggio supera il record.

### `WaveManager`

Genera le ondate con 6 formazioni che ruotano ciclicamente:

| Ondata % 6 | Formazione | Descrizione |
|---|---|---|
| 0 | LINEA | Fila orizzontale uniforme |
| 1 | V | Freccia con vertice al centro |
| 2 | PINZA | Due gruppi dai lati che si stringono |
| 3 | SERPENTE | Diagonale alternata sinistra/destra |
| 4 | GRIGLIA | Matrice 2 righe × N colonne |
| 5 | MISTA | Mix casuale pesato per difficoltà |

La difficoltà cresce linearmente dalla wave 1 alla 9.
I tipi di nemico introdotti progressivamente:
- Wave 1-3: solo `EnemyBasic`
- Wave 4+: compare `EnemyZigzag`
- Wave 6+: compare `EnemySweeper`
- Wave 7+: compare `EnemyDiver`

Ogni 5 ondate: un solo `EnemyBoss`.
Mantiene aggiornata la posizione X del giocatore (per i Diver).

---

## 8. Sistema di collisioni

Le collisioni sono rilevate in `GameScreen.update()` con `Rectangle.overlaps()`.

Quattro tipi gestiti ogni frame con iteratori espliciti (no `ConcurrentModificationException`):

```
1. Proiettili giocatore  →  nemico
   → nemico.takeDamage(1)
   → se muore: punteggio, esplosione, possibile power-up, trasferimento proiettili orfani

2. Corpo nemico  →  giocatore (contatto)
   → nemico.setAlive(false), proiettili trasferiti agli orfani
   → player.takeDamage()

3. Proiettili nemici  →  giocatore
   → proiettile.setAlive(false)
   → player.takeDamage()

4. Proiettili orfani  →  giocatore
   → (proiettili di nemici già morti che continuano a volare)
   → player.takeDamage()

5. Power-up  →  giocatore
   → powerUp.apply(player)
```

Il giocatore ha 1.8 secondi di invulnerabilità dopo ogni danno subito.
Durante questo periodo i danni vengono ignorati anche se ci sono più collisioni.

---

## 9. Sistema audio

I 10 file MP3 forniti sono stati convertiti in formato OGG Vorbis
(richiesto da LibGDX) tramite FFmpeg con il comando:
```
ffmpeg -i file.mp3 -c:a libvorbis -q:a 4 file.ogg
```

Mappa completa:

| File originale | File OGG | Quando suona |
|---|---|---|
| `musica_game.mp3` | `musica_menu.ogg` | Loop nel menu |
| `musica.mp3` | `musica_gioco.ogg` | Loop nelle wave normali |
| `musica_boss.mp3` | `musica_boss.ogg` | Loop nelle wave 5 e 10 |
| `wave.mp3` | `wave.ogg` | Inizio ogni ondata + raccolta power-up |
| `esplosione.mp3` | `esplosione.ogg` | Nemico eliminato |
| `boss_damage.mp3` | `boss_damage.ogg` | Colpo al boss (non letale) |
| `damage.mp3` | `damage.ogg` | Danno al giocatore |
| `death.mp3` | `death.ogg` | Game over |
| `fine.mp3` | `fine.ogg` | Vittoria |
| `enemy_bullet.mp3` | `enemy_bullet.ogg` | Disponibile (volume ridotto) |

---

## 10. Sistema R/W File

`ScoreManager` usa l'API di LibGDX per scrivere file in modo portabile
(funziona su Windows, Mac, Linux senza cambiare il percorso):

```java
// SCRITTURA
FileHandle fh = Gdx.files.local("highscore.txt");
fh.writeString(String.valueOf(highScore), false); // false = sovrascrive

// LETTURA
FileHandle fh = Gdx.files.local("highscore.txt");
if (fh.exists()) {
    highScore = Integer.parseInt(fh.readString().trim());
}
```

Il file viene creato nella cartella locale del gioco (su Windows: nella stessa
cartella dell'eseguibile o in AppData a seconda della configurazione).

---

## 11. Gestione delle eccezioni

Tre categorie di gestione:

**1. Asset mancanti** — `try-catch` silenzioso, il gioco continua:
```java
try {
    texture = new Texture(Gdx.files.internal("images/player.png"));
} catch (Exception e) {
    Gdx.app.error("Player", "player.png: " + e.getMessage());
    // texture rimane null → draw() non disegna nulla
}
```

**2. Precondizioni sui metodi** — lanciano eccezioni per segnalare uso scorretto:
```java
// IllegalStateException: stato non valido
if (!alive) throw new IllegalStateException("Il giocatore è già morto");

// IllegalArgumentException: parametro non valido
if (points < 0) throw new IllegalArgumentException("Punti negativi: " + points);
```

**3. File corrotto** — `NumberFormatException` gestita con reset a 0:
```java
try {
    highScore = Integer.parseInt(fh.readString().trim());
} catch (NumberFormatException e) {
    Gdx.app.error("ScoreManager", "File corrotto — reset a 0");
    highScore = 0;
}
```

---

## 12. Sistema responsive (FitViewport)

Tutte le schermate usano `FitViewport(720, 1280)` — dimensioni virtuali fisse.
LibGDX scala automaticamente il contenuto alla risoluzione reale del monitor,
applicando letterbox (bande nere) se le proporzioni non corrispondono.

Questo significa che il gioco funziona identicamente su:
- Schermo 1920×1080 (16:9) → letterbox laterale
- Schermo 2560×1440 (16:9) → letterbox laterale
- Schermo 1280×800 (16:10) → letterbox diversa
- Qualsiasi altra risoluzione

Al ridimensionamento della finestra viene chiamato `resize(int w, int h)`
che aggiorna il viewport: `viewport.update(w, h, true)`.

Il launcher avvia il gioco in **fullscreen nativo** usando la risoluzione
corrente del monitor: `cfg.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())`.

---

## 13. Controller (gamepad)

Gestito tramite la libreria `gdx-controllers` (aggiunta come dipendenza Gradle).

In `Player.update()`:
```java
if (Controllers.getControllers().size > 0) {
    Controller ctrl = Controllers.getControllers().first();
    float ax = ctrl.getAxis(0);  // stick sinistro X
    float ay = ctrl.getAxis(1);  // stick sinistro Y
    if (Math.abs(ax) > 0.15f) x += ax * speed * delta;  // deadzone 15%
    if (Math.abs(ay) > 0.15f) y -= ay * speed * delta;
    if (ctrl.getButton(0)) shoot();        // Tasto A
    if (ctrl.getButton(1)) activatePendingSpeed(); // Tasto B
}
```

Anche i menu (`MenuScreen`, `PauseScreen`) supportano la navigazione
con lo stick e la conferma con il tasto A.

---

## 14. Unit test

File: `GameLogicTest.java` — 15 test JUnit 4.

I test usano classi mock interne che replicano la logica senza dipendere
da LibGDX (nessun contesto grafico richiesto):

| Gruppo | Test | Cosa verifica |
|---|---|---|
| ScoreManager | 6 test | highscore iniziale, punteggio maggiore/minore/uguale, reset, più sottomissioni |
| Player | 6 test | score iniziale, addScore, punteggio negativo, takeDamage, morte, addLife |
| WaveManager | 3 test | wave 1 non boss, wave 5 boss, wave 10 boss, contatore |

Esempio di test:
```java
// Danno causa morte alla terza vita
@Test
public void testTakeDamage() {
    PlayerMock p = new PlayerMock();
    assertTrue(p.takeDamage());   // 2 vite rimaste
    assertTrue(p.takeDamage());   // 1 vita rimasta
    assertFalse(p.takeDamage());  // 0 vite → morto
    assertEquals(0, p.getLives());
}

// Danno su giocatore morto lancia eccezione
@Test(expected = IllegalStateException.class)
public void testDamageWhenDead() {
    PlayerMock p = new PlayerMock();
    p.takeDamage(); p.takeDamage(); p.takeDamage(); // muore
    p.takeDamage(); // deve lanciare IllegalStateException
}
```

Per eseguire i test: `./gradlew core:test`

---

## 15. JavaDoc

Tutte le 18 classi e tutti i metodi pubblici sono documentati con JavaDoc completo:
- `@author` e `@version` su ogni classe
- `@param` per ogni parametro dei metodi
- `@return` per ogni metodo non void
- `@throws` per ogni eccezione dichiarata o lanciata
- `@see` per i riferimenti incrociati tra classi correlate

Per generare la documentazione HTML: `./gradlew core:javadoc`
Output in: `core/build/docs/javadoc/index.html`

---

## 16. Class Diagram

```
                    ┌─────────────────┐
                    │  GameObject     │  ← astratta
                    │─────────────────│
                    │ #x, y, w, h     │
                    │ #texture        │
                    │ #alive          │
                    │─────────────────│
                    │ +update()  abs  │
                    │ +draw()         │
                    │ +getBounds()    │
                    └────────┬────────┘
                             │ estende
          ┌──────────────────┼──────────────────────┐
          │                  │                       │
   ┌──────┴──────┐   ┌───────┴──────┐       ┌───────┴──────┐
   │   Player    │   │   Enemy      │ abs    │   PowerUp    │
   │─────────────│   │──────────────│        │──────────────│
   │ speed,lives │   │ health,points│        │ type: enum   │
   │ score       │   │ shootTimer   │        │──────────────│
   │ doubleShotT │   │─────────────-│        │ +update()    │
   │ pendingSpeed│   │ +move() abs  │        │ +apply(p)    │
   │─────────────│   │ +shoot() abs │        └──────────────┘
   │ +update()   │   └──────┬───────┘
   │ +shoot()    │          │ estende
   │ +takeDamage │   ┌──────┴──────────────────────────────┐
   │ +addLife()  │   │        │           │                │
   └─────────────┘  EnemyBasic EnemyZigzag EnemySweeper EnemyDiver EnemyBoss

   ┌────────────────┐    ┌──────────────────┐
   │  Bullet        │    │  WaveManager     │
   │ (static tex)   │    │ 6 formazioni     │
   │ vx, vy         │    │ difficoltà cresc.│
   └────────────────┘    └──────────────────┘

   ┌────────────────┐    ┌──────────────────┐    ┌──────────────────┐
   │  AudioManager  │    │  ScoreManager    │    │  Main (Game)     │
   │ 3 musiche      │    │ R/W file         │    │ batch, audio, sc │
   │ 7 SFX          │    │ highscore        │    └──────────────────┘
   └────────────────┘    └──────────────────┘
```

---

## 17. Fonti e dichiarazione AI

| Risorsa | URL | Licenza |
|---|---|---|
| LibGDX 1.14.0 | https://libgdx.com | Apache 2.0 |
| StartupHelper.java | https://github.com/crykn/libgdx-screenmanager (damios) | Apache 2.0 |
| LibGDX Wiki | https://libgdx.com/wiki/ | — |
| gdx-controllers | https://libgdx.com/wiki/input/controllers | Apache 2.0 |
| FitViewport docs | https://libgdx.com/wiki/graphics/2d/viewports | — |
| JUnit 4 | https://junit.org/junit4/ | EPL 1.0 |
| Python Pillow | https://pillow.readthedocs.io | HPND |
| FFmpeg | https://ffmpeg.org | LGPL |
| File audio | Forniti dal docente / studente | — |
| Asset PNG | Generati programmaticamente con Python Pillow | — |

### Dichiarazione uso AI

La struttura del progetto, le classi, il JavaDoc, la logica dei nemici
(`EnemyBoss`, `EnemyDiver`, `WaveManager`) e i sistemi di gioco (combo, inventory,
proiettili orfani) sono stati sviluppati con il supporto di **Claude (Anthropic)**.
Il codice è stato verificato, compreso e adattato dallo studente.

In conformità con le linee guida del docente, l'uso dell'AI è dichiarato qui
e nei commenti del sorgente. Le penalità per mancata dichiarazione sono state evitate.

> *"Nel caso vengano utilizzate delle porzioni di codice prese da IA è necessario
> indicarlo nei commenti; in caso di mancate fonti verrà applicata una
> penalizzazione di 40pt"* — Linee guida progetto

---

*Fine documentazione — Space Shooter v1.0*
