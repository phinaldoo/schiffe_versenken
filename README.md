# Schiffe Versenken

## Spielen

### 1. Kompilieren

```bash
javac *.java
```

### 2. Spiel starten

1. Terminal-Version
```bash
java Main
```

2. GUI-Version
```bash
java BattleshipGUI
```


## Features

- 2-Spieler-Modus im Wechsel
- Nach jedem Treffer oder versenkten Schiff darf derselbe Spieler sofort nochmal schießen
- Schiff-Setup pro Spieler: **manuell** oder **zufällig**
- Robuste Eingabeprüfung (Koordinaten, Richtung, Bereich)
- Treffer/Miss/Versenkt-Logik
- Sieg-Erkennung, wenn alle gegnerischen Schiffsfelder getroffen wurden
- Board-Rendering (in der Konsole):
  - `S` = eigenes Schiff
  - `X` = Treffer
  - `o` = Fehlschuss
  - `.` = unbekannt
- Schiffe dürfen sich nicht überschneiden oder direkt berühren (inkl. diagonal)
