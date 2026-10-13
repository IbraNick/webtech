package meinproject.htw.webtech;

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	@GetMapping
	public List<Note> getAllNotes() {
		return List.of(
				new Note(1L, "Einkaufsliste", "Milch, Brot, Kaffee und Nudeln nicht vergessen.",
						Instant.parse("2026-10-10T14:30:00Z")),
				new Note(2L, "Webtech-Ideen", "Die Spracherkennung läuft lokal im Browser, das Audio verlässt das Gerät nie.", 
						 Instant.parse("2026-10-11T09:15:00Z")),
				new Note(3L, "Lecture summary", "The professor explained REST basics: resources, verbs and status codes.",
						 Instant.parse("2026-10-12T16:45:00Z")));
	}
}