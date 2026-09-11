package com.projet.api;

import com.projet.exception.CompteIntrouvableException;
import com.projet.exception.SoldeInsuffisantException;
import com.projet.exception.ValidationException;
import com.projet.model.Client;
import com.projet.model.compte.Compte;
import com.projet.model.compte.CompteCourant;
import com.projet.model.compte.CompteEpargne;
import com.projet.model.compte.Transaction;
import com.projet.service.BankService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ApiServer {

    private final BankService banque;

    public ApiServer(BankService banque) {
        this.banque = banque;
    }

    public void demarrer() throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress("localhost", 8080), 0
        );
        server.createContext("/api", new ApiHandler());
        server.createContext("/", new RootHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("API BankManager disponible sur http://localhost:8080");
    }

    private class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCors(exchange);
            send(exchange, 200,
                    "{\"message\":\"API BankManager active.\","
                    + "\"accounts\":\"/api/accounts\","
                    + "\"transfers\":\"/api/transfers\"}");
        }
    }

    private class ApiHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                send(exchange, 204, "");
                return;
            }

            try {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();

                if ("POST".equals(method) && "/api/login".equals(path)) {
                    authentifier(exchange, readBody(exchange));
                    return;
                }

                if ("POST".equals(method) && "/api/register".equals(path)) {
                    inscrire(exchange, readBody(exchange));
                    return;
                }

                if ("GET".equals(method) && "/api/accounts".equals(path)) {
                    send(exchange, 200,
                            comptesJson(banque.getComptes(clientId(exchange))));
                    return;
                }

                if ("GET".equals(method)
                        && path.matches("/api/accounts/[^/]+/transactions")) {
                    String numero = path.split("/")[3];
                        banque.verifierProprietaire(numero, clientId(exchange));
                    send(exchange, 200,
                            transactionsJson(banque.getHistorique(numero)));
                    return;
                }

                if ("POST".equals(method) && "/api/accounts".equals(path)) {
                    creerCompte(exchange, readBody(exchange), clientId(exchange));
                    return;
                }

                if ("POST".equals(method) && "/api/transfers".equals(path)) {
                    effectuerVirement(exchange, readBody(exchange), clientId(exchange));
                    return;
                }

                if ("POST".equals(method) && "/api/deposits".equals(path)) {
                    effectuerDepot(exchange, readBody(exchange), clientId(exchange));
                    return;
                }

                if ("POST".equals(method) && "/api/withdrawals".equals(path)) {
                    effectuerRetrait(exchange, readBody(exchange), clientId(exchange));
                    return;
                }

                send(exchange, 404, error("Route introuvable."));
            } catch (Exception e) {
                send(exchange, 400, error(e.getMessage()));
            }
        }
    }

    private void authentifier(HttpExchange exchange, String body)
            throws Exception {
        Map<String, String> fields = parseJson(body);
        Client client = banque.authentifier(
                required(fields, "email"),
                required(fields, "motDePasse")
        );
        send(exchange, 200, clientJson(client));
    }

    private void inscrire(HttpExchange exchange, String body)
            throws Exception {
        Map<String, String> fields = parseJson(body);
        Client client = new Client(
                System.currentTimeMillis(),
                required(fields, "nom"),
                required(fields, "prenom"),
                required(fields, "telephone"),
                required(fields, "email"),
                required(fields, "motDePasse")
        );
        String numero = "CM-" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 12).toUpperCase();
        Compte compte;
        if ("EPARGNE".equals(required(fields, "type"))) {
            compte = new CompteEpargne(numero, client, 0.0);
        } else {
            compte = new CompteCourant(numero, client);
        }
        banque.ajouterClient(client);
        banque.ajouterCompte(compte);
        send(exchange, 201, clientJson(client));
    }

    private void creerCompte(HttpExchange exchange, String body, long clientId)
            throws Exception {
        Map<String, String> fields = parseJson(body);
        Client client = banque.rechercherClient(clientId);

        String numero = "CM-" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 12).toUpperCase();
        String type = required(fields, "type");
        Compte compte;
        if ("COURANT".equals(type)) {
            compte = new CompteCourant(numero, client);
        } else if ("EPARGNE".equals(type)) {
            double taux = Double.parseDouble(fields.getOrDefault("taux", "0"));
            compte = new CompteEpargne(numero, client, taux);
        } else {
            throw new ValidationException("Type de compte invalide.");
        }

        banque.ajouterCompte(compte);
        send(exchange, 201, compteJson(compte));
    }

    private void effectuerVirement(HttpExchange exchange, String body, long clientId)
            throws Exception {
        Map<String, String> fields = parseJson(body);
        String source = required(fields, "source");
        String destination = required(fields, "destination");
        double montant = Double.parseDouble(required(fields, "montant"));
        banque.verifierProprietaire(source, clientId);
        banque.effectuerVirement(source, destination, montant);
        send(exchange, 200, "{\"message\":\"Transfert effectué avec succès.\"}");
    }

    private void effectuerDepot(HttpExchange exchange, String body, long clientId)
            throws Exception {
        Map<String, String> fields = parseJson(body);
        String compte = required(fields, "compte");
        double montant = Double.parseDouble(required(fields, "montant"));
        banque.verifierProprietaire(compte, clientId);
        banque.deposer(compte, montant);
        send(exchange, 200, "{\"message\":\"Dépôt effectué avec succès.\"}");
    }

    private void effectuerRetrait(HttpExchange exchange, String body, long clientId)
            throws Exception {
        Map<String, String> fields = parseJson(body);
        String compte = required(fields, "compte");
        double montant = Double.parseDouble(required(fields, "montant"));
        banque.verifierProprietaire(compte, clientId);
        banque.retirer(compte, montant);
        send(exchange, 200, "{\"message\":\"Retrait effectué avec succès.\"}");
    }

    private String comptesJson(List<Compte> comptes) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < comptes.size(); i++) {
            if (i > 0) json.append(',');
            json.append(compteJson(comptes.get(i)));
        }
        return json.append(']').toString();
    }

    private String compteJson(Compte compte) {
        Client client = compte.getClient();
        String type = compte instanceof CompteEpargne ? "EPARGNE" : "COURANT";
        return "{" +
                "\"id\":\"" + json(compte.getNumeroCompte()) + "\"," +
                "\"type\":\"" + type + "\"," +
                "\"balance\":" + compte.getSolde() + "," +
                "\"nom\":\"" + json(client.getNom()) + "\"," +
                "\"prenom\":\"" + json(client.getPrenom()) + "\"" +
                "}";
    }

    private String clientJson(Client client) {
        return "{" +
                "\"id\":" + client.getId() + "," +
                "\"nom\":\"" + json(client.getNom()) + "\"," +
                "\"prenom\":\"" + json(client.getPrenom()) + "\"," +
                "\"email\":\"" + json(client.getEmail()) + "\"" +
                "}";
    }

    private String transactionsJson(List<Transaction> transactions) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            if (i > 0) json.append(',');
            json.append("{\"type\":\"")
                    .append(transaction.getType())
                    .append("\",\"amount\":")
                    .append(transaction.getMontant())
                    .append(",\"description\":\"")
                    .append(json(transaction.getDescription()))
                    .append("\",\"date\":\"")
                    .append(transaction.getDate())
                    .append("\"}");
        }
        return json.append(']').toString();
    }

    private Map<String, String> parseJson(String body) {
        Map<String, String> values = new HashMap<String, String>();
        String content = body.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1);
        }
        for (String pair : content.split(",")) {
            String[] parts = pair.split(":", 2);
            if (parts.length == 2) {
                values.put(unquote(parts[0].trim()), unquote(parts[1].trim()));
            }
        }
        return values;
    }

    private String required(Map<String, String> fields, String name)
            throws ValidationException {
        String value = fields.get(name);
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Le champ " + name + " est obligatoire.");
        }
        return value;
    }

    private long clientId(HttpExchange exchange) throws ValidationException {
        String value = exchange.getRequestHeaders().getFirst("X-Client-Id");
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Connexion requise.");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ValidationException("Identifiant client invalide.");
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
    }

    private void send(HttpExchange exchange, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private String error(String message) {
        return "{\"error\":\"" + json(message == null ? "Erreur inconnue." : message) + "\"}";
    }

    private String json(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        return value;
    }
}
