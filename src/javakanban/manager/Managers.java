package javakanban.manager;

import javakanban.model.TypeTaskManager;



public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getTaskManager(TypeTaskManager type) {
        return switch (type) {
            case IN_MEMORY -> new InMemoryTaskManager();
            case FILE_BACKED -> new FileBackedTaskManager();
        };
    }

}
