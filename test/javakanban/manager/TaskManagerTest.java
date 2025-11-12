package javakanban.manager;

import javakanban.exceptions.InMemoryTaskManagerException;
import javakanban.model.Epic;
import javakanban.model.Status;
import javakanban.model.Subtask;
import javakanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 abstract class TaskManagerTest <T extends TaskManager>{
    T taskManager;
    Task task1, task2;
    Task savedTask;
    Epic epic1, epic2;
    Epic savedEpic;
    Subtask subtask1, subtask2, subtask3;
    Subtask savedSubtask;
    DateTimeFormatter formatterForDataTaskAndCSV = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    abstract T getTaskManager();

    @BeforeEach
    void genDataBeforeTest() {
        taskManager = getTaskManager();

        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-10 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

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

        //Наполнение historyManager = 2 задачи + 1 подзадача + 1 эпик
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
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

        /*
         Тест по обновлению задачи с приоритетом
         Текущий список задач с приоритетами:
         [
            Subtask{epicId=3, id=6, name='Подзадача 2 (Эпика 1), startTime=2025-11-09T01:10, duration=10мин.},
            Task{id=2, name='Задача 2', startTime=2025-11-10T01:00, duration=60 мин.},
            Subtask{epicId=3, id=5, name='Подзадача 1 (Эпика 1)', startTime=2025-11-10T02:00, duration=30мин.}
         ]
          */
        task2.setStartTime(LocalDateTime.parse("2025-11-10 00:01", formatterForDataTaskAndCSV));
        taskManager.updateTask(task2);
        assertEquals(3, taskManager.getPrioritizedTasks().size(), "Обновление приватизированной задачи " +
                "сработало некорректно");
        assertEquals("2025-11-10T00:01", taskManager.getPrioritizedTasks().get(1).getStartTime().get().toString(), "Обновление приватизированной задачи " +
                "сработало некорректно");
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

        // Проверка на возможность присвоить подзадаче неверный epicId
        Subtask subtaskTM4 = new Subtask("Подзадача 4 для несуществующего Эпика ",
                "Описание 4", Status.NEW, 666);
        assertNull(taskManager.addSubtask(subtaskTM4), "Удалось создать подзадачу с неверным epicId");
        assertEquals(3, taskManager.getSubtasks().size(),
                "Удалось создать подзадачу с неверным epicId");

    }

    @Test
    void updateSubtask() {
        savedSubtask = taskManager.getSubtaskById(subtask3.getId());
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
        savedSubtask = taskManager.getSubtaskById(subtask3.getId());
        savedSubtask.setId(subtask3.getEpicId());
        assertNull(taskManager.updateSubtask(savedSubtask), "Удалось подменить Id.");

        /*
         Тест по обновлению подзадачи с приоритетом
         Текущий список задач с приоритетами:
         [
            Subtask{epicId=3, id=6, name='Подзадача 2 (Эпика 1), startTime=2025-11-09T01:10, duration=10мин.},
            Task{id=2, name='Задача 2', startTime=2025-11-10T01:00, duration=60 мин.},
            Subtask{epicId=3, id=5, name='Подзадача 1 (Эпика 1)', startTime=2025-11-10T02:00, duration=30мин.}
         ]
          */
        subtask1.setStartTime(LocalDateTime.parse("2025-11-15 02:01", formatterForDataTaskAndCSV));
        taskManager.updateSubtask(subtask1);
        assertEquals(3, taskManager.getPrioritizedTasks().size(), "Обновление приоритетной подзадачи " +
                "сработало некорректно");
        assertEquals("2025-11-15T02:01", taskManager.getPrioritizedTasks().get(2).getStartTime().get().toString(),
                "Обновление приоритетной подзадачи сработало некорректно");

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
        assertEquals(2, taskManager.getHistory().size(), "Задачи не удалились из истории.");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Задачи не удалились из приоритета.");
    }

    @Test
    void delEpics() {
        taskManager.delEpics();
        assertEquals(0, taskManager.getEpics().size(), "Эпики не удалились.");
        assertEquals(2, taskManager.getHistory().size(),
                "Подзадача не удалились из истории при удалении Эпика.");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Подзадачи эпиков не удалились из приоритета.");

    }

    @Test
    void delSubtasks() {
        taskManager.delSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(), "Подзадачи не удалились.");
        assertEquals(3, taskManager.getHistory().size(),
                "Подзадача не удалились из истории.");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Подзадачи не удалились из приоритета.");

    }

    @Test
    void delTaskById() {
        taskManager.delTaskById(task1.getId());
        assertNull(taskManager.getTaskById(task1.getId()), "Задача 1 не удалилась.");
        assertEquals(3, taskManager.getHistory().size(),
                "Задача не удалились из истории.");

        taskManager.delTaskById(task2.getId());
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Задача не удалились из приоритета.");


    }

    @Test
    void delSubtaskById() {
        taskManager.delSubtaskById(subtask3.getId());
        assertEquals(3, taskManager.getHistory().size(), "Подзадача не удалились из истории.");
        taskManager.delSubtaskById(subtask1.getId());
        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Подзадача 1 не удалилась.");
        assertFalse(taskManager.getEpicById(subtask1.getEpicId()).getSubtasksId().contains(subtask1.getId()),
                "Подзадача 1 не удалилась из Эпика.");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Подзадача не удалились из приоритета.");

    }

    @Test
    void delEpicById() {
        taskManager.delEpicById(epic1.getId());
        assertNull(taskManager.getEpicById(epic1.getId()), "Эпик 1 не удалился.");
        assertEquals(3, taskManager.getHistory().size(), "Эпик 1 не удался из истории.");
        assertEquals(1, taskManager.getPrioritizedTasks().size(),
                "Подзадачи эпика не удалились из приоритета.");
    }

     @Test
     void calculateStatusEpicById() {
        // Проверка статуса Эпика с подзадачами в статусе NEW
         assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus(),
                 "Проверка статуса Эпика с подзадачами в статусе NEW не пройдена");

         // Проверка статуса Эпика с подзадачами в статусе NEW и DONE
         savedSubtask = taskManager.getSubtaskById(subtask1.getId());
         savedSubtask.setStatus(Status.DONE); // Меняем статус у единственной подзадачи Эпика 1
         taskManager.updateSubtask(savedSubtask);
         assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(),
                 "Проверка статуса Эпика с подзадачами в статусе NEW и DONE не пройдена");

         // Проверка статуса Эпика с подзадачами в статусе IN_PROGRESS
         savedSubtask = taskManager.getSubtaskById(subtask1.getId());
         savedSubtask.setStatus(Status.IN_PROGRESS); // Меняем статус у подзадачи Эпика1
         taskManager.updateSubtask(savedSubtask);
         savedSubtask = taskManager.getSubtaskById(subtask2.getId());
         savedSubtask.setStatus(Status.IN_PROGRESS); // Меняем статус у подзадачи Эпика 1
         taskManager.updateSubtask(savedSubtask);
         assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(),
                 "Проверка статуса Эпика с подзадачами в статусе IN_PROGRESS не пройдена");

         savedSubtask = taskManager.getSubtaskById(subtask3.getId());
         savedSubtask.setStatus(Status.DONE); // Меняем статус у единственной подзадачи Эпика 2
         taskManager.updateSubtask(savedSubtask);
         assertEquals(Status.DONE, taskManager.getEpicById(subtask3.getEpicId()).getStatus(),
                 "Проверка статуса Эпика с единственной подзадачей  в статусе DONE не пройдена");
     }

     @Test
     void getPrioritizedTasks() {
        String correctResult1 = "[Subtask{epicId=3, id=6, name='Подзадача 2 (Эпика 1)', description='Описание 2', " +
                "status=NEW, startTime=2025-11-09T01:10, duration=10мин.}, " +
                "Task{id=2, name='Задача 2', description='Описание задачи 2', " +
                "status=NEW, startTime=2025-11-10T01:00, duration=60 мин.}, " +
                "Subtask{epicId=3, id=5, name='Подзадача 1 (Эпика 1)', description='Описание 1', " +
                "status=NEW, startTime=2025-11-10T02:00, duration=30мин.}]";

         String correctResult2 = "[Subtask{epicId=3, id=5, name='Подзадача 1 (Эпика 1)', description='Описание 1', " +
                 "status=NEW, startTime=2025-11-10T02:00, duration=30мин.}]";

         assertEquals(correctResult1, taskManager.getPrioritizedTasks().toString(),
                 "Список приоритетных задач некорректен");

         //Проверка списка приоритетных задач после удаления
         taskManager.delTaskById(2);
         taskManager.delSubtaskById(6);
         assertEquals(correctResult2, taskManager.getPrioritizedTasks().toString(),
                 "Список приоритетных задач некорректен");
     }

     @Test
     void calculateTimeEpicById() {
         assertEquals("2025-11-09T01:10", taskManager.getEpicById(epic1.getId()).getStartTime().get().toString(),
                 "Рассчитанное время начало Эпика1 неверно");
         assertEquals(40, taskManager.getEpicById(epic1.getId()).getDuration().get().toMinutes(),
                 "Рассчитанная продолжительность Эпика1 неверно");
         assertEquals("2025-11-10T02:30", taskManager.getEpicById(epic1.getId()).getEndTime().get().toString(),
                 "Рассчитанное время окончание Эпика1 неверно");

         assertFalse(taskManager.getEpicById(epic2.getId()).getStartTime().isPresent(),
                 "Рассчитанное время начало Эпика2 неверно");
         assertFalse(taskManager.getEpicById(epic2.getId()).getDuration().isPresent(),
                 "Рассчитанная продолжительность Эпика2 неверно");
         assertFalse(taskManager.getEpicById(epic2.getId()).getEndTime().isPresent(),
                 "Рассчитанное время окончание Эпика12 неверно");
     }

     @Test void  isOverlapOfTimeTasks(){
         /*
         Текущий список задач с приоритетами:
         [
            Subtask{epicId=3, id=6, name='Подзадача 2 (Эпика 1), startTime=2025-11-09T01:10, duration=10мин.},
            Task{id=2, name='Задача 2', startTime=2025-11-10T01:00, duration=60 мин.},
            Subtask{epicId=3, id=5, name='Подзадача 1 (Эпика 1)', startTime=2025-11-10T02:00, duration=30мин.}
         ]
          */

        //Проверка добавить задачу полностью совпадающей по времени выполнения с другой
         Task task = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW,
                 LocalDateTime.parse("2025-11-10 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
             assertThrows(InMemoryTaskManagerException.class, () -> {
                 taskManager.addTask(task);
             }, "Удалось добавить задачу полностью совпадающей по времени выполнения с другой");

         //Попытка добавить задачу, пересекающуюся по времени выполнения справа с другой
          Task task2 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW,
                 LocalDateTime.parse("2025-11-10 01:50", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
         assertThrows(InMemoryTaskManagerException.class, () -> {
             taskManager.addTask(task2);
         }, "Удалось добавить задачу пересекающуюся по времени выполнения справа с другой");

         //Попытка добавить задачу, пересекающуюся по времени выполнения слева с другой
         Task task3 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW,
                 LocalDateTime.parse("2025-11-10 00:01", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
         assertThrows(InMemoryTaskManagerException.class, () -> {
             taskManager.addTask(task3);
         }, "Удалось добавить задачу пересекающуюся по времени выполнения слева с другой");
     }
}
