package serverrest.service;

import serverrest.classes.Cartella;
import serverrest.classes.Cella;
import serverrest.classes.Estrazione;
import serverrest.classes.Tombolata;
import serverrest.classes.Utente;
import serverrest.classes.Vincita;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class TombolaService {

    private static final Map<UUID, Tombolata> TOMBOlATE = new LinkedHashMap<>();
    private static final Map<UUID, Utente> UTENTI = new LinkedHashMap<>();
    private static final Map<UUID, Cartella> CARTELLE = new LinkedHashMap<>();
    private static final Map<UUID, List<Cella>> CELLE_PER_CARTELLA = new LinkedHashMap<>();
    private static final Map<UUID, List<Estrazione>> ESTRAZIONI_PER_TOMBOLATA = new LinkedHashMap<>();
    private static final Map<UUID, List<Vincita>> VINCITE_PER_TOMBOLATA = new LinkedHashMap<>();
    private static final Map<UUID, Set<Integer>> NUMERI_ESTRATTI = new LinkedHashMap<>();
    private static final Random RANDOM = new Random();

    private TombolaService() {
    }

    public static synchronized Tombolata creaTombolata(String nome, String dataFineAssegnazioneCartelle) {
        Tombolata tombolata = new Tombolata();
        tombolata.setId(UUID.randomUUID());
        tombolata.setNome(nome);
        tombolata.setStato("creata");
        tombolata.setDataFineAssegnazioneCartelle(dataFineAssegnazioneCartelle);

        TOMBOlATE.put(tombolata.getId(), tombolata);
        ESTRAZIONI_PER_TOMBOLATA.put(tombolata.getId(), new ArrayList<>());
        VINCITE_PER_TOMBOLATA.put(tombolata.getId(), new ArrayList<>());
        NUMERI_ESTRATTI.put(tombolata.getId(), new LinkedHashSet<>());

        return tombolata;
    }

    public static synchronized Utente registraUtente(String email, String nickname, String ruolo) {
        Utente utente = new Utente();
        utente.setId(UUID.randomUUID());
        utente.setEmail(email);
        utente.setNickname(nickname);
        utente.setRuolo(ruolo == null || ruolo.trim().isEmpty() ? "giocatore" : ruolo.trim());

        UTENTI.put(utente.getId(), utente);
        return utente;
    }

    public static synchronized Map<String, Object> creaCartella(UUID utenteId, UUID tombolataId) {
        ensureUtenteEsiste(utenteId);
        Tombolata tombolata = ensureTombolataEsiste(tombolataId);

        Cartella cartella = new Cartella();
        cartella.setId(UUID.randomUUID());
        cartella.setUtenteId(utenteId);
        cartella.setTombolataId(tombolataId);

        List<Cella> celle = generaCelleCartella(cartella.getId());

        CARTELLE.put(cartella.getId(), cartella);
        CELLE_PER_CARTELLA.put(cartella.getId(), celle);

        if ("creata".equalsIgnoreCase(tombolata.getStato())) {
            tombolata.setStato("aperta");
        }

        Map<String, Object> risposta = new LinkedHashMap<>();
        risposta.put("cartella", cartella);
        risposta.put("celle", celle);
        return risposta;
    }

    public static synchronized Estrazione effettuaEstrazione(UUID tombolataId) {
        Tombolata tombolata = ensureTombolataEsiste(tombolataId);
        Set<Integer> numeriEstratti = NUMERI_ESTRATTI.get(tombolataId);

        if (numeriEstratti.size() >= 90) {
            tombolata.setStato("terminata");
            throw new IllegalArgumentException("La tombolata ha già estratto tutti i numeri disponibili");
        }

        int numero;
        do {
            numero = RANDOM.nextInt(90) + 1;
        } while (numeriEstratti.contains(numero));

        numeriEstratti.add(numero);

        Estrazione estrazione = new Estrazione();
        estrazione.setId(UUID.randomUUID());
        estrazione.setTombolataId(tombolataId);
        estrazione.setNumero(numero);
        estrazione.setTimestamp(Instant.now().toString());

        ESTRAZIONI_PER_TOMBOLATA.get(tombolataId).add(estrazione);
        tombolata.setStato("attiva");

        if (numeriEstratti.size() == 90) {
            tombolata.setStato("terminata");
        }

        return estrazione;
    }

    public static synchronized Vincita registraVincita(UUID tombolataId, UUID utenteId, String tipo) {
        ensureTombolataEsiste(tombolataId);
        ensureUtenteEsiste(utenteId);

        Vincita vincita = new Vincita();
        vincita.setId(UUID.randomUUID());
        vincita.setTombolataId(tombolataId);
        vincita.setUtenteId(utenteId);
        vincita.setTipo(tipo);
        vincita.setTimestamp(Instant.now().toString());

        VINCITE_PER_TOMBOLATA.get(tombolataId).add(vincita);
        return vincita;
    }

    public static synchronized Map<String, Object> statoTombolata(UUID tombolataId) {
        Tombolata tombolata = ensureTombolataEsiste(tombolataId);

        Map<String, Object> stato = new LinkedHashMap<>();
        stato.put("tombolata", tombolata);
        stato.put("cartelle", cartellePerTombolata(tombolataId));
        stato.put("celle", cellePerTombolata(tombolataId));
        stato.put("estrazioni", new ArrayList<>(ESTRAZIONI_PER_TOMBOLATA.get(tombolataId)));
        stato.put("vincite", new ArrayList<>(VINCITE_PER_TOMBOLATA.get(tombolataId)));
        stato.put("numeriEstratti", new ArrayList<>(NUMERI_ESTRATTI.get(tombolataId)));
        return stato;
    }

    public static synchronized Map<String, Object> statoCompleto() {
        Map<String, Object> stato = new LinkedHashMap<>();
        stato.put("tombolate", new ArrayList<>(TOMBOlATE.values()));
        stato.put("utenti", new ArrayList<>(UTENTI.values()));
        stato.put("cartelle", new ArrayList<>(CARTELLE.values()));
        stato.put("estrazioniPerTombolata", ESTRAZIONI_PER_TOMBOLATA);
        stato.put("vincitePerTombolata", VINCITE_PER_TOMBOLATA);
        return stato;
    }

    private static Tombolata ensureTombolataEsiste(UUID tombolataId) {
        Tombolata tombolata = TOMBOlATE.get(tombolataId);
        if (tombolata == null) {
            throw new IllegalArgumentException("Tombolata non trovata: " + tombolataId);
        }
        return tombolata;
    }

    private static void ensureUtenteEsiste(UUID utenteId) {
        if (!UTENTI.containsKey(utenteId)) {
            throw new IllegalArgumentException("Utente non trovato: " + utenteId);
        }
    }

    private static List<Cella> generaCelleCartella(UUID cartellaId) {
        List<Cella> celle = new ArrayList<>();
        Set<Integer> numeriUsati = new LinkedHashSet<>();

        for (int riga = 1; riga <= 3; riga++) {
            Set<Integer> colonneSelezionate = new LinkedHashSet<>();
            while (colonneSelezionate.size() < 5) {
                colonneSelezionate.add(RANDOM.nextInt(9) + 1);
            }

            for (Integer colonna : colonneSelezionate) {
                int numero = generaNumeroPerColonna(colonna, numeriUsati);

                Cella cella = new Cella();
                cella.setId(UUID.randomUUID());
                cella.setCartellaId(cartellaId);
                cella.setRiga(riga);
                cella.setColonna(colonna);
                cella.setValore(numero);

                celle.add(cella);
            }
        }

        return celle;
    }

    private static int generaNumeroPerColonna(int colonna, Set<Integer> numeriUsati) {
        int minimo = (colonna - 1) * 10 + 1;
        int massimo = colonna * 10;
        if (colonna == 9) {
            massimo = 90;
        }

        int numero;
        do {
            numero = RANDOM.nextInt(massimo - minimo + 1) + minimo;
        } while (numeriUsati.contains(numero));

        numeriUsati.add(numero);
        return numero;
    }

    private static List<Cartella> cartellePerTombolata(UUID tombolataId) {
        List<Cartella> cartelle = new ArrayList<>();
        for (Cartella cartella : CARTELLE.values()) {
            if (tombolataId.equals(cartella.getTombolataId())) {
                cartelle.add(cartella);
            }
        }
        return cartelle;
    }

    private static List<Cella> cellePerTombolata(UUID tombolataId) {
        List<Cella> celle = new ArrayList<>();
        for (Cartella cartella : CARTELLE.values()) {
            if (tombolataId.equals(cartella.getTombolataId())) {
                List<Cella> celleCartella = CELLE_PER_CARTELLA.get(cartella.getId());
                if (celleCartella != null) {
                    celle.addAll(celleCartella);
                }
            }
        }
        return celle;
    }
}