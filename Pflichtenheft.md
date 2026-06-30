# Pflichtenheft

## Projekt

**Projektname:** Schiffe Versenken  

---

## 1. Einleitung

Dieses Pflichtenheft beschreibt die konkrete technische und funktionale Umsetzung des im Lastenheft definierten Spiels "Schiffe Versenken". Ziel ist ein lokal ausführbares Spiel für zwei menschliche Spieler auf einem Gerät. Die Anwendung wurde in Java umgesetzt und steht in zwei Varianten zur Verfügung:

- als Konsolenanwendung
- als grafische Desktop-Anwendung mit Java Swing

Beide Varianten greifen auf denselben fachlichen Regelkern zurück.

---

## 2. Zielsystem

Das Produkt stellt ein vollständiges, regelkonformes Spiel bereit, das folgende Phasen unterstützt:

1. Eingabe der Spielernamen
2. Schiffsplatzierung pro Spieler
3. wechselseitige Schussphase
4. automatische Gewinnerermittlung
5. Darstellung des Endergebnisses

---

## 3. Systemkontext und Abgrenzung

## 3.1 Einsatzkontext

Die Anwendung läuft lokal auf einem einzelnen Rechner.

---

## 4. Technische Rahmenbedingungen

- Programmiersprache: **Java**
- GUI-Technologie: **Swing**
- Build: Kompilierung über `javac *.java`
- Startpunkte:
  - `java Main`
  - `java BattleshipGUI`

---

## 5. Architektur

## 5.1 Architekturmuster

Die Umsetzung folgt einer einfachen Schichtentrennung:

1. **Engine**
   - enthält Spiellogik, Spielzustand und Regeldurchsetzung

2. **Präsentation Konsole**
   - textuelle Benutzereingabe und Ausgabe

3. **Präsentation GUI**
   - grafische Benutzerführung und Visualisierung

## 5.2 Architekturziele

Die Architektur wurde so umgesetzt, dass:

- Regeln nur einmal zentral implementiert werden
- sowohl Konsole als auch GUI denselben Regelkern verwenden
- Änderungen an der Darstellung möglichst nicht die Spiellogik betreffen

---

## 6. Fachliches Domänenmodell

## 6.1 Übersicht

Die Game Engine besteht aus folgenden zentralen Klassen und Enums:

- `GAME`
- `GRID`
- `SHIP`
- `Coordinate`
- `ShipType`
- `ShotReport`
- `ShotResult`
- `Orientation`

## 6.2 Verantwortung der Engine-Klassen

### `GAME`

Verantwortlich für:

- Verwaltung der beiden Spielerbretter
- Verwaltung des aktiven Spielers
- Gewinnerermittlung
- Delegation eines Schusses auf das gegnerische Brett

### `GRID`

Verantwortlich für:

- Speichern von Schiffen und Schüssen
- Prüfen zulässiger Platzierungen
- zufällige und manuelle Schiffsplatzierung
- Entfernen eines Schiffs
- Auswertung von Schüssen
- textuelle Brettdarstellung für den Konsolenmodus

### `SHIP`

Verantwortlich für:

- Koordinaten eines einzelnen Schiffs
- Trefferstatus der einzelnen Segmente
- Erkennung, ob das Schiff versenkt ist

### `Coordinate`

Verantwortlich für:

- Repräsentation einer Spielfeldkoordinate
- Parsing von Benutzereingaben
- Grenzprüfung

### `ShipType`

Verantwortlich für:

- Definition eines Schiffstyps mit Name, Länge und Anzahl

### `ShotReport`

Verantwortlich für:

- Transport des Ergebnisses eines Schusses von der Domain in die Oberfläche

### `ShotResult`

Enum für:

- `MISS`
- `HIT`
- `SUNK`
- `ALREADY_TARGETED`

### `Orientation`

Enum für:

- `HORIZONTAL`
- `VERTICAL`

---

## 7. Produktdaten und Regeln

## 7.1 Spielfeld

- Standardgröße im Produkt: **10 x 10**

## 7.2 Flotte

Die Standardflotte wird in beiden Startpunkten gleich erzeugt:

| Schiffstyp | Länge | Anzahl |
| --- | ---: | ---: |
| Schlachtschiff | 5 | 1 |
| Zerstörer | 4 | 1 |
| U-Boot | 3 | 2 |
| Patrouillenboot | 2 | 1 |

## 7.3 Platzierungsregeln

Die Implementierung erzwingt:

- Platzierung vollständig innerhalb des Brettes
- keine Überlappung
- kein direktes Aneinandergrenzen
- kein diagonales Berühren

## 7.4 Schussregeln

Die Implementierung unterscheidet:

- **Fehlschuss**
- **Treffer**
- **Schiff versenkt**
- **bereits beschossenes Feld**

## 7.5 Zugregeln

Die Spielsteuerung setzt um:

- `MISS` -> Zugwechsel
- `HIT` -> gleicher Spieler bleibt am Zug
- `SUNK` -> gleicher Spieler bleibt am Zug

## 7.6 Siegbedingung

Ein Spieler gewinnt, wenn alle Schiffszellen des gegnerischen Brettes getroffen wurden.

---

## 8. Funktionale Umsetzung

## 8.1 Konsolenmodus

### Start

Der Konsolenmodus startet in `Main.main(String[] args)`.

### Eingaben

Unterstützt werden:

- Spielernamen
- Auswahl zwischen manuellem und zufälligem Setup
- Koordinaten im Format `A1`
- Koordinaten im Format `1,1`
- Ausrichtung über `H` oder `V`

### Ablauf

1. Namen einlesen
2. Flotte erzeugen
3. `GAME` instanziieren
4. Setup für Spieler 1
5. Setup für Spieler 2
6. Spielschleife bis `game.isGameOver()`
7. Gewinner ausgeben

### Besondere Umsetzung

- Nach `ALREADY_TARGETED` wird kein Zug verbraucht
- das Brett wird textuell gerendert
- `clearScreenHint()` verwendet ANSI-Steuerzeichen zur optischen Trennung

## 8.2 GUI-Modus

### Start

Die GUI startet in `BattleshipGUI.main(String[] args)`.

### Screens

Die grafische Anwendung verwendet `CardLayout` und umfasst:

- `WelcomePanel`
- `SetupPanel`
- `GamePanel`
- `ResultPanel`

### GUI-Ablauf

1. Namen im Startscreen eingeben
2. Spiel starten
3. Setup Spieler 1
4. Übergangsscreen
5. Setup Spieler 2
6. Übergangsscreen
7. Spielscreen
8. Ergebnisscreen

---

## 9. Detailbeschreibung der GUI-Komponenten

## 9.1 `BattleshipGUI`

### Aufgaben

- Hauptfenster
- Navigation zwischen den Screens
- Verwaltung der aktuellen Spielinstanz
- Anzeige von Übergangs- und Einstellungsdialogen

### Wichtige Methoden

- `showWelcome()`
- `startGame(String, String)`
- `setupComplete(int)`
- `endTurn()`
- `showGameOver(String)`
- `newGame()`
- `showSettingsDialog()`
- `leaveCurrentGame()`
- `closeGame()`

### Besondere Umsetzung

- globale Swing-Schrift wird gesetzt
- die GUI wird über `SwingUtilities.invokeLater(...)` gestartet
- der Einstellungsdialog erlaubt:
  - Spiel verlassen
  - Anwendung schließen
  - Rückkehr ins Spiel

## 9.2 `WelcomePanel`

### Aufgaben

- Startscreen
- Erfassung der Spielernamen
- visuelle Einführung

### Eigenschaften

- animierter Hintergrund mit Wellen und Blasen
- zwei Eingabefelder
- Start-Button
- Enter-Unterstützung zum Starten

## 9.3 `SetupPanel`

### Aufgaben

- interaktive Schiffsplatzierung
- Anzeige des aktuellen Schiffs
- Fortschrittsanzeige der Flotte

### Interaktionsmöglichkeiten

