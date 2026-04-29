import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class TombolaServer {
    
    public static void main(String[] args) {
        
        // Configurazione porta (default 8080)
        int porta = 8080;
        if (args.length > 0) {
            try {
                porta = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Porta non valida, uso porta default 8080");
            }
        }
        
        try {
            // Crea il server sulla porta specificata
            HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
            
            // Registra gli handler per gli endpoint
            server.createContext("/api/calcola/post", new PostHandler());
            server.createContext("/api/calcola/get", new GetHandler());
            
            // Endpoint di benvenuto
            server.createContext("/", exchange -> {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                
                Map<String, Object> info = new HashMap<>();
                info.put("messaggio", "Benvenuto alla Calcolatrice REST API");
                info.put("versione", "2.0.0");
                info.put("tecnologia", "Java + GSON");
                
                Map<String, String> endpoints = new HashMap<>();
                endpoints.put("POST", "/api/calcola/post");
                endpoints.put("GET", "/api/calcola/get?operando1=X&operando2=Y&operatore=OP");
                info.put("endpoints", endpoints);
                
                Map<String, String> operatori = new HashMap<>();
                operatori.put("somma", "SOMMA o +");
                operatori.put("sottrazione", "SOTTRAZIONE o -");
                operatori.put("moltiplicazione", "MOLTIPLICAZIONE o * o X");
                operatori.put("divisione", "DIVISIONE o /");
                info.put("operatori_supportati", operatori);
                
                String jsonRisposta = gson.toJson(info);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                byte[] bytes = jsonRisposta.getBytes();
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.getResponseBody().close();
            });
            
            // Avvia il server
            server.setExecutor(null); // Usa il default executor
            server.start();
            
            // Messaggi di conferma
            System.out.println("==============================================");
            System.out.println("  Server REST con GSON avviato!");
            System.out.println("==============================================");
            System.out.println("Porta: " + porta);
            System.out.println();
            System.out.println("Endpoint disponibili:");
            System.out.println("  - POST: http://localhost:" + porta + "/api/calcola/post");
            System.out.println("  - GET:  http://localhost:" + porta + "/api/calcola/get");
            System.out.println("  - Info: http://localhost:" + porta + "/");
            System.out.println();
            System.out.println("Operatori supportati:");
            System.out.println("  SOMMA, SOTTRAZIONE, MOLTIPLICAZIONE, DIVISIONE");
            System.out.println();
            System.out.println("Premi Ctrl+C per fermare il server");
            System.out.println("==============================================");
            
        } catch (IOException e) {
            System.err.println("Errore nell'avvio del server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}