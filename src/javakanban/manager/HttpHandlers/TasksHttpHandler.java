package javakanban.manager.HttpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.exceptions.InMemoryTaskManagerException;
import javakanban.exceptions.ManagerFileSaveException;
import javakanban.manager.TaskManager;
import javakanban.model.Task;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TasksHttpHandler extends BaseHttpHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager taskManager;

    public TasksHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        if (!paramsURI[1].equals("tasks")) {
            sendNotFound(exchange);
            return; // запрос не соответствует эндпоинту
        }
        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) { // Запрос всех объектов
                    String json = gson.toJson(taskManager.getTasks());
                    sendTextOK(exchange, json);
                    break; // Выход из GET
                }

                // Запрос по id
                if (paramsURI.length == 3 && getIdFromString(paramsURI[2]).isPresent()) {
                    Task task = taskManager.getTaskById(getIdFromString(paramsURI[2]).get());
                    if (task == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(task);
                        sendTextOK(exchange, json);
                    }
                    break; // Выход из GET
                }

                // Неверный id или URL запроса
                sendNotFound(exchange);
                break; // Выход из GET
            }
            case "POST": {
                if (paramsURI.length != 2) { // не удалось распознать команду
                    sendNotFound(exchange);
                    break; // Выход из GET
                }
                Task task, savedTask;
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (requestBody.isBlank()) {
                    sendNotAcceptable(exchange); // Отсутствует тело запроса
                    break; // Выход из POST по ошибке
                }
                // Десериализация
                try {
                    task = gson.fromJson(requestBody, Task.class);
                } catch (Exception e) {
                    sendNotAcceptable(exchange);
                    break; // Выход из POST по ошибке
                }

                if (task.getStatus() == null) { // Проверка на наличие статуса
                    sendNotAcceptable(exchange);
                    break; // Выход из POST по ошибке
                }

                if (task.getId() != null) { // Попытка обновить объект
                    try {
                        savedTask = taskManager.updateTask(task);
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendHasOverlaps(exchange);
                        break; // Выход из POST по ошибке
                    } catch (ManagerFileSaveException e) {
                        sendInternalError(exchange);
                        break; // Выход из POST по ошибке
                    }

                    if (savedTask != null) { // успешно
                        sendCreated(exchange,"{\"message\" : \"OK\"}");
                    } else {
                        sendNotFound(exchange); // Объект для обновления не найден
                    }
                    break; // Выход из POST
                }

                if (task.getId() == null) {  // Создаем новый объект
                    try {
                        savedTask = taskManager.addTask(task);
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendNotAcceptable(exchange);
                        break; // Выход из POST по ошибке
                    } catch (ManagerFileSaveException e) {
                        sendInternalError(exchange);
                        break; // Выход из POST по ошибке
                    }
                    sendCreated(exchange, gson.toJson(savedTask));
                    break; // Выход из POST
                }

                // Неверный id или URL запроса
                sendNotFound(exchange);
                break; // Выход из POST

            }
            case "DELETE": {
                if (paramsURI.length == 2) { // Удаление всех задач
                    try {
                        taskManager.delTasks();
                    } catch (ManagerFileSaveException e) {
                        sendInternalError(exchange);
                        break; // Выход по ошибке
                    }
                    sendTextOK(exchange, "{\"message\" : \"OK\"}");
                    break; // Выход из DELETE
                }

                // Удаляем конкретный объект
                if (paramsURI.length == 3
                        && getIdFromString(paramsURI[2]).isPresent()
                        && taskManager.getTaskById(getIdFromString(paramsURI[2]).get()) != null) {
                    try {
                        taskManager.delTaskById(getIdFromString(paramsURI[2]).get());
                    } catch (ManagerFileSaveException e) {
                        sendInternalError(exchange);
                        break; // Выход по ошибке
                    }
                    sendTextOK(exchange, "{\"message\" : \"OK\"}");
                    break; // Выход из DELETE
                }
                // Неверный id или URL запроса
                sendNotFound(exchange);
                break; // Выход из DELETE
            }
            default:
                // Неверный id или URL запроса
                sendNotFound(exchange);
                break; // Выход из DELETE
        }
    }
}
