package javakanban.manager.HttpHandlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.exceptions.InMemoryTaskManagerException;
import javakanban.manager.TaskManager;
import javakanban.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHttpHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) {
                    String json = gson.toJson(taskManager.getTasks());
                    sendText(exchange, json);
                    break;
                } else if (paramsURI.length == 3 && getIdFromString(exchange, paramsURI[2]).isPresent()) {
                    Task task = taskManager.getTaskById(getIdFromString(exchange, paramsURI[2]).get());

                    if (task == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(task);
                        sendText(exchange, json);
                    }
                }
                break;
            }
            case "POST": {
                Task task, savedTask, tepmTAsk;
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
               try {
                    task = gson.fromJson(requestBody, Task.class);
                } catch (JsonSyntaxException e) {
                    sendNotAcceptable(exchange);
                    break;
                }

                if (task.getId() != null) { // попытка обновить
                    try {
                        savedTask =taskManager.updateTask(task);
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendNotAcceptable(exchange);
                        break;
                    }
                    if (savedTask != null) { // успешно
                        sendDone(exchange);
                    } else {
                        sendNotFound(exchange); // не найдено
                    }

                } else { // Добавляем новое

                    try {
                        savedTask = taskManager.addTask(task);;
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendNotAcceptable(exchange);
                        break;
                    }
                    sendDone(exchange);
                }
                break;
            }
            case "DELETE": {

                break;
            }
            default:

        }

    }

}
