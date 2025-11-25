package javakanban.manager.HttpHandlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler {

    protected Optional<Integer> getIdFromString(String string) {
        int id;
        try {
            id = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(id);
    }

    protected void sendResponse(HttpExchange exchange, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendTextOK(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"message\" : \"404 Not Found\"}", 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        sendNotAcceptable(exchange);
    }

    protected void sendNotAcceptable(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"message\" : \"406 Not Acceptable\"}", 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "{\"message\" : \"500 Internal Server Error\"}", 500);
    }
}