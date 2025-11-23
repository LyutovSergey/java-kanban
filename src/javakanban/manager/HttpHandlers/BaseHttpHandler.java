package javakanban.manager.HttpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import javakanban.manager.TaskManager;
import javakanban.model.GsonBuilderForHTTP;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler {

    protected static Gson gson;
    protected static TaskManager taskManager;
    protected HttpExchange exchange;

    public BaseHttpHandler(TaskManager taskManager) {
        BaseHttpHandler.taskManager = taskManager;
        GsonBuilderForHTTP jsonBuilderForHTTP = new GsonBuilderForHTTP();
        gson =  jsonBuilderForHTTP.getGson();
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

    protected void sendCreated() throws IOException {
        sendResponse("" , 201);
    }

    protected void sendOk() throws IOException {
        sendResponse("" , 200);
    }


    protected void sendNotFound() throws IOException {
        sendResponse( "404 Not Found" , 404);
    }
    protected void sendHasOverlaps() throws IOException {
        sendNotAcceptable();
    }

    protected void sendNotAcceptable() throws IOException {
        sendResponse("406 Not Acceptable" , 406);
    }



    protected void sendNInternalError() throws IOException {
        sendResponse("500 Internal Server Error" , 500);
    }


}