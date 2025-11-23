package javakanban.manager.HttpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.exceptions.InMemoryTaskManagerException;
import javakanban.manager.TaskManager;
import javakanban.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class SubtasksHttpHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) { // Запрос всех объектов
                    String json = gson.toJson(taskManager.getSubtasks());
                    sendText(json);
                    break; // Выход из GET
                }

                // Запрос по id
                if (paramsURI.length == 3 && getIdFromString(paramsURI[2]).isPresent()) {
                    Subtask subtask = taskManager.getSubtaskById(getIdFromString(paramsURI[2]).get());
                    if (subtask == null) {
                        sendNotFound();
                    } else {
                        String json = gson.toJson(subtask);
                        sendText(json);
                    }
                    break; // Выход из GET
                }

                // Неверный id или URL запроса
                sendNotFound();
                break; // Выход из GET
            }
            case "POST": {
                if (paramsURI.length != 2) { // не удалось распознать команду
                    sendNotFound();
                    break; // Выход из GET
                }
                Subtask subtask, savedSubtask;
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                // Десериализация
                try {
                    subtask = gson.fromJson(requestBody, Subtask.class);
                } catch (Exception e) {
                    sendNotAcceptable();
                    break; // Выход из POST по ошибке
                }

                if (subtask.getStatus() == null) { // Проверка на наличие статуса
                    sendNotAcceptable();
                    break; // Выход из POST по ошибке
                }


                if (subtask.getId() != null) { // Попытка обновить объект
                    try {
                        savedSubtask = taskManager.updateSubtask(subtask);
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendHasOverlaps();
                        break; // Выход из POST по ошибке
                    }
                    if (savedSubtask != null) { // успешно
                        sendCreated();
                    } else {
                        sendNotFound(); // Объект для обновления не найден
                    }
                    break; // Выход из POST
                }

                if (subtask.getId() == null) {  // Создаем новый объект
                    try {
                        savedSubtask = taskManager.addSubtask(subtask);
                    } catch (InMemoryTaskManagerException e) { //ошибка
                        sendNotAcceptable();
                        break; // Выход из POST по ошибке
                    }
                    if (savedSubtask != null) { // успешно
                        sendCreated();
                    } else {
                        sendNotAcceptable(); // Ошибка при создании
                    }
                    break; // Выход из POST
                }

                // Неверный id или URL запроса
                sendNotFound();
                break; // Выход из POST

            }
            case "DELETE": {
                if (paramsURI.length == 2) { // Удаление всех задач
                    taskManager.delSubtasks();
                    sendOk();
                    break; // Выход из DELETE
                }

                // Удаляем конкретный объект
                if (paramsURI.length == 3
                        && getIdFromString(paramsURI[2]).isPresent()
                        && taskManager.getSubtaskById(getIdFromString(paramsURI[2]).get()) != null) {
                    taskManager.delSubtaskById(getIdFromString(paramsURI[2]).get());
                    sendOk();
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
