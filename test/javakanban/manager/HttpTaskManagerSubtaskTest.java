package javakanban.manager;

import com.google.gson.Gson;
import javakanban.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerSubtaskTest {
    // создаём экземпляр InMemoryTaskManager
    private final TaskManager taskManager = Managers.getTaskManager(TypeTaskManager.IN_MEMORY);
    private Epic epic1, epic2;
    private final Gson gson = (new GsonBuilderForHTTP()).getGson();
    DateTimeFormatter formatterForDataTaskAndCSV = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HttpTaskManagerSubtaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.delTasks();
        taskManager.delSubtasks();
        taskManager.delEpics();
        HttpTaskServer.start(taskManager);
        epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        epic2 = new Epic("Эпик 2", "Описание Эпика 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-10 02:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
   //     taskManager.addSubtask(subtask1);
        String subtaskJson = gson.toJson(subtask1);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество субтасков");
        assertEquals("Подзадача 1 (Эпика 1)", taskManager.getSubtasks().get(0).getName(), "Некорректное имя субтаска");

        // Тест на добавление субтаска с некорректным эпик id 123
        subtask1 = new Subtask("Подзадача 2 (Эпика1123)", "Описание 1", Status.NEW, 123,
                LocalDateTime.parse("2011-11-11 11:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
        subtaskJson = gson.toJson(subtask1);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество субтасков");

        // Тест на добавление пересекающего по времени субтаска
        subtask1 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-10 02:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
        subtaskJson = gson.toJson(subtask1);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество субтасков");
    }

    @Test
    public void testDelSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-10 02:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-09 01:10", formatterForDataTaskAndCSV), Duration.ofMinutes(10));
        Subtask subtask3 = new Subtask("Подзадача 3 (Эпика 2)", "Описание 3", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        // Тест на удаление одной задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/"+subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(), "Ошибка при удалении подзадачи");
        assertEquals(2, taskManager.getSubtasks().size(), "Некорректное количество подзадач");

        // Тест на удаление несуществующей задачи
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks/123");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        assertEquals(2, taskManager.getSubtasks().size(), "Некорректное количество подзадач");

        // Тест на удаление всех задач
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks/");
         request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-10 02:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
        taskManager.addSubtask(subtask1);
        String json = "{\"epicId\" : 1,\"id\" : "
                + subtask1.getId()
                + ",\"name\" : \"Подзадача 1 Обновленная (Эпика 1)\",\"description\" : \"Описание 1\","
                + "\"status\" : \"NEW\",\"startTime\" : \"2026-11-10 02:00\",\"duration\" : 130}";
        Subtask subtask2 = gson.fromJson(json, Subtask.class);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество эпиков");
        assertEquals(subtask2.getName()+ subtask2.getDescription(), taskManager.getSubtaskById(subtask1.getId()).getName()
                + taskManager.getSubtaskById(subtask1.getId()).getDescription(), "Субтаска не обновилась");

        //Тест на обновление несуществующего эпика 123
        json = "{\"epicId\" : 1,\"id\" : "
                + "123"
                + ",\"name\" : \"Подзадача 1 Обновленная (Эпика 1)\",\"description\" : \"Описание 1\","
                + "\"status\" : \"NEW\",\"startTime\" : \"2026-11-10 02:00\",\"duration\" : 130}";
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Удалось обновить несуществующую подзадачу");
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-10 02:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-09 01:10", formatterForDataTaskAndCSV), Duration.ofMinutes(10));
        Subtask subtask3 = new Subtask("Подзадача 3 (Эпика 2)", "Описание 3", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        String json = gson.toJson(taskManager.getSubtasks());


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не соответствует эталонному");
        assertEquals(json, response.body(), "Список подзадач не соответствует эталонному");

        // Тест на получение задачи по id
        json = gson.toJson(taskManager.getSubtaskById(subtask2.getId()));
        url = URI.create("http://localhost:8080/subtasks/" + subtask2.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Не удалось получить субтаску по id");
        assertEquals(json, response.body(), "Ответ не соответствует ожидаемому");

        //Тест на get несуществующей задачи
        url = URI.create("http://localhost:8080/subtasks/" + 123);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Запрос несуществующего объекта отработал некорректно");
    }

}