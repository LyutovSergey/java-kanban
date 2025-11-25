package javakanban.manager.HttpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.exceptions.ManagerFileSaveException;
import javakanban.manager.TaskManager;
import javakanban.model.Epic;
import javakanban.model.Status;
import javakanban.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class EpicsHttpHandler extends BaseHttpHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager taskManager;

    public EpicsHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected boolean isCorrectDataEpic(Epic epic) { // Разрешено только id, name и description
        return epic.getEndTime().isEmpty()
                && epic.getStartTime().isEmpty()
                && epic.getDuration().isEmpty()
                && (epic.getStatus() == null || epic.getStatus() == Status.NEW)
                && epic.getSubtasksId().isEmpty();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] paramsURI =  exchange.getRequestURI().getPath().split("/");

        if (!paramsURI[1].equals("epics")) {
            sendNotFound(exchange);
            return; // запрос не соответствует эндпоинту
        }
        switch (exchange.getRequestMethod()) {
            case "GET": {
                if (paramsURI.length == 2) { // Запрос всех объектов
                    String json = gson.toJson(taskManager.getEpics());
                    sendTextOK(exchange, json);
                    break; // Выход из GET
                }

                // Запрос по id
                if (paramsURI.length == 3 && getIdFromString(paramsURI[2]).isPresent()) {
                    Epic epic = taskManager.getEpicById(getIdFromString(paramsURI[2]).get());
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(epic);
                        sendTextOK(exchange, json);
                    }
                    break; // Выход из GET
                }

                // Запрос подзадач эпика
                if (paramsURI.length == 4
                        && getIdFromString(paramsURI[2]).isPresent()
                        && paramsURI[3].equals("subtasks")) {

                    List<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpicById(getIdFromString(paramsURI[2]).get());
                    if (subtasksOfEpic == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(subtasksOfEpic);
                        sendTextOK(exchange, json);
                    }
                    break; // Выход из GET
                }

                // Неверный id или URL запроса
                sendNotFound(exchange);
                break; // Выход из GET
            }
            case "POST": { // Разрешено только id, name и description
                if (paramsURI.length != 2) { // не удалось распознать команду
                    sendNotFound(exchange);
                    break; // Выход из GET
                }
                Epic epic, savedEpic;
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (requestBody.isBlank()) {
                    sendNotAcceptable(exchange); // Отсутствует тело запроса
                    break; // Выход из POST по ошибке
                }
                // Десериализация
                try {
                    epic = gson.fromJson(requestBody, Epic.class);
                } catch (Exception e) {
                    sendNotAcceptable(exchange);
                    break; // Выход из POST по ошибке
                }

                if (!isCorrectDataEpic(epic)) { // Разрешено только id, name и description
                    sendNotAcceptable(exchange);
                    break; // Выход из POST по ошибке
                }

                if (epic.getId() != null) { // Попытка обновить объект
                    try {
                        savedEpic = taskManager.updateEpic(epic);
                    } catch (ManagerFileSaveException e) {
                        sendInternalError(exchange);
                        break; // Выход из POST по ошибке
                    }
                    if (savedEpic != null) { // успешно
                        sendCreated(exchange, gson.toJson(savedEpic));
                    } else {
                        sendNotFound(exchange); // Объект для обновления не найден
                    }
                    break; // Выход из POST
                }

                if (epic.getId() == null) {  // Создаем новый объект
                    try {
                        savedEpic = taskManager.addEpic(epic);
                    } catch (ManagerFileSaveException e) {
                        sendInternalError(exchange);
                        break; // Выход из POST по ошибке
                    }
                    if (savedEpic != null) { // успешно
                        sendCreated(exchange, gson.toJson(savedEpic));
                    } else {
                        sendNotAcceptable(exchange); // Ошибка при создании
                    }
                    break; // Выход из POST
                }

                // Неверный id или URL запроса
                sendNotFound(exchange);
                break; // Выход из POST

            }
            case "DELETE": {
                if (paramsURI.length == 2) { // Удаление всех задач
                    try {
                        taskManager.delEpics();
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
                        && taskManager.getEpicById(getIdFromString(paramsURI[2]).get()) != null) {
                    try {
                        taskManager.delEpicById(getIdFromString(paramsURI[2]).get());
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
