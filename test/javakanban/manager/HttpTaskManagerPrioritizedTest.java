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

public class HttpTaskManagerPrioritizedTest {
    // создаём экземпляр InMemoryTaskManager
    private final TaskManager taskManager = Managers.getTaskManager(TypeTaskManager.IN_MEMORY);
    // передаём его в качестве аргумента в конструктор javakanban.manager.HttpTaskServer
    private final Gson gson = (new GsonBuilderForHTTP()).getGson();
    private Subtask subtask1, subtask2, subtask3;
    private Epic epic1, epic2;
    private Task task1, task2, task3;
    private final HttpTaskServer httpTaskServer= new HttpTaskServer(8080, taskManager);
    DateTimeFormatter formatterForDataTaskAndCSV = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HttpTaskManagerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.delTasks();
        taskManager.delSubtasks();
        taskManager.delEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    private void setVariable() {
        epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        epic2 = new Epic("Эпик 2", "Описание Эпика 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-10 02:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
        subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId(),
                LocalDateTime.parse("2025-11-09 01:10", formatterForDataTaskAndCSV), Duration.ofMinutes(10));
        subtask3 = new Subtask("Подзадача 3 (Эпика 2)", "Описание 3", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.parse("2025-11-11 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-12 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        task3 = new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-13 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
    }

    @Test
    public void testPrioritized() throws IOException, InterruptedException {
        // Тест пустых приоритетов
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не соответствует эталонному");
        assertEquals("[]", response.body(), "Список приоритетов не пустой");

        // Тест приоритетов
        setVariable();
        String json = gson.toJson(taskManager.getPrioritizedTasks());
        url = URI.create("http://localhost:8080/prioritized/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не соответствует эталонному");
        assertEquals(json, response.body(), "Список приоритетов не совпадает с ожидаемым");

        // Тест приоритетов после удаление объекта
        taskManager.delEpics();
        json = gson.toJson(taskManager.getPrioritizedTasks());
        url = URI.create("http://localhost:8080/prioritized/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа не соответствует эталонному");
        assertEquals(json, response.body(), "Список приоритетов не совпадает с ожидаемым");
    }
}