- Platzierung per Klick
- Rotation per Button
- Rotation per Taste `R`
- zufälliges Ergänzen noch offener Schiffe
- Aufnehmen bereits platzierter Schiffe zur Korrektur

### Technische Umsetzung

Der Setup-Status wird über eine Liste konkreter Platzierungsziele verwaltet. Jedes Ziel entspricht genau einem real zu platzierenden Schiff, z. B. `U-Boot #2`.

Wesentliche Zustandsfelder:

- `currentPlayerIndex`
- `currentGrid`
- `currentOrientation`
- `placementTargets`
- `currentPlacementIndex`

### Besondere Umsetzung

- bereits platzierte Schiffe können nach Abschluss nochmals angeklickt und verschoben werden
- bei fehlgeschlagener Teil-Zufallsplatzierung werden nur die in diesem Schritt automatisch gesetzten Schiffe zurückgenommen

## 9.4 `GamePanel`

### Aufgaben

- Darstellung des eigentlichen Spiels
- Schussabgabe
- visuelle Rückmeldung über Treffer und Fehlschüsse

### Anzeigeelemente

- eigenes Brett
- gegnerisches Brett
- Spieleranzeige
- Statusanzeige
- Nachrichtenbereich
- Button `Zug beenden`

### Technische Umsetzung

- auf dem gegnerischen Brett werden Mausklicks abgefangen
- vor `playTurn(...)` wird geprüft, ob ein Feld bereits beschossen wurde
- bei `MISS` wird `canShoot` deaktiviert und der Zugende-Button angezeigt
- bei `HIT` und `SUNK` bleibt der Spieler aktiv
- bei Sieg wird mit kurzer Verzögerung der Ergebnisscreen angezeigt

## 9.5 `BoardPanel`

### Aufgaben

- gemeinsame Zeichenkomponente für Setup und Match

### Eigenschaften

- Darstellung als eigenes Brett oder Gegneransicht
- Hover-Erkennung
- Klickerkennung
- Platzierungsvorschau

### Visuelle Zustände

- Wasser
- eigenes Schiff
- Treffer
- Fehlschuss
- versenktes Schiff
- gültige oder ungültige Platzierungsvorschau

## 9.6 `ResultPanel`

### Aufgaben

- Gewinneranzeige
- Abschlussbildschirm
- Neustart oder Beenden

### Eigenschaften

- Konfetti-Animation
- Button `Neues Spiel`
- Button `Beenden`

## 9.7 UI-Hilfskomponenten

### `ModernButton`

- eigener Button-Look
- Hover- und Press-Effekte
- Farbverläufe und Schatten

### `ModernTextField`

- eigener Feld-Look
- Placeholder-Unterstützung
- Fokusdarstellung

### `SettingsIconButton`

- benutzerdefiniertes Zahnrad-Icon
- Hover- und Press-Feedback

---

## 10. Details über Implementierung

## 10.1 `GAME`

- `GAME(String, String, int, List<ShipType>)`
- `getPlayerGrid(int)`
- `getActivePlayerGrid()`
- `getOpponentGrid()`
- `getActivePlayerIndex()`
- `getActivePlayerName()`
- `getOpponentName()`
- `getFleetDefinition()`
- `switchTurn()`
- `playTurn(Coordinate)`
- `isGameOver()`
- `getWinnerName()`

## 10.2 `GRID`

- `canPlaceShip(Coordinate, Orientation, int)`
- `placeShip(String, Coordinate, Orientation, int)`
- `placeShipsRandomly(List<ShipType>, long)`
- `placeShipRandomly(String, int, Random, int)`
- `removeShip(String)`
- `getShipAt(Coordinate)`
- `fireAt(Coordinate)`
- `isAllShipsSunk()`
- `hasShotAt(Coordinate)`
- `hasShipAt(Coordinate)`
- `renderOwnBoard()`
- `renderOpponentView()`
- `getShips()`
- `getUsername()`
- `getSize()`

## 10.3 `SHOTREPORT`

- `getResult()`
- `getCoordinate()`
- `getSunkShipName()`
- `isGameWon()`
- `grantsExtraTurn()`