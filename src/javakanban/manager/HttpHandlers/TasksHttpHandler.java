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
        this.exchange = exchange;
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) { // Запрос всех объектов
                    String json = gson.toJson(taskManager.getTasks());
                    sendText(json);
                    break; // Выход из GET
                }

                // Запрос по id
                if (paramsURI.length == 3 && getIdFromString(paramsURI[2]).isPresent()) {
                    Task task = taskManager.getTaskById(getIdFromString(paramsURI[2]).get());
                    if (task == null) {
                        sendNotFound();
                    } else {
                        String json = gson.toJson(task);
                        sendText(json);
                    }
                    break; // Выход из GET
                }

                // Неверный id или URL запроса
                sendNotFound();
                break; // Выход из GET
            }
            case "POST": {
                Task task, savedTask;
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                // Десериализация
                try {
                    task = gson.fromJson(requestBody, Task.class);
                } catch (JsonSyntaxException e) {
                    sendNotAcceptable();
                    break; // Выход из POST по ошибке
                }

                if (task.getId() != null) { // Попытка обновить объект
                    try {
                        savedTask =taskManager.updateTask(task);
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendNotAcceptable();
                        break; // Выход из POST по ошибке
                    }
                    if (savedTask != null) { // успешно
                        sendDone();
                    } else {
                        sendNotFound(); // Объект для обновления не найден
                    }
                    break; // Выход из POST
                }

                if (task.getId() == null) {  // Создаем новый объект
                    try {
                        savedTask = taskManager.addTask(task);;
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendNotAcceptable();
                        break; // Выход из POST по ошибке
                    }
                    sendDone();
                    break; // Выход из POST
                }

                // Неверный id или URL запроса
                sendNotFound();
                break; // Выход из POST

            }
            case "DELETE": {
                if (paramsURI.length == 2) { // Удаление всех задач
                    taskManager.delTasks();
                    sendDone();
                    break; // Выход из DELETE
                }

                // Удаляем конкретный объект
                if (paramsURI.length == 3 && getIdFromString(paramsURI[2]).isPresent()) {
                    taskManager.delTaskById(getIdFromString(paramsURI[2]).get());
                    sendDone();
                    break; // Выход из DELETE
                }

                // Неверный id или URL запроса
                sendNotFound();
                break; // Выход из DELETE
            }
            default:
                // Неверный id или URL запроса
                sendNotFound();
                break; // Выход из DELETE

        }

    }

}
