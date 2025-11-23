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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerEpicsTest {
    // создаём экземпляр InMemoryTaskManager
    private final TaskManager taskManager = Managers.getTaskManager(TypeTaskManager.IN_MEMORY);
    // передаём его в качестве аргумента в конструктор javakanban.manager.HttpTaskServer
    private final Gson gson = (new GsonBuilderForHTTP()).getGson();

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
        Epic epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание Эпика 2");
        Epic epic3 = new Epic("Эпик 3", "Описание Эпика 3");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        // Тест на удаление одной задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/"+epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(2, taskManager.getEpics().size(), "Некорректное количество эпиков");

        // Тест на удаление несуществующей задачи
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics/123");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
        assertEquals(2, taskManager.getEpics().size(), "Некорректное количество эпиков");

        // Тест на удаление всех задач
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics/");
         request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpics().size(), "Некорректное количество эпиков");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        taskManager.addEpic(epic1);
        String epicJson = "{\"id\": " + epic1.getId() + ",\"name\": \"Эпик 1 обновленный\",\"description\": \"Описание зпик 1 обновленный\"}";
        Epic epic2 = gson.fromJson(epicJson, Epic.class);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");
        assertEquals(epic2.getName()+ epic2.getDescription(), taskManager.getEpicById(epic1.getId()).getName()
                + taskManager.getEpicById(epic1.getId()).getDescription(), "Эпик не обновилась");

        //Тест на обновление несуществующего эпика 123
        String epicJson2 = "{\"id\": " + 123 + ",\"name\": \"Эпик 1 обновленный\",\"description\": \"Описание зпик 1 обновленный\"}";
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Удалось обновить несуществующий эпик");
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");

    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание Эпика 2");
        Epic epic3 = new Epic("Эпик 3", "Описание Эпика 3");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        String jsonEpics = gson.toJson(taskManager.getEpics());


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Код ответа не соответствует эталонному");
        assertEquals(jsonEpics, response.body(),"Список эпиков не соответствует эталонному");

        // Тест на получение задачи по id
        String jsonEpic = gson.toJson(taskManager.getEpicById(epic2.getId()));
        url = URI.create("http://localhost:8080/epics/" + epic2.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Не удалось получить эпик по id");
        assertEquals(jsonEpic, response.body(), "Ответ не соответствует ожидаемому");

        //Тест на get несуществующей задачи
        url = URI.create("http://localhost:8080/epics/" + 123);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Запрос несуществующего объекта отработал некорректно");
    }

}