package javakanban.manager;

import javakanban.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager;
    Task task1, task2;
    Task savedTask;
    Epic epic1, epic2;
    Epic savedEpic;
    Subtask subtask1, subtask2, subtask3;
    Subtask savedSubtask;


    @BeforeEach
    void genDataBeforeTest() {
        taskManager = Managers.getDefault();

        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        epic2 = new Epic("Эпик 2", "Описание Эпика 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId());
        subtask3 = new Subtask("Подзадача 3 (Эпика 2)", "Описание 3", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
    }


    @Test
    void addTask() {
        assertNotNull(task1.getId(), "Задача 1 не добавилась.");
        savedTask =taskManager.getTaskById(task1.getId());
        assertEquals(task1, savedTask, "Задача 1 не совпадает с сохранённой.");

        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.NEW);
        taskManager.addTask(task3);
        savedTask = taskManager.getTaskById(task3.getId());
        assertNotNull(savedTask.getId(), "Задача 3 не добавилась.");
        assertEquals(task3.getName(), savedTask.getName(), "Задача не совпадает по Названию с сохранённой.");
        assertEquals(task3.getDescription(), savedTask.getDescription(), "Задача не совпадает по Описанию с сохранённой.");
        assertEquals(task3.getStatus(), savedTask.getStatus(), "Задача не совпадает по Статусу с сохранённой.");

    }

    @Test
    void updateTask() {
        task2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task2);
        savedTask =taskManager.getTaskById(task2.getId());
        assertEquals(task2.getId(), savedTask.getId(), "Задача 2 не совпадает по Id с сохранённой.");
        assertEquals(task2.getName(), savedTask.getName(), "Задача 2 не совпадает по Названию с сохранённой.");
        assertEquals(task2.getDescription(), savedTask.getDescription(), "Задача 2 не совпадает "
                                                                                    +"по Описанию с сохранённой.");
        assertEquals(Status.IN_PROGRESS, savedTask.getStatus(), "Задача 2 не совпадает по Статусу "
                                                                        + "с сохранённой.");
    }

    @Test
    void getTaskById() {
        assertNotNull(taskManager.getTaskById(1), "Задача 1 не получена.");
        assertNull(taskManager.getTaskById(10), "Получена задача с несуществующим Id.");
    }

    @Test
    void addEpic() {
        savedEpic = taskManager.getEpicById(epic1.getId());
        assertNotNull(epic1.getId(), "Epic 1 не добавился.");
        assertEquals(epic1, savedEpic, "Epic 1 не совпадает с сохранённым.");

        Epic epic3 = new Epic("Эпик 3", "Описание Эпика 3");
        taskManager.addEpic(epic3);
        savedEpic = taskManager.getEpicById(epic3.getId());
        assertEquals(epic3.getName(), savedEpic.getName(), "Эпик не совпадает по Названию с сохранённым.");
        assertEquals(epic3.getDescription(), savedEpic.getDescription(), "Эпик не совпадает по Описанию с сохранённым.");
        assertEquals(epic3.getStatus(), savedEpic.getStatus(), "Эпик не совпадает по Статусу с сохранённым.");
    }

    @Test
    void updateEpic() {
        savedEpic = taskManager.getEpicById(epic2.getId());
        savedEpic.setName("Эпик 2 обновленный");
        savedEpic.setDescription("Описание Эпика 2 обновленное");
        taskManager.updateEpic(savedEpic);
        assertEquals("Эпик 2 обновленный", taskManager.getEpicById(epic2.getId()).getName(),
                                                                "Epic 2 не совпадает Имя с обновленным.");
        assertEquals("Описание Эпика 2 обновленное", taskManager.getEpicById(epic2.getId()).getDescription(),
                                                                "Epic 2 не совпадает Описание с обновленным.");
    }

    @Test
    void getEpicById() {
        assertNotNull(taskManager.getEpicById(epic1.getId()), "Эпик 1 не получен.");
        assertNull(taskManager.getEpicById(100), "Получен Эпик с несуществующим Id.");
    }

    @Test
    void getSubtasksOfEpicById() {
        List<Subtask> subtasks = new ArrayList<>(taskManager.getSubtasksOfEpicById(epic1.getId()));
        assertEquals(2, subtasks.size(), "Вернулся массив подзадач неверного размера.");
        assertTrue(subtasks.contains(subtask1),"В Эпике 1 отсутствует Подзадача 1.");
        assertTrue(subtasks.contains(subtask2),"В Эпике 1 отсутствует Подзадача 2.");
        subtasks = new ArrayList<>(taskManager.getSubtasksOfEpicById(100));
        assertEquals(0, subtasks.size(), "Вернулся массив подзадач неверного размера.");
    }

    @Test
    void addSubtask() {
        savedSubtask = taskManager.getSubtaskById(subtask1.getId());
        assertNotNull(subtask1.getId(), "Подзадача не добавилась.");
        assertEquals(subtask1, savedSubtask, "Подзадача 1 не совпадает с сохранённой.");
        // Проверка попытки добавить Эпика как подзадачи в самого себя - невозможно - не даёт компилировать
        //taskManager.addSubtask(epic1);


    }

    @Test
    void updateSubtask() {
        savedSubtask= taskManager.getSubtaskById(subtask3.getId());
        savedSubtask.setName("Подзадача 3 (Эпика 2) обновленная");
        savedSubtask.setDescription("Описание 3 обновленное");
        savedSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask);
        assertEquals("Подзадача 3 (Эпика 2) обновленная", taskManager.getSubtaskById(subtask3.getId()).getName(),
                "Подзадача 2 не совпадает Имя с обновленным.");
        assertEquals("Описание 3 обновленное", taskManager.getSubtaskById(subtask3.getId()).getDescription(),
                "Подзадача 2 не совпадает Описание с обновленным.");
        assertEquals(Status.DONE, taskManager.getSubtaskById(subtask3.getId()).getStatus(),
                                                "Эпик 2 не изменил свой статус при обновлении подзадачи.");

        // Проверка на подмену Id
        savedSubtask= taskManager.getSubtaskById(subtask3.getId());
        savedSubtask.setId(subtask3.getEpicId());
        assertNull(taskManager.updateSubtask(savedSubtask), "Удалось подменить Id.");
    }

    @Test
    void getSubtaskById() {
        assertNotNull(taskManager.getSubtaskById(subtask1.getId()), "Подзадача 1 не получена.");
        assertNull(taskManager.getSubtaskById(100), "Получена подзадача с несуществующим Id.");
    }

    @Test
    void getTasks() {
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются.");
        assertEquals(2, taskManager.getTasks().size(), "Неверное количество задач.");
    }

    @Test
    void getEpics() {
        List<Epic> epics = new ArrayList<>(taskManager.getEpics());
        assertEquals(2, epics.size(), "Вернулся массив Эпиков неверного размера.");
        assertTrue(epics.contains(epic1),"Эпик 1 отсутствует.");
        assertTrue(epics.contains(epic2),"Эпик 2 отсутствует.");
    }

    @Test
    void getSubtasks() {
        List<Subtask> subtasks = new ArrayList<>(taskManager.getSubtasks());
        assertEquals(3, subtasks.size(), "Вернулся массив Эпиков неверного размера.");
        assertTrue(subtasks.contains(subtask1),"Подзадача 1 отсутствует.");
        assertTrue(subtasks.contains(subtask2),"Подзадача 2 отсутствует.");
        assertTrue(subtasks.contains(subtask3),"Подзадача 3 отсутствует.");
    }

    @Test
    void delTasks() {
        taskManager.delTasks();
        assertEquals(0, taskManager.getTasks().size(), "Задачи не удалились.");
    }

    @Test
    void delEpics() {
        taskManager.delEpics();
        assertEquals(0, taskManager.getEpics().size(), "Эпики не удалились.");
    }

    @Test
    void delSubtasks() {
        taskManager.delSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(), "Подзадачи не удалились.");
    }

    @Test
    void calculateStatusEpicById() {
        savedSubtask= taskManager.getSubtaskById(subtask3.getId());
        savedSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask);
        assertEquals(Status.DONE, taskManager.getEpicById(subtask3.getEpicId()).getStatus(),
                "Эпик 2 не изменил свой статус при обновлении подзадачи.");
    }

    @Test
    void delTaskById() {
        taskManager.delTaskById(task1.getId());
        assertNull(taskManager.getTaskById(task1.getId()), "Задача 1 не удалилась.");
    }

    @Test
    void delSubtaskById() {
        taskManager.delSubtaskById(subtask1.getId());
        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Подзадача 1 не удалилась.");
        assertFalse(taskManager.getEpicById(subtask1.getEpicId()).getSubtasksId().contains(subtask1.getId()),
                                                                "Подзадача 1 не удалилась из Эпика.");
    }

    @Test
    void delEpicById() {
        taskManager.delEpicById(epic1.getId());
        assertNull(taskManager.getEpicById(epic1.getId()), "Эпик 1 не удалился.");
    }

    @Test
    void getHistory() {
    }
}