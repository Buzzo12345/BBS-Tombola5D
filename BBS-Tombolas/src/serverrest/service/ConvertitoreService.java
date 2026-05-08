/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverrest.service;

/**
 *
 * @author delfo
 */
public class ConvertitoreService{
    
    /**
     * Esegue l'operazione matematica richiesta
     * 
     * @param operando  L' operando
     * @param operatore1 Il primo operatore
    * @param operatore2 Il secondo operatore
     * @return Il risultato dell'operazione
     * @throws IllegalArgumentException se l'operatore non è valido o divisione per zero
     */
    public static double converti(Double operando, String operatore1, String operatore2) 
            throws IllegalArgumentException {
        
        if (operando == null) {
            throw new IllegalArgumentException("Operando non può essere nullo o vuoto");
        }
        
        // Convertiamo l'operatore in maiuscolo per gestire input case-insensitive
        String op1 = operatore1.toUpperCase().trim();
        String op2 = operatore2.toUpperCase().trim();
        
        switch (op1) {
            case "METRI":
            case "METRI (M)":
            case "MT":
            case "M":
                if (op2.equals("YARD") || op2.equals("YARD (YD)")) {
                    return operando * 1.09361;
                } else {
                    throw new IllegalArgumentException(
                        "Operatore di conversione non valido: " + operatore2 + 
                        ". Operatori consentiti: YARD"
                    );
                }
            case "YARD":
            case "YARD (YD)":
            case "YD":
                if (op2.equals("METRI") || op2.equals("METRI (M)")) {
                    return operando / 1.09361;
                } else {
                    throw new IllegalArgumentException(
                        "Operatore di conversione non valido: " + operatore2 + 
                        ". Operatori consentiti: METRI"
                    );
                }
            default:
                throw new IllegalArgumentException(
                    "Operatori di conversione non valido: " + operatore1 + ", " + operatore2 + 
                    ". Operatori consentiti: METRI, YARD"
                );
        }
        
    }
}
