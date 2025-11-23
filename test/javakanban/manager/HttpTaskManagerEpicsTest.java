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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest {

    // создаём экземпляр InMemoryTaskManager
    private final TaskManager taskManager = Managers.getTaskManager(TypeTaskManager.IN_MEMORY);
    // передаём его в качестве аргумента в конструктор javakanban.manager.HttpTaskServer
    private final Gson gson = (new GsonBuilderForHTTP()).getGson();
    private final DateTimeFormatter formatterForDataTaskAndCSV = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HttpTaskManagerEpicsTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.delTasks();
        taskManager.delSubtasks();
        taskManager.delEpics();
        HttpTaskServer.start(taskManager);
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        String epicJson = gson.toJson(epic1);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпика");
        assertEquals("Эпик 1", taskManager.getEpics().get(0).getName(), "Некорректное имя эпика");

        // Тест на добавление эпика с некоректным статусом
        epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        epic1.setStatus(Status.DONE);
        epicJson = gson.toJson(epic1);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");
    }

    @Test
    public void testDelEpics() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 =  new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.parse("2025-11-11 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        Task task2 =  new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-12 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        Task task3 =  new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-13 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        // Тест на удаление одной задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/"+task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(2, taskManager.getTasks().size(), "Некорректное количество задач");

        // Тест на удаление несуществующей задачи
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/123");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        assertEquals(2, taskManager.getTasks().size(), "Некорректное количество задач");

        // Тест на удаление всех задач
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/");
         request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasks().size(), "Некорректное количество задач");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task1 =  new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.parse("2025-11-11 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));

        taskManager.addTask(task1);
        String taskJson = "{\"id\": " + task1.getId() + ",\"name\": \"Задача 1 обновленная\",\"description\": \"Описание задачи 1 Обновлённая\","
                + "\"status\": \"DONE\",\"startTime\": \"2026-11-11 06:00\",\"duration\": 30}";
        Task task= gson.fromJson(taskJson, Task.class);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals(task.toString(), taskManager.getTaskById(task1.getId()).toString(), "Задача не обновилась");

        //Тест на обновление несуществующей задачи 123
        String taskJson2 = "{\"id\": 123,\"name\": \"Задача 1 обновленная\",\"description\": \"Описание задачи 1 Обновлённая\","
                + "\"status\": \"DONE\",\"startTime\": \"2026-11-11 06:00\",\"duration\": 30}";
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");

    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task1 =  new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.parse("2025-11-11 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        Task task2 =  new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-12 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        Task task3 =  new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-13 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        String jsonTasks = gson.toJson(taskManager.getTasks());

        // Тест на получение всех задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(jsonTasks, response.body());

        // Тест на получение задачи по id
        String jsonTask2 = gson.toJson(taskManager.getTaskById(task2.getId()));
        url = URI.create("http://localhost:8080/tasks/" + task2.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(jsonTask2, response.body());

        //Тест на get несуществующей задачи
        url = URI.create("http://localhost:8080/tasks/" + 123);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

}