package meinproject.htw.webtech;

import java.time.Instant;

public class Note {

	private Long id;
	private String title;
	private String transcript;
	private Instant createdAt;

	public Note(Long id, String title, String transcript, Instant createdAt) {
		this.id = id;
		this.title = title;
		this.transcript = transcript;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getTranscript() {
		return transcript;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}