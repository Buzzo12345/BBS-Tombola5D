package serverrest.classes;

import java.util.UUID;

public class Estrazione {

	private UUID id;
	private UUID tombolataId;
	private int numero;
	private String timestamp;

	public Estrazione() {
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

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
