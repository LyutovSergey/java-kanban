package javakanban.manager.HttpHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import javakanban.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public abstract class BaseHttpHandler {

    protected static Gson gson;
    protected static TaskManager taskManager;
    protected HttpExchange exchange;

    public BaseHttpHandler(TaskManager taskManager) {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        BaseHttpHandler.taskManager = taskManager;
    }

    protected Optional<Integer> getIdFromString( String string) throws IOException {
        int id;
        try {
            id = Integer.parseInt(string);
        } catch (NumberFormatException e) {
           // sendNInternalError();
            return Optional.empty();
        }
        return Optional.of(id);
    }

    protected void sendResponse(String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendText(String text) throws IOException {
        sendResponse( text , 200);
    }

    protected void sendDone() throws IOException {
        sendResponse("" , 201);
    }

    protected void sendNotFound() throws IOException {
        sendResponse( "404 Not Found" , 404);
    }

    protected void sendNotAcceptable() throws IOException {
        sendResponse("406 Not Acceptable" , 406);
    }


    protected void sendNInternalError() throws IOException {
        sendResponse("500 Internal Server Error" , 500);
    }


    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {

            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            if ("null".equals(value)) {
                return null;
            } else {
                return LocalDateTime.parse(value, formatter);
            }
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {

            if (duration == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(duration.toMinutes());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            if ("null".equals(value)) {
                return null;
            } else {
                //return Duration.ofMinutes(jsonReader.nextLong());
                long minutes = Long.parseLong(value);
                return Duration.ofMinutes(minutes);

            }
        }
    }
}