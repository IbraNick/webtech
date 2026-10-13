package meinproject.htw.webtech;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class NoteTest {

	private final Instant createdAt = Instant.parse("2026-10-10T14:30:00Z");

	@Test
	void constructorSetsAllFields() {
		Note note = new Note(7L, "Titel der Notiz", "Text der Notiz", createdAt);

		assertThat(note.getId()).isEqualTo(7L);
		assertThat(note.getTitle()).isEqualTo("Titel der Notiz");
		assertThat(note.getTranscript()).isEqualTo("Text der Notiz");
		assertThat(note.getCreatedAt()).isEqualTo(createdAt);
	}

	@Test
	void createdAtKeepsInstantValue() {
		Note note = new Note(1L, "Titel", "Text", createdAt);

		assertThat(note.getCreatedAt()).isEqualTo(Instant.parse("2026-10-10T14:30:00Z"));
	}
}