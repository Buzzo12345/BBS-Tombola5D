package serverrest.classes;

import java.util.UUID;

public class Tombolata {

    private UUID id;
    private String nome;
    private String stato;
    private String dataFineAssegnazioneCartelle;

    public Tombolata() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getDataFineAssegnazioneCartelle() {
        return dataFineAssegnazioneCartelle;
    }

    public void setDataFineAssegnazioneCartelle(String dataFineAssegnazioneCartelle) {
        this.dataFineAssegnazioneCartelle = dataFineAssegnazioneCartelle;
    }
}