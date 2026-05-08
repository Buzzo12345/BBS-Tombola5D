package serverrest.classes;

import java.util.UUID;

public class Cella {

	private UUID id;
	private UUID cartellaId;
	private int valore;
	private int riga;
	private int colonna;

	public Cella() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getCartellaId() {
		return cartellaId;
	}

	public void setCartellaId(UUID cartellaId) {
		this.cartellaId = cartellaId;
	}

	public int getValore() {
		return valore;
	}

	public void setValore(int valore) {
		this.valore = valore;
	}

	public int getRiga() {
		return riga;
	}

	public void setRiga(int riga) {
		this.riga = riga;
	}

	public int getColonna() {
		return colonna;
	}

	public void setColonna(int colonna) {
		this.colonna = colonna;
	}
}
