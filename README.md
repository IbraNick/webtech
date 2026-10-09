# webtech
# Projektstartdokument: Diktiergerät mit lokalem Whisper

Oct 9, 2026

Wir bauen ein Web-Diktiergerät: Nutzer sprechen ins Mikrofon, ein Whisper-Modell transkribiert die Aufnahme lokal im Browser, und der erzeugte Text wird als Notiz gespeichert und verwaltet.

## Projektüberblick

- **Arbeitstitel:** Diktaphon (Alternativen: VoiceNotes, Whisperbox)
- **Idee:** Sprachaufnahme → lokale Transkription → Notiz mit Titel, Text, Sprache und Zeitstempel
- **Besonderheit:** Das Audio verlässt das Gerät nie. Nur der fertige Text geht ans Backend. Das ist unser Datenschutz-Argument in der Präsentation.
- **Zielgruppe:** Studierende und alle, die Gedanken schnell festhalten wollen
- **Team:** Ibrahim(s0602066), Kenan(s0601714)

## Anforderungen der Uni

Das Modul verlangt eine selbst gewählte Web-Anwendung, die im Zweierteam entsteht und öffentlich im Internet läuft.

- Teams: genau zwei Personen (oder Einzelteam), keine größeren Teams
- Backend: Java oder Kotlin mit Spring Boot
- Frontend: TypeScript oder JavaScript mit Vue.js
- Datenbank: Managed PostgreSQL
- Deployment: Render, Frontend über die öffentliche Render-URL erreichbar
- Qualität: Frontend- und Backend-Tests, automatisch ausgeführt per GitHub Actions
- Bedienung: App muss selbsterklärend benutzbar sein
- Abgabe am Ende: Screenshot-Dokumentation, die für jeden Use Case zeigt, wo und wie er umgesetzt ist
- Präsentation: 18. bis 22. Januar, Code-Abgabe und Doku bis Sonntag, 17. Januar, 23:59 Uhr

## Use Cases

Die Muss-Use-Cases decken die Pflichtfunktionen der Milestones ab. Die Kann-Use-Cases kommen nur dazu, wenn Zeit bleibt. Für die Screenshot-Doku braucht jeder Use Case mindestens einen Screenshot.

| Nr. | Use Case | Beschreibung | Prio | Milestone |
| --- | --- | --- | --- | --- |
| UC1 | Notizen anzeigen | Liste aller gespeicherten Notizen mit Titel, Datum und Textvorschau | Muss | M2 (Dummy), M3 |
| UC2 | Sprache aufnehmen | Aufnahme per Mikrofon starten und stoppen, Dauer wird angezeigt | Muss | Finale |
| UC3 | Lokal transkribieren | Whisper läuft im Browser, Fortschritt beim Modell-Download und bei der Transkription sichtbar | Muss | Finale |
| UC4 | Notiz speichern | Transkribierten Text prüfen, Titel vergeben und per POST speichern | Muss | M4 |
| UC5 | Notiz ansehen | Detailansicht einer einzelnen Notiz | Muss | Finale |
| UC6 | Notiz bearbeiten | Text und Titel nachträglich korrigieren (Whisper macht Fehler) | Muss | Finale |
| UC7 | Notiz löschen | Notiz dauerhaft entfernen, mit Bestätigung | Muss | Finale |
| UC8 | Notizen durchsuchen | Suche nach Stichwort in Titel und Text | Kann | Finale |
| UC9 | Sprache wählen | Deutsch, Englisch oder automatische Erkennung | Kann | Finale |
| UC10 | Text kopieren/exportieren | Notiz in die Zwischenablage oder als .txt herunterladen | Kann | Finale |
| UC11 | Fallback | Hinweis, wenn der Browser das Modell nicht ausführen kann (z. B. kein Mikrofon-Zugriff) | Kann | Finale |

## Tech-Stack und Architektur

Der Stack ist vorgegeben. Unsere eigene Entscheidung betrifft nur, wo die Spracherkennung läuft: im Browser des Nutzers, nicht auf dem Server.

| Schicht | Technologie | Aufgabe |
| --- | --- | --- |
| Backend | Java oder Kotlin, Spring Boot (Web, Data JPA, Validation) | REST-API, Speichern und Laden der Notizen |
| Datenbank | Managed PostgreSQL auf Render | Persistenz der Notizen |
| Frontend | Vue 3, TypeScript, Vite | Oberfläche, Mikrofon-Aufnahme, Notizliste |
| Spracherkennung | transformers.js mit Whisper (tiny, base oder small) | Lokale Transkription per WebAssembly oder WebGPU im Web Worker |
| Audio | MediaRecorder und Web Audio API | Aufnahme im Browser, Umwandlung für das Modell |
| Hosting | Render (Web Service, Static Site, PostgreSQL) | Öffentliche Erreichbarkeit |
| CI | GitHub Actions | Tests bei jedem Push |

