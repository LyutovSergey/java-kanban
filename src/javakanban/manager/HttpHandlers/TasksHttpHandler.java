package javakanban.manager.HttpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.manager.TaskManager;

import java.io.IOException;
import java.lang.reflect.Array;

public class TasksHttpHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");
        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2){
                    String json = gson.toJson(taskManager.getTasks());
                    sendText(exchange, json);
                    return;
                }

                break;
            }
            case "POST": {

                break;
            }
            case "DELETE": {

                break;
            }
            default:

        }

    }

}
