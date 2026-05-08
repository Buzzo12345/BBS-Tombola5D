/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package serverrest.service;

import com.sun.net.httpserver.HttpServer;


import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Server REST per la calcolatrice
 * 
 * @author bambux % daves
 */
public class ServerRest {

    /**
     * Avvia il server REST sulla porta specificata
     * 
     * @param porta la porta su cui avviare il server
     */
    public static void avviaServer(int porta) {
        try {
            // Crea il server sulla porta specificata
            HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
            
            // Endpoint tombola
            server.createContext("/api/tombolata/post", new PostHandler());
            server.createContext("/api/tombolata/get", new GetHandler());
            
            // Endpoint di benvenuto
            server.createContext("/", ServerRest::gestisciBenvenuto);
            
            // Avvia il server
            server.setExecutor(null); // Usa il default executor
            server.start();
            
            // Messaggi di conferma
            System.out.println("==============================================");
            System.out.println("  Benvenuti alla Tombolata!!!");
            System.out.println("==============================================");
            System.out.println("Porta: " + porta);
            System.out.println();
            System.out.println("Endpoint disponibili:");
            System.out.println("  - POST: http://localhost:" + porta + "/api/tombolata/post");
            System.out.println("  - GET: http://localhost:" + porta + "/api/tombolata/get");
            System.out.println("  - Info: http://localhost:" + porta + "/");
            System.out.println();
            System.out.println();
            System.out.println("Premi Ctrl+C per fermare il server");
            System.out.println("==============================================");
            
        } catch (IOException e) {
            System.err.println("Errore nell'avvio del server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gestisce l'endpoint di benvenuto che fornisce informazioni sull'API
     * 
     * @param exchange l'oggetto HttpExchange per gestire la richiesta/risposta
     * @throws IOException in caso di errori durante la comunicazione
     */
    private static void gestisciBenvenuto(HttpExchange exchange) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("messaggio", "Benvenuti alla Tombolata!!!");
        info.put("versione", "1.0.0");
        info.put("tecnologia", "Java + GSON");
        
        Map<String, Object> endpoints = new LinkedHashMap<>();
        Map<String, String> api = new LinkedHashMap<>();
        api.put("POST", "/api/tombolata/post");
        api.put("GET", "/api/tombolata/get?tombolataId=UUID_OPZIONALE");
        endpoints.put("api", api);
        info.put("endpoints", endpoints);
        
        Map<String, String> azioni = new LinkedHashMap<>();
        azioni.put("CREA_TOMBOLATA", "Crea una nuova tombolata");
        azioni.put("REGISTRA_UTENTE", "Registra un giocatore o gestore");
        azioni.put("CREA_CARTELLA", "Assegna una cartella a un utente");
        azioni.put("ESTRAI_NUMERO", "Estrae il prossimo numero della tombolata");
        azioni.put("REGISTRA_VINCITA", "Registra una vincita");
        info.put("azioni_supportate", azioni);
        
        String jsonRisposta = gson.toJson(info);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = jsonRisposta.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}