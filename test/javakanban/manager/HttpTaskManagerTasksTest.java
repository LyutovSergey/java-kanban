package javakanban.manager;

import com.google.gson.Gson;
import javakanban.model.GsonBuilderForHTTP;
import javakanban.model.Status;
import javakanban.model.Task;
import javakanban.model.TypeTaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

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

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getTaskManager(TypeTaskManager.IN_MEMORY);
    // передаём его в качестве аргумента в конструктор javakanban.manager.HttpTaskServer
    Gson gson = (new GsonBuilderForHTTP()).getGson();
    DateTimeFormatter formatterForDataTaskAndCSV = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.delTasks();
        manager.delSubtasks();
        manager.delEpics();
        HttpTaskServer.start(manager);
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task =  new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-10 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));

        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }
}