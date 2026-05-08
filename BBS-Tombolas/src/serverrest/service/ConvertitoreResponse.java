/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverrest.service;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @author delfo
 */
public class ConvertitoreResponse {
    private Double operando;
    private String operatore1;
    private String operatore2;
    private Double risultato;
    private String operazione;
    private String timestamp;
    private String requestId;
    
    // Costruttore vuoto necessario per GSON
    public ConvertitoreResponse() {
    }
    
    // Costruttore con parametri
    public ConvertitoreResponse(Double operando, String operatore1, 
                             String operatore2, Double risultato) {
        this.operando = operando;
        this.operatore1 = operatore1;
        this.operatore2 = operatore2;
        this.risultato = risultato;
        this.operazione = String.format("%.2f %s %.2f = %.2f", 
            operando, operatore1, operatore2, risultato);
        this.timestamp = Instant.now().toString();
        this.requestId = UUID.randomUUID().toString();
    }
    
    // Getter
    public Double getOperando() {
        return operando;
    }
    
    public String getOperatore1() {
        return operatore1;
    }
    
    public String getOperatore2() {
        return operatore2;
    }
    
    public Double getRisultato() {
        return risultato;
    }
    
    public String getOperazione() {
        return operazione;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getRequestId() {
        return requestId;
    }
    
    // Setter
    public void setOperando(Double operando) {
        this.operando = operando;
    }
    
    public void setOperatore1(String operatore1) {
        this.operatore1 = operatore1;
    }
    
    public void setOperatore2(String operatore2) {
        this.operatore2 = operatore2;
    }
    
    public void setRisultato(Double risultato) {
        this.risultato = risultato;
    }
    
    public void setOperazione(String operazione) {
        this.operazione = operazione;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}