<img width="1344" height="536" alt="image" src="https://github.com/user-attachments/assets/dbaf2dba-ecf3-4ed3-9cea-4e79fbc830e8" />

Das Frontend schickt nur den fertigen Text als JSON an das Spring-Boot-Backend, das ihn per JPA in PostgreSQL speichert.

Zwei Repositories: eines für das Backend (hier steht auch die Projektbeschreibung im README), eines für das Frontend.

## Datenmodell und REST-API

Es gibt eine zentrale Entity `Note`. Das reicht für alle Pflicht-Use-Cases und hält das Backend schlank.

| Feld | Typ | Beschreibung |
| --- | --- | --- |
| id | Long | Primärschlüssel, automatisch vergeben |
| title | String | Titel der Notiz |
| transcript | String (Text, lang) | Transkribierter Text |
| language | String | Sprachcode, z. B. `de` oder `en` |
| durationSeconds | Integer | Länge der Aufnahme in Sekunden |
| createdAt | Instant | Zeitpunkt des Speicherns |

| Methode | Route | Zweck | Milestone |
| --- | --- | --- | --- |
| GET | `/api/notes` | Alle Notizen, neueste zuerst (in M1 hartkodierte Beispiele) | M1 |
| POST | `/api/notes` | Neue Notiz speichern | M4 |
| GET | `/api/notes/{id}` | Einzelne Notiz | Finale |
| PUT | `/api/notes/{id}` | Notiz bearbeiten | Finale |
| DELETE | `/api/notes/{id}` | Notiz löschen | Finale |

Für die Suche (UC8) reicht ein optionaler Query-Parameter `GET /api/notes?q=stichwort`. CORS muss im Backend für die Frontend-URL freigeschaltet werden, sonst blockiert der Browser die Aufrufe nach dem Deployment.

## Tests

Für die Finalabgabe brauchen wir Frontend- und Backend-Tests, die bei jedem Push automatisch in GitHub Actions laufen. Wir schreiben sie von Anfang an mit, nicht erst im Januar.

| Bereich | Werkzeug | Was getestet wird |
| --- | --- | --- |
| Backend, Unit | JUnit 5, Mockito | Service-Logik, z. B. Validierung leerer Titel oder Texte |
| Backend, Web | `@WebMvcTest` mit MockMvc | Statuscodes und JSON von GET, POST, PUT und DELETE |
| Backend, Datenbank | `@DataJpaTest` mit H2 oder Testcontainers | Speichern, Laden, Löschen und Suche im Repository |
| Frontend, Komponenten | Vitest, Vue Test Utils | `NoteList` rendert pro Notiz einen Eintrag, Formular sendet korrekte Daten |
| Frontend, API-Schicht | Vitest mit gemocktem `fetch` | Fehlerbehandlung bei Netzwerk- und Serverfehlern |
| Whisper-Anbindung | Vitest, Transkriptions-Modul gemockt | Ablauf Aufnahme → Text → Speichern, ohne echtes Modell zu laden |

**GitHub Actions:** Pro Repository ein Workflow `ci.yml`, der bei Push und Pull Request läuft. Backend: Java installieren, `./mvnw test` (bzw. `./gradlew test`). Frontend: Node installieren, `npm ci`, `npm run test`, `npm run build`. Ein Status-Badge im README zeigt, dass alles grün ist.

Das echte Whisper-Modell testen wir nur manuell, weil der Download zu groß und zu langsam für die CI ist.

## Deadlines und Abgaben

Der nächste Termin ist M1 am 18. Oktober, also in neun Tagen. Danach folgt etwa alle drei Wochen ein Milestone, am Ende steht die Finalabgabe am Sonntag, 17. Januar, 23:59 Uhr.

&#91;embedded content: Zeitleiste · 4 Milestones, Finalabgabe, Präsentation\]

