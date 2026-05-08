/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverrest.service;


/**
 *
 * @author delfo
 */
public class ConvertitoreRequest {
    private double operando;
    private String operatore1;
    private String operatore2;
    
    // Costruttore vuoto necessario per GSON
    public ConvertitoreRequest() {
    }
    
    // Costruttore con parametri
    public ConvertitoreRequest(double operando, String operatore1, String operatore2) {
        this.operando = operando;
        this.operatore1 = operatore1;
        this.operatore2 = operatore2;
    }
    
    // Getter
    public double getOperando() {
        return operando;
    }
    
    public String getOperatore1() {
        return operatore1;
    }
    
    public String getOperatore2() {
        return operatore2;
    }

    // Setter
    public void setOperando(double operando) {
        this.operando = operando;
    }
    
    public void setOperatore1(String operatore1) {
        this.operatore1 = operatore1;
    }
    
    public void setOperatore2(String operatore2) {
        this.operatore2 = operatore2;
    }

    @Override
    public String toString() {
        return "OperazioneRequest{" +
                "operando=" + operando +
                ", operatore1='" + operatore1 + '\'' +
                ", operatore2='" + operatore2 + '\'' +
                '}';
    }
}
