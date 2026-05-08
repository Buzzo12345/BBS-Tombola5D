package serverrest.service;

import java.util.UUID;

public class TombolaRequest {

    private String azione;
    private String nome;
    private String dataFineAssegnazioneCartelle;
    private String email;
    private String nickname;
    private String ruolo;
    private UUID utenteId;
    private UUID tombolataId;
    private String tipo;

    public TombolaRequest() {
    }

    public String getAzione() {
        return azione;
    }

    public void setAzione(String azione) {
        this.azione = azione;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataFineAssegnazioneCartelle() {
        return dataFineAssegnazioneCartelle;
    }

    public void setDataFineAssegnazioneCartelle(String dataFineAssegnazioneCartelle) {
        this.dataFineAssegnazioneCartelle = dataFineAssegnazioneCartelle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}