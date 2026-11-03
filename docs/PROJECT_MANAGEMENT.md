# TeaPilot – Projektsteuerung

Führend sind Issues, Meilensteine und CI auf Gitea:
https://git.tracht-digital.de/Tracht-Digital-Solutions/TeaPilot/projects/36

## Arbeitsweise

1. Issue mit erfüllten Abhängigkeiten nach Priorität und Meilenstein auswählen.
2. Karte nach „In Bearbeitung“ verschieben; auf einem Feature-Branch arbeiten.
3. Checkliste und Abnahmekriterien umsetzen; gezielte Tests ergänzen.
4. Core-/Android-Tests, Lint und beide APK-Builds über Linux-Gitea-CI prüfen.
5. PR und tatsächlichen Test-/Buildnachweis im Issue festhalten. Gerätetests
   niemals aus einem erfolgreichen Unit-Test oder APK-Build ableiten.
6. Geprüften Stand integrieren, Quellen auf GitHub abgleichen, Issue schließen
   und Karte nach „Erledigt“ verschieben. Blockaden mit konkretem Grund erfassen.

## Board und Prioritäten

Spalten: Backlog, Als Nächstes, In Bearbeitung, Blockiert, Erledigt.
P1: Sicherheit/notwendiger MVP-Schritt; P2: geplanter Ausbau; P3: später.
Termine werden erst mit realistischer Kapazitätsplanung gesetzt.

## Meilensteine

- M0: Technische Basis und erster sitzungsbasierter PAT-Zugriff (CI 559/564).
- M1: Sicherer Zugang und Repository-Browser.
- M2: Issues, Pull Requests und Benachrichtigungen als MVP.
- M3: Offline, Suche, adaptive/barrierefreie Oberflächen, acht Sprachen, Wear OS.
- M4: Geräte-/Sicherheitsabnahme, Tag-Releases, Signierung und Distribution.
- M5: Erweiterte Projekt-/Organisationsverwaltung und Kanban.

26 Issues (#4–#29) enthalten Schritt-für-Schritt-To-dos, Abnahmekriterien,
Prioritäten und Vorgänger. #4/#5 dokumentieren abgeschlossene Implementierung;
die echte Geräte-/End-to-End-Abnahme bleibt in #22 offen.

## Bekannte Planungsgrenzen

Die vollständige ursprüngliche Interviewspezifikation fehlt im Repository.
Issue #6 konsolidiert bestätigte Anforderungen und offene Entscheidungen;
insbesondere dürfen die acht Zielsprachen nicht frei erfunden werden.
Nur Android und Wear OS. Keine Apple-Arbeiten, keine KI oder Telemetrie.
GitHub wurde bisher per API abgeglichen; ein automatischer Spiegel ist noch
nicht eingerichtet und wird durch #27 verfolgt.

## Erste laufende Aufgabe

#7 sichert die Versionserkennung an zugriffsgeschützten Servern ab und entfernt
ungeprüfte Servereingaben aus Android-Saved-State. Abschluss nur nach grüner CI.
Danach ist #8 (Keystore-gestützte Kontenspeicherung) technisch als Nächstes bereit;
#6 (Spezifikation) und #11 (Repository-Browser) stehen ebenfalls im nächsten Block.
