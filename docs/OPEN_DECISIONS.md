# Offene Entscheidungen und Nachweise

Diese Liste trennt fehlende Produktentscheidungen von noch nicht erledigter
Implementierung. Konsolidierung bedeutet nicht, dass die folgenden Punkte
bereits entschieden oder umgesetzt sind.

| ID | Offener Punkt | Betroffene Arbeit | Umgang bis zur Klärung |
| --- | --- | --- | --- |
| O-01 | Exakte Liste der acht vereinbarten Zielsprachen fehlt | #20 | Keine Sprachen oder Übersetzungen frei erfinden; Umsetzung bleibt blockiert |
| O-02 | Verbindliche Wear-Mindestversion und genaue Uhr-Abläufe fehlen | #21 | API 30 und standalone-Manifest sind aktueller Prototyp, keine abgeschlossene Produktentscheidung |
| O-03 | Umfang von Offline-Daten und Offline-Schreibaktionen unvollständig | #18 | Keine bestätigte erfolgreiche Änderung ohne Serverbestätigung anzeigen |
| O-04 | Gitea-Mindestversion und verbindliche Funktionsmatrix fehlen | #10/#11/#28 | API-Unterstützung prüfen und Grenzen dokumentieren statt universelle Kompatibilität behaupten |
| O-05 | Vollständige ursprüngliche Interviewunterlagen fehlen im Repository | Gesamter Plan | Bestätigte Zusammenfassung verwenden; zusätzlich belegte Entscheidungen später kontrolliert einarbeiten |

## Noch ausstehende technische Nachweise

- #7: Code und Regressionstests liegen in PR #30. CI-Lauf 570 wartet noch auf
  Runner-Zuteilung; der Fix ist nicht gemergt oder als getestet freigegeben.
- #8/#9: Verschlüsselte Kontenspeicherung und Kontoisolation sind noch offen.
- #22: Keine behauptete echte Geräte-/End-to-End-Abnahme für Android oder Wear OS.
- #24/#25: Tag-Releases und dauerhafte Signierung sind noch nicht eingerichtet.
- #26: Keine behauptete Play-/F-Droid-Veröffentlichung.
- #27: Noch kein automatischer GitHub-Spiegel; bisheriger Abgleich erfolgte per API.

## Pflege

Entscheidungen mit ihrer tatsächlichen Quelle im zugehörigen Issue festhalten,
danach SPECIFICATION.md und diese Liste gemeinsam aktualisieren. Eine neue
Annahme darf nicht stillschweigend zur verbindlichen Interviewentscheidung werden.
