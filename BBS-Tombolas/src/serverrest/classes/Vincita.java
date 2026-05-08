package serverrest.classes;

import java.util.UUID;

public class Vincita {

	private UUID id;
	private UUID tombolataId;
	private UUID utenteId;
	private String tipo;
	private String timestamp;

	public Vincita() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getTombolataId() {
		return tombolataId;
	}

	public void setTombolataId(UUID tombolataId) {
		this.tombolataId = tombolataId;
	}

	public UUID getUtenteId() {
		return utenteId;
	}

	public void setUtenteId(UUID utenteId) {
		this.utenteId = utenteId;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
