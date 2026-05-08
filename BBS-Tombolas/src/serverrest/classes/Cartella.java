package serverrest.classes;

import java.util.UUID;

public class Cartella {

	private UUID id;
	private UUID utenteId;
	private UUID tombolataId;

	public Cartella() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUtenteId() {
		return utenteId;
	}

	public void setUtenteId(UUID utenteId) {
		this.utenteId = utenteId;
	}

	public UUID getTombolataId() {
		return tombolataId;
	}

	public void setTombolataId(UUID tombolataId) {
		this.tombolataId = tombolataId;
	}
}
