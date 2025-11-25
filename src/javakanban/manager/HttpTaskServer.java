package javakanban.manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import javakanban.manager.HttpHandlers.*;
import javakanban.model.*;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final int port;
    protected HttpServer httpServer;
    protected TaskManager taskManager;

    public HttpTaskServer(int port, TaskManager taskManager) {
        this.port = port;
        this.taskManager = taskManager;
    }

    protected void start() throws IOException {
        Gson gson = (new GsonBuilderForHTTP()).getGson();
        // настройка и запуск HTTP-сервера
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TasksHttpHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicsHttpHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager, gson));
        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    protected void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getTaskManager(TypeTaskManager.FILE_BACKED);
        HttpTaskServer server = new HttpTaskServer(8080, taskManager);
        server.start();
    }
}
