/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package serverrest.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 *
 * @author delfo
 */


public class GetHandler implements HttpHandler {
    
    // Istanza Gson configurata per pretty printing
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        // Verifica che sia una richiesta GET
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            inviaErrore(exchange, 405, "Metodo non consentito. Usa GET");
            return;
        }
        
        try {
            Map<String, String> parametri = estraiParametri(exchange.getRequestURI().getQuery());

            String tombolataIdParam = parametri.get("tombolataId");
            Map<String, Object> risposta;

            if (tombolataIdParam == null || tombolataIdParam.trim().isEmpty()) {
                risposta = serverrest.service.TombolaService.statoCompleto();
            } else {
                UUID tombolataId = UUID.fromString(tombolataIdParam);
                risposta = serverrest.service.TombolaService.statoTombolata(tombolataId);
            }

            String jsonRisposta = gson.toJson(risposta);
            inviaRisposta(exchange, 200, jsonRisposta);
            
        } catch (NumberFormatException e) {
            inviaErrore(exchange, 400, "UUID non valido o parametro numerico errato");
        } catch (IllegalArgumentException e) {
            inviaErrore(exchange, 400, e.getMessage());
        } catch (Exception e) {
            inviaErrore(exchange, 500, "Errore interno del server: " + e.getMessage());
        }
    }
    
    /**
     * Estrae i parametri dalla query string
     */
    private Map<String, String> estraiParametri(String query) {
        Map<String, String> parametri = new HashMap<>();
        
        if (query == null || query.isEmpty()) {
            return parametri;
        }
        
        String[] coppie = query.split("&");
        for (String coppia : coppie) {
            String[] keyValue = coppia.split("=");
            if (keyValue.length == 2) {
                try {
                    String chiave = URLDecoder.decode(keyValue[0], "UTF-8");
                    String valore = URLDecoder.decode(keyValue[1], "UTF-8");
                    parametri.put(chiave, valore);
                } catch (Exception e) {
                    // Ignora parametri malformati
                }
            }
        }
        
        return parametri;
    }
    
    /**
     * Invia una risposta di successo
     */
    private void inviaRisposta(HttpExchange exchange, int codice, String jsonRisposta) 
            throws IOException {
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        
        byte[] bytes = jsonRisposta.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(codice, bytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    /**
     * Invia una risposta di errore in formato JSON
     */
    private void inviaErrore(HttpExchange exchange, int codice, String messaggio) 
            throws IOException {
        
        Map<String, Object> errore = new HashMap<>();
        errore.put("errore", messaggio);
        errore.put("status", codice);
        
        String jsonErrore = gson.toJson(errore);
        inviaRisposta(exchange, codice, jsonErrore);
    }
}
