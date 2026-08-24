package hw07.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hw07.core.model.Client;
import hw07.registration.RegistrationService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController implements HttpHandler {

    private final RegistrationService service;

    public ClientController(RegistrationService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try {
            if ("POST".equals(exchange.getRequestMethod()) && path.equals("/api/clients")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                send(exchange, 201,
                        toJson(service.register(jsonField(body, "name"), jsonField(body, "email"))));
            } else if ("GET".equals(exchange.getRequestMethod()) && path.equals("/api/clients")) {
                send(exchange, 200, toJson(service.findAll()));
            } else {
                send(exchange, 404, "{\"error\":\"not found\"}");
            }
        } catch (IllegalArgumentException e) {
            send(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String toJson(List<Client> clients) {
        StringBuilder json = new StringBuilder("[");
        for (Client client : clients) {
            if (json.length() > 1) {
                json.append(',');
            }
            json.append(toJson(client));
        }
        return json.append(']').toString();
    }

    private String toJson(Client client) {
        return "{\"id\":" + client.id()
                + ",\"name\":\"" + client.name() + "\""
                + ",\"email\":\"" + client.email() + "\""
                + ",\"cardNumber\":\"" + client.cardNumber() + "\""
                + ",\"discountPercent\":" + client.discountPercent()
                + "}";
    }

    private void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String jsonField(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}
