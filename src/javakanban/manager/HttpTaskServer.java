package javakanban.manager;

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
        // настройка и запуск HTTP-сервера
        HttpTaskServer.taskManager = this.taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TasksHttpHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHttpHandler(taskManager));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));
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