| Milestone | Muss erreicht sein | Abzugeben in Moodle |
| --- | --- | --- |
| M1 | Thema, Team, GitHub-Repo, Entity-Klasse, Spring-App mit GET-Route auf Beispieldaten | Teammitglieder, Projektname, Übungsgruppe und Dozent, Link zum Backend-Repo (README mit Projektbeschreibung) |
| M2 | Vue-App gepusht, mindestens eine eigene Komponente mit v-for über Beispiel-Entitäten | Link zum Frontend-Repo |
| M3 | Frontend und Backend auf Render deployed, Frontend ruft die GET-Route auf | Link zur deployten Backend-API (z. B. die Listenroute) |
| M4 | PostgreSQL auf Render angebunden, Frontend ruft POST-Route auf und speichert eine Entität | Link zum deployten Frontend |
| Final | Frontend- und Backend-Tests laufen per GitHub Actions, App ist öffentlich und selbsterklärend nutzbar | Screenshot-Dokumentation: pro Use Case ein Screenshot, der zeigt, wo und wie er umgesetzt ist |

## Grober Ablauf

Die Spracherkennung kommt bewusst erst nach dem Deployment dazu. So läuft das Projekt zu jedem Milestone lauffähig und die riskanteste Komponente blockiert nichts.

1. **Bis M1 (18.10.):** Partner finden und in der Moodle-Gruppenverwaltung eintragen. GitHub-Repo für das Backend anlegen. Spring-Boot-Projekt (Web) mit Klasse `Note` und `GET /api/notes` mit Beispieldaten pushen. README mit Projektbeschreibung schreiben.
2. **M2 (bis 8.11.):** Vue-Projekt (Vite, TypeScript) in eigenem Repo anlegen. Komponente `NoteList.vue` rendert Beispiel-Notizen mit `v-for`, zunächst mit Dummy-Daten im Code.
3. **M3 (bis 22.11.):** Backend als Web Service und Frontend als Static Site auf Render deployen. Frontend holt die Notizen per `fetch` von der Backend-URL. CORS einrichten. Hinweis: Auf dem Free-Tarif schläft das Backend ein und braucht beim ersten Aufruf bis zu eine Minute.
4. **M4 (bis 13.12.):** PostgreSQL-Instanz auf Render anlegen und mit Spring Boot verbinden (JPA, Umgebungsvariablen für die Zugangsdaten). `POST /api/notes` bauen, im Frontend ein Formular zum Anlegen einer Notiz einbauen.
5. **Dezember bis Weihnachten:** Whisper einbauen: Mikrofon-Aufnahme, transformers.js in einem Web Worker, Fortschrittsanzeige. Aufnahme → Text → Speichern verbinden (UC2 bis UC4).
6. **Januar bis 17.1.:** PUT, DELETE und Detailansicht, Suche und Export nach Zeit. Backend- und Frontend-Tests fertigstellen, GitHub Actions einrichten. Screenshot-Dokumentation zu jedem Use Case schreiben. Danach Code-Freeze.
7. **18. bis 22.1.:** Präsentation üben und halten, live auf der Render-URL.

**Arbeitsteilung (Vorschlag):** Eine Person übernimmt das Backend (Spring, Datenbank, Backend-Tests), die andere das Frontend (Vue, Whisper-Anbindung, Frontend-Tests). Deployment, README und Screenshot-Doku teilt ihr euch. Wechselt bei Gelegenheit per Pull Request das Review, damit beide beide Seiten verstehen, denn in der Präsentation müssen beide alles erklären können.

## Risiken und offene Entscheidungen

| Risiko | Auswirkung | Gegenmaßnahme |
| --- | --- | --- |
| Whisper im Browser ist langsam oder ungenau (v. a. tiny bei Deutsch) | Frust bei der Nutzung, schwache Präsentation | base oder small testen, Transkription im Web Worker, Fortschrittsanzeige, Text nachträglich editierbar (UC6) |
| Modell-Download von 40 bis 150 MB beim ersten Besuch | Lange Wartezeit, mobil teuer | Hinweis vor dem Download, Browser-Cache nutzen, Modell erst bei der ersten Aufnahme laden |
| Mikrofon braucht HTTPS und Nutzer-Erlaubnis | Aufnahme geht lokal, aber nicht auf Render | Render liefert HTTPS automatisch, Fehlermeldung bei verweigertem Zugriff |
| Render Free schläft ein, DB-Tarif ist begrenzt | Erster Aufruf dauert, Demo wirkt kaputt | Seite vor der Präsentation aufwecken, Ladeanzeige im Frontend, Tarifbedingungen der Datenbank prüfen |
| Zeitplan rutscht | Milestone verpasst | Whisper erst nach M4 einplanen, Kann-Use-Cases streichen |

**Offene Entscheidungen:**

- Java oder Kotlin im Backend
- Maven oder Gradle
- Projektname
- Wer macht Backend, wer Frontend
- Welches Whisper-Modell (tiny, base oder small), nach Praxistest im Dezember
- Optional: Web Speech API als Fallback, wenn das Modell nicht läuft
