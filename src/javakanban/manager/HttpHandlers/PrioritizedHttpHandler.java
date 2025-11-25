package javakanban.manager.HttpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.manager.TaskManager;

import java.io.IOException;


public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager taskManager;

    public PrioritizedHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        if (!paramsURI[1].equals("prioritized")) {
            sendNotFound(exchange);
            return; // запрос не соответствует эндпоинту
        }
        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) { // Запрос всех объектов
                    String json = gson.toJson(taskManager.getPrioritizedTasks());
                    sendTextOK(exchange, json);
                    break; // Выход из GET
                }

                // Неверный URL запроса
                sendNotFound(exchange);
                break; // Выход из GET
            }
            default:
                // Неверный URL запроса
                sendNotFound(exchange);
                break; // Выход из DELETE
        }
    }
}
