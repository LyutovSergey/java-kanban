package javakanban.manager.HttpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.manager.TaskManager;

import java.io.IOException;


public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) { // Запрос всех объектов
                    String json = gson.toJson(taskManager.getPrioritizedTasks());
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
