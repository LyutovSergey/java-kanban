package javakanban.manager;

import com.sun.net.httpserver.HttpServer;
import javakanban.manager.HttpHandlers.*;
import javakanban.model.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int port = 8080;
    protected static HttpServer httpServer;
    protected static  TaskManager taskManager;

    protected static void start(TaskManager taskManager) throws IOException {
        // настройка и запуск HTTP-сервера
        HttpTaskServer.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TasksHttpHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHttpHandler(taskManager));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));
        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }
    protected static void stop(){
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
       start(Managers.getTaskManager(TypeTaskManager.FILE_BACKED));
    }
}
