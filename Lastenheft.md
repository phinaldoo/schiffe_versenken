# Lastenheft

## Projekt

**Projektname:** Schiffe Versenken 

---

## 1. Ausgangssituation

Das bekannte Brettspiel "Schiffe Versenken" soll als digitales Spiel umgesetzt werden. Das Spiel soll lokal ausführbar sein und zwei Personen das Spielen auf demselben Gerät ermöglichen. Die Anwendung soll sowohl in einer textbasierten Konsolenversion als auch in einer grafischen Desktop-Version verfügbar sein.

---

## 2. Zielbestimmung

Ziel ist die Bereitstellung eines vollständigen, lokal lauffähigen Spiels "Schiffe Versenken", das die klassischen Kernregeln digital abbildet und in zwei Bedienvarianten genutzt werden kann:

- **Konsolenmodus** für einfache, textbasierte Nutzung
- **Swing-GUI** für komfortable, grafische Interaktion

---

## 3. Produkteinsatz

### 3.1 Zielgruppe

Die Anwendung richtet sich an:

- Schülerinnen und Schüler

### 3.2 Einsatzumgebung

- Desktop oder Laptop
- installierte Java-Laufzeit bzw. Java-Entwicklungsumgebung
- Ausführung lokal

---

## 4. Funktionale Anforderungen

## 4.1 Allgemeine Spielfunktion

Das System muss:

- ein Spiel für **genau zwei Spieler** verwalten
- für jeden Spieler ein eigenes Spielfeld bereitstellen
- eine vollständige Partie bis zur Gewinnerermittlung ermöglichen
- den aktiven Spieler verwalten
- Treffer, Fehlschüsse und versenkte Schiffe korrekt erkennen

## 4.2 Spielfeld und Flotte

Das Produkt muss folgende fachliche Grundlagen unterstützen:

- quadratisches Spielfeld
- Standardgröße in der Anwendung: **10 x 10**
- Standardflotte:
  - 1 Schlachtschiff mit Länge 5
  - 1 Zerstörer mit Länge 4
  - 2 U-Boote mit Länge 3
  - 1 Patrouillenboot mit Länge 2

## 4.3 Regeln für die Schiffsplatzierung

- Schiffe dürfen nur vollständig innerhalb des Spielfelds platziert werden
- Schiffe dürfen sich nicht überschneiden
- Schiffe dürfen sich nicht berühren
- das Berührungsverbot gilt auch diagonal

## 4.4 Schussregeln

Das System muss:

- Schüsse auf Wasser als Fehlschuss erkennen
- Schüsse auf Schiffsfelder als Treffer erkennen
- vollständig getroffene Schiffe als versenkt markieren
- einen Sieg erkennen, sobald alle Schiffsfelder eines Spielers getroffen wurden
- erkennen, wenn auf ein bereits beschossenes Feld geschossen wird

## 5.5 Zugregeln

Das Produkt muss folgende Zuglogik umsetzen:

- bei **Fehlschuss** wechselt der Zug zum anderen Spieler
- bei **Treffer** bleibt derselbe Spieler am Zug
- bei **versenktem Schiff** bleibt derselbe Spieler ebenfalls am Zug

## 5.6 Konsolenmodus

Die Konsolenversion muss:

- Spielernameneingabe unterstützen
- pro Spieler ein manuelles oder zufälliges Setup ermöglichen
- Koordinateneingaben in zwei Formaten akzeptieren:
  - `A1`
  - `1,1`
- die Spielfelder textuell darstellen
- nach jedem Spielende den Gewinner ausgeben

## 5.7 GUI-Modus

Die grafische Anwendung muss:

- einen Startscreen mit Namenseingabe bereitstellen
- die Schiffsplatzierung pro Spieler grafisch unterstützen
- eine Schiffsrotation ermöglichen
- eine zufällige Platzierung offener Schiffe ermöglichen
- einen Spielscreen mit eigenem und gegnerischem Feld anzeigen
- Treffer, Fehlschüsse und versenkte Schiffe visuell unterscheiden
- einen Ergebnisbildschirm mit Gewinneranzeige anzeigen

## 5.8 Bedienunterstützung in der GUI

Die GUI soll zusätzlich:

- eine moderne, visuell ansprechende Gestaltung besitzen
- Hinweise zum aktuellen Spielzustand anzeigen
- einen Einstellungsdialog zum Verlassen oder Schließen des Spiels bereitstellen
