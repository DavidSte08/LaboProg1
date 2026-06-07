# Space Shooter

**Studente:** David Stefanovic  
**Classe:** I2BD  
**Scuola:** SAMT — Scuola Arti e Mestieri Trevano  
**Anno scolastico:** 2025/2026  
**Modulo:** Labo1 — Progetto LibGDX  
**Consegna:** 07.06.2026  

---

## Descrizione

Space Shooter è un gioco sparatutto a scorrimento verticale (shoot 'em up) sviluppato con il framework LibGDX in Java. Il giocatore pilota una navicella spaziale e affronta 10 ondate di nemici alieni con difficoltà crescente. Ogni 5 ondate appare un boss con 3 fasi di attacco.

## Come avviare

```bash
./gradlew lwjgl3:run
```

## Controlli

| Tasto | Azione |
|---|---|
| WASD / Frecce | Muovi la navicella |
| SPAZIO | Spara |
| Q | Attiva doppio sparo (slot 1) |
| E | Attiva rallentamento nemici (slot 2) |
| ESC / P | Pausa |

Controller gamepad supportato.

## Librerie di terze parti

- **LibGDX 1.14.0** — Apache 2.0
- **StartupHelper.java** — © 2020 damios — Apache 2.0 (file incluso nel template LibGDX)
- **JUnit 4** — EPL 1.0
- **gdx-controllers** — Apache 2.0

## Struttura progetto

```
core/src/main/java/.../
├── Main.java                  ← Entry point
├── entities/                  ← GameObject, Player, Enemy, PowerUp, Bullet
├── screens/                   ← MenuScreen, GameScreen, PauseScreen, HowToPlayScreen
├── utils/                     ← AudioManager, ScoreManager, WaveManager
└── weapons/                   ← Bullet

core/src/test/java/.../
└── GameLogicTest.java         ← 19 unit test JUnit 4

assets/
├── images/                    ← 31 PNG
└── sounds/                    ← 10 OGG

docs/
├── DOCUMENTAZIONE.docx        ← Documentazione tecnica completa
└── ClassDiagram.md            ← Diagramma delle classi
```

## Dichiarazione uso AI

Questo progetto è stato sviluppato con il supporto di Claude (Anthropic — https://claude.ai).  
Il commento `// NOTA AI` è presente in cima a ogni file Java sorgente.  
Dichiarazione obbligatoria per il Labo1 SAMT I2BD 2025/2026.
