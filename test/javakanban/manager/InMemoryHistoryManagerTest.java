package javakanban.manager;

import javakanban.model.Epic;
import javakanban.model.Status;
import javakanban.model.Subtask;
import javakanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void genDataBeforeTest() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Задача", "Описание задачи 1", Status.NEW);
        task.setId(1);
        epic = new Epic("Эпик", "Описание Эпика 1");
        epic.setId(2);
        subtask = new Subtask("Подзадача ", "Описание подзадачи", Status.NEW, 1);
        subtask.setId(3);
    }

    @Test
    void addTaskAndGetHistory() {
        historyManager.add(task);
       assertEquals(1, historyManager.getHistory().size(), "Задача не добавилась.");
        historyManager.add(subtask);
        assertEquals(2, historyManager.getHistory().size(), "Подзадача не добавилась.");
        historyManager.add(epic);
        assertEquals(3, historyManager.getHistory().size(), "Эпик не добавился.");

      //  assertEquals(epic.getName(), historyManager.getHistory().getFirst().getName(), "Сдвиг истории  не сработал");
      //  assertEquals(task.getName(), historyManager.getHistory().getLast().getName(), "Последняя запись  некорректна.");
      //  assertEquals(task.getName(), historyManager.getHistory().get(InMemoryHistoryManager.MAX_SIZE_OF_HISTORY - 1).getName(), "Последняя запись  некорректна.");
       // assertEquals(10, historyManager.getHistory().size(), "Количество записей в истории некорректно.");
    }



}