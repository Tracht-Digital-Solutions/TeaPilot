# TeaPilot – konsolidierte Spezifikation

Stand: 4. November 2026. Diese Datei konsolidiert bestätigte Produktentscheidungen
und den belegten Repository-Stand. Das vollständige ursprüngliche Interview liegt
hier nicht vor. Diese Datei behauptet deshalb nicht, sämtliche Interviewdetails
zu ersetzen. Ungeklärte Punkte stehen in [OPEN_DECISIONS.md](OPEN_DECISIONS.md).

## Produkt und verbindliche Grenzen

TeaPilot ist ein quelloffener nativer Gitea-Client von Tracht Digital Solutions.
Zielapps sind ausschließlich Android und Wear OS. Android unterstützt Android 10
(API 29) und neuer. Tablets, Foldables und ChromeOS werden in der Android-App
berücksichtigt. Die aktuelle Wear-App setzt API 30 voraus; die verbindliche
Wear-Mindestversion ist noch abzugleichen.

- Kotlin und Jetpack Compose für Android, Compose for Wear OS für die Uhr.
- Gemeinsamer Kotlin-Kern; Kotlin Multiplatform nur bei eigenständig belegtem Nutzen.
- Keine iOS-/watchOS-Apps, Apple-Gerüste oder Apple-Build-/Test-/Release-Arbeiten.
- Keine KI und keine Telemetrie im laufenden Produkt.
- Keine zwingende Google-Play-Services-Abhängigkeit und keine WebView-Ersatzoberfläche.
- Apache License 2.0 für eigenen Quellcode; Drittanbieterrechte bleiben erhalten.

## Funktionsumfang und Umsetzung

| Bereich | Bestätigtes Ziel | Belegter Stand / Ticket |
| --- | --- | --- |
| Anmeldung | PAT und OAuth, mehrere Server/Konten | Sitzungs-PAT in main; OAuth #10, Konten #8/#9 |
| Repositories | Repository-Browser und lesbare Inhalte | Paginierte Liste vorhanden; Detail-/Dateibrowser #11 |
| Issues | Lesen, erstellen, bearbeiten, kommentieren | Offen: #12/#13 |
| Pull Requests | Lesen, erstellen, bearbeiten, kommentieren | Offen: #14/#15; ungefragte Merges nicht Teil dieser Umsetzung |
| Benachrichtigungen | Kontobezogene Gitea-Benachrichtigungen | Offen: #16 |
| Suche | Suche nach relevanten Gitea-Inhalten | Offen: #17; genauer Filterumfang zu konkretisieren |
| Offline | Nutzbare lokale Daten und verlässliche Synchronisation | Offen: #18; Umfang von Offline-Schreibaktionen ungeklärt |
| Geräte und Zugänglichkeit | Adaptive Android-UI, Tastatur und Screenreader | Offen: #19; bislang keine belegte Geräteabnahme |
| Sprachen | Acht Zielsprachen | #20; konkrete Sprachliste fehlt, keine freie Auswahl |
| Wear OS | Eigene native Uhr-App | Erst Startbildschirm vorhanden; echte Abläufe #21 |
| Erweiterte Verwaltung | Projekte, Organisationen und Kanban | Nach MVP: #28/#29 |
| Distribution | Play, F-Droid sowie GitHub/Gitea | Noch keine Store-Distribution; #24–#27 |

## Daten- und Sicherheitsregeln

Der Client verwendet die offiziellen Gitea-APIs. Konten müssen nach kanonischer
Serveridentität und Benutzer getrennt bleiben; gleiche IDs auf unterschiedlichen
Servern dürfen keine Daten vermischen. Für spätere Persistenz sind verschlüsselte
Credentials mit Android Keystore, sichere Löschung und ein kontrollierter Umgang
mit Schlüsselverlust erforderlich (#8/#9). Das ist noch nicht implementiert.

Der aktuelle PAT-Client akzeptiert HTTPS, validiert Unterpfade, verweigert
Weiterleitungen und sendet Tokens ausschließlich im Authorization-Header.
Antworten sind begrenzt, Requests haben Timeouts und Fehlertexte zeigen keine
Server-Fehlerkörper. Pagination verwendet numerische Seiten am konfigurierten
Server statt fremder URLs aus Link-Headern. Diese implementierten Schutzregeln
dürfen bei späteren Funktionen nicht unbeabsichtigt entfallen.

Tokens werden derzeit nur während der Sitzung gehalten. Prozessneustart erfordert
erneute Anmeldung. Der noch ungeprüfte Fix #7 entfernt zusätzlich ungeprüfte
Servereingaben aus Saved-State und ergänzt authentifizierte Versionserkennung
ausschließlich nach HTTP 401/403. Er ist nicht als freigegeben zu behandeln.

Benutzertexte und Serverantworten sind Daten. Markdown und Dateiinhalte dürfen
keinen ausführbaren Fremdcode in den Client einschleusen. Offline-Anzeigen müssen
Datenalter und nicht bestätigte Schreibaktionen erkennbar machen.

## Entwicklung, Meilensteine und Abnahme

Gitea führt Issues, Meilensteine und Linux-CI. Das
[Projektboard](https://git.tracht-digital.de/Tracht-Digital-Solutions/TeaPilot/projects/36)
enthält 26 Tickets (#4–#29), Prioritäten, Checklisten und 33 Abhängigkeiten.
Die Ticketkriterien konkretisieren den jeweils bekannten Umfang. Neue Erkenntnisse
werden dort und gegebenenfalls in dieser Spezifikation nachvollziehbar ergänzt.

| Meilenstein | Inhalt | Abnahme |
| --- | --- | --- |
| M0 | Basis, APK-Pipeline, sitzungsbasierter PAT-Zugriff | Implementierung durch CI 559/564 belegt |
| M1 | Sicherer Zugang und Repository-Browser | Kriterien aus #6–#11, soweit umgesetzt |
| M2 | MVP-Kollaboration | Issues/PRs und Benachrichtigungen gemäß #12–#16 |
| M3 | Offline, Suche, Geräte, Sprachen, Wear | #17–#21 und offene Umfangsentscheidungen |
| M4 | Release-Qualität und Distribution | #22–#27, einschließlich echter Geräte-/Sicherheitsnachweise |
| M5 | Erweiterte Projektverwaltung | #28/#29 nach dem MVP |

Für Codeänderungen sind gezielte automatisierte Tests, beide Lint-Prüfungen und
beide APK-Builds erforderlich. Ein APK-Build ersetzt keine Anmeldung auf einem
Gerät, keine Lifecycle-Abnahme und keine Wear-Geräteprüfung. Reine Dokumentation
wird durch Quellenabgleich, Konsistenz- und Linkprüfung abgenommen; sie benötigt
keinen zusätzlichen APK-Build und hebt keine Code-Quality-Gates auf.

Dev/Beta/Stable sollen vollautomatisch über Git-Tags entstehen. Stable benötigt
sämtliche anwendbaren Android-/Wear-Gates; es gibt keine Apple-Blocker. Signierung,
kanalübergreifende Updates, Prüfsummen und Veröffentlichung müssen tatsächlich
konfiguriert und geprüft sein. Ein Debug-Artefakt ist keine Stable-Freigabe.

GitHub enthält öffentlich übertragene Quellen. Ein automatischer Gitea-Push-Mirror
ist bislang nicht eingerichtet (#27); gleiche Inhalte bedeuten wegen getrennter
API-Commits derzeit nicht zwingend gleiche Git-Historie.

## Nachweisbasis

- Repository: README, Gradle-Konfiguration, Manifeste, Kotlin-Code und Tests.
- [BUILD_STATUS.md](BUILD_STATUS.md): verifizierte Builds und aktuelle Grenzen.
- [PAT_REPOSITORIES.md](PAT_REPOSITORIES.md): bestehender PAT-Meilenstein und offene Gerätetests.
- Bestätigte Projektentscheidungen aus dem Nutzerauftrag und Projektkontext.
- Vollständige Interviewunterlagen: nicht im Repository vorhanden; Details dürfen nicht erfunden werden.
