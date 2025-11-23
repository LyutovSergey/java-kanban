package javakanban.manager.HttpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.exceptions.InMemoryTaskManagerException;
import javakanban.manager.TaskManager;
import javakanban.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class HistoryHttpHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) { // Запрос всех объектов
                    String json = gson.toJson(taskManager.getHistory());
                    sendText(json);
                    break; // Выход из GET
                }

                // Неверный URL запроса
                sendNotFound();
                break; // Выход из GET
            }
            default:
                // Неверный URL запроса
                sendNotFound();
                break; // Выход из DELETE
        }
    }
}
