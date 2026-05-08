/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package serverrest.service;

import serverrest.classes.Estrazione;
import serverrest.classes.Tombolata;
import serverrest.classes.Utente;
import serverrest.classes.Vincita;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;



/**
 *
 * @author delfo
 */


public class PostHandler implements HttpHandler {
    
    // Istanza Gson configurata per pretty printing
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        // Verifica che sia una richiesta POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            inviaErrore(exchange, 405, "Metodo non consentito. Usa POST");
            return;
        }
        
        try {
            // Legge il body della richiesta
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
            );
            
            // GSON converte automaticamente il JSON in oggetto Java
            TombolaRequest request = gson.fromJson(reader, TombolaRequest.class);
            reader.close();
            
            // Validazione
            if (request == null) {
                inviaErrore(exchange, 400, "Body della richiesta vuoto o non valido");
                return;
            }

            String azione = request.getAzione() == null ? "" : request.getAzione().trim().toUpperCase();
            Map<String, Object> risposta = new LinkedHashMap<>();

            switch (azione) {
                case "CREA_TOMBOLATA": {
                    if (request.getNome() == null || request.getNome().trim().isEmpty()) {
                        inviaErrore(exchange, 400, "Nome tombolata mancante o vuoto");
                        return;
                    }
                    Tombolata tombolata = TombolaService.creaTombolata(
                            request.getNome(),
                            request.getDataFineAssegnazioneCartelle()
                    );
                    risposta.put("azione", azione);
                    risposta.put("tombolata", tombolata);
                    risposta.put("messaggio", "Tombolata creata correttamente");
                    break;
                }
                case "REGISTRA_UTENTE": {
                    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                        inviaErrore(exchange, 400, "Email mancante o vuota");
                        return;
                    }
                    if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
                        inviaErrore(exchange, 400, "Nickname mancante o vuoto");
                        return;
                    }
                    Utente utente = TombolaService.registraUtente(
                            request.getEmail(),
                            request.getNickname(),
                            request.getRuolo()
                    );
                    risposta.put("azione", azione);
                    risposta.put("utente", utente);
                    risposta.put("messaggio", "Utente registrato correttamente");
                    break;
                }
                case "CREA_CARTELLA": {
                    if (request.getUtenteId() == null || request.getTombolataId() == null) {
                        inviaErrore(exchange, 400, "Servono utenteId e tombolataId");
                        return;
                    }
                    Map<String, Object> cartellaCreata = TombolaService.creaCartella(
                            request.getUtenteId(),
                            request.getTombolataId()
                    );
                    risposta.put("azione", azione);
                    risposta.putAll(cartellaCreata);
                    risposta.put("messaggio", "Cartella creata correttamente");
                    break;
                }
                case "ESTRAI_NUMERO": {
                    if (request.getTombolataId() == null) {
                        inviaErrore(exchange, 400, "Serve tombolataId");
                        return;
                    }
                    Estrazione estrazione = TombolaService.effettuaEstrazione(request.getTombolataId());
                    risposta.put("azione", azione);
                    risposta.put("estrazione", estrazione);
                    risposta.put("messaggio", "Numero estratto correttamente");
                    break;
                }
                case "REGISTRA_VINCITA": {
                    if (request.getTombolataId() == null || request.getUtenteId() == null) {
                        inviaErrore(exchange, 400, "Servono tombolataId e utenteId");
                        return;
                    }
                    if (request.getTipo() == null || request.getTipo().trim().isEmpty()) {
                        inviaErrore(exchange, 400, "Tipo vincita mancante o vuoto");
                        return;
                    }
                    Vincita vincita = TombolaService.registraVincita(
                            request.getTombolataId(),
                            request.getUtenteId(),
                            request.getTipo()
                    );
                    risposta.put("azione", azione);
                    risposta.put("vincita", vincita);
                    risposta.put("messaggio", "Vincita registrata correttamente");
                    break;
                }
                default:
                    inviaErrore(exchange, 400,
                            "Azione non valida. Usare: CREA_TOMBOLATA, REGISTRA_UTENTE, CREA_CARTELLA, ESTRAI_NUMERO, REGISTRA_VINCITA");
                    return;
            }

            String jsonRisposta = gson.toJson(risposta);
            inviaRisposta(exchange, 200, jsonRisposta);
            
        } catch (JsonSyntaxException e) {
            inviaErrore(exchange, 400, "JSON non valido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            inviaErrore(exchange, 400, e.getMessage());
        } catch (Exception e) {
            inviaErrore(exchange, 500, "Errore interno del server: " + e.getMessage());
        }
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
