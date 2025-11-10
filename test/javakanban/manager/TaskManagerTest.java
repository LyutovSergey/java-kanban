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
    Task taskTM1, taskTM2;
    Task savedTaskTM;
    Epic epicTM1, epicTM2;
    Epic savedEpicTM;
    Subtask subtaskTM1, subtaskTM2, subtask3TM;
    Subtask savedSubtaskTM;
    DateTimeFormatter formatterForDataTaskAndCSV = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    abstract T getTaskManager();

    @BeforeEach
    void genDataBeforeTestTM() {
        taskManager = getTaskManager();

        taskTM1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        taskTM2 = new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.parse("2025-11-10 01:00", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
        taskManager.addTask(taskTM1);
        taskManager.addTask(taskTM2);

        epicTM1 = new Epic("Эпик 1", "Описание Эпика 1");
        epicTM2 = new Epic("Эпик 2", "Описание Эпика 2");
        taskManager.addEpic(epicTM1);
        taskManager.addEpic(epicTM2);

        subtaskTM1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epicTM1.getId(),
                LocalDateTime.parse("2025-11-10 02:00", formatterForDataTaskAndCSV), Duration.ofMinutes(30));
        subtaskTM2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epicTM1.getId(),
            LocalDateTime.parse("2025-11-09 01:10", formatterForDataTaskAndCSV), Duration.ofMinutes(10));
        subtask3TM = new Subtask("Подзадача 3 (Эпика 2)", "Описание 3", Status.NEW, epicTM2.getId());
        taskManager.addSubtask(subtaskTM1);
        taskManager.addSubtask(subtaskTM2);
        taskManager.addSubtask(subtask3TM);
    }


    @Test
    void addTask() {
        assertNotNull(taskTM1.getId(), "Задача 1 не добавилась.");
        savedTaskTM =taskManager.getTaskById(taskTM1.getId());
        assertEquals(taskTM1, savedTaskTM, "Задача 1 не совпадает с сохранённой.");

        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.NEW);
        taskManager.addTask(task3);
        savedTaskTM = taskManager.getTaskById(task3.getId());
        assertNotNull(savedTaskTM.getId(), "Задача 3 не добавилась.");
        assertEquals(task3.getName(), savedTaskTM.getName(), "Задача не совпадает по Названию с сохранённой.");
        assertEquals(task3.getDescription(), savedTaskTM.getDescription(), "Задача не совпадает по Описанию с сохранённой.");
        assertEquals(task3.getStatus(), savedTaskTM.getStatus(), "Задача не совпадает по Статусу с сохранённой.");

    }

    @Test
    void updateTask() {
        taskTM2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(taskTM2);
        savedTaskTM =taskManager.getTaskById(taskTM2.getId());
        assertEquals(taskTM2.getId(), savedTaskTM.getId(), "Задача 2 не совпадает по Id с сохранённой.");
        assertEquals(taskTM2.getName(), savedTaskTM.getName(), "Задача 2 не совпадает по Названию с сохранённой.");
        assertEquals(taskTM2.getDescription(), savedTaskTM.getDescription(), "Задача 2 не совпадает "
                +"по Описанию с сохранённой.");
        assertEquals(Status.IN_PROGRESS, savedTaskTM.getStatus(), "Задача 2 не совпадает по Статусу "
                + "с сохранённой.");
    }

    @Test
    void getTaskById() {
        assertNotNull(taskManager.getTaskById(1), "Задача 1 не получена.");
        assertNull(taskManager.getTaskById(10), "Получена задача с несуществующим Id.");
    }

    @Test
    void addEpic() {
        savedEpicTM = taskManager.getEpicById(epicTM1.getId());
        assertNotNull(epicTM1.getId(), "Epic 1 не добавился.");
        assertEquals(epicTM1, savedEpicTM, "Epic 1 не совпадает с сохранённым.");

        Epic epic3 = new Epic("Эпик 3", "Описание Эпика 3");
        taskManager.addEpic(epic3);
        savedEpicTM = taskManager.getEpicById(epic3.getId());
        assertEquals(epic3.getName(), savedEpicTM.getName(), "Эпик не совпадает по Названию с сохранённым.");
        assertEquals(epic3.getDescription(), savedEpicTM.getDescription(), "Эпик не совпадает по Описанию с сохранённым.");
        assertEquals(epic3.getStatus(), savedEpicTM.getStatus(), "Эпик не совпадает по Статусу с сохранённым.");
    }

    @Test
    void updateEpic() {
        savedEpicTM = taskManager.getEpicById(epicTM2.getId());
        savedEpicTM.setName("Эпик 2 обновленный");
        savedEpicTM.setDescription("Описание Эпика 2 обновленное");
        taskManager.updateEpic(savedEpicTM);
        assertEquals("Эпик 2 обновленный", taskManager.getEpicById(epicTM2.getId()).getName(),
                "Epic 2 не совпадает Имя с обновленным.");
        assertEquals("Описание Эпика 2 обновленное", taskManager.getEpicById(epicTM2.getId()).getDescription(),
                "Epic 2 не совпадает Описание с обновленным.");
    }

    @Test
    void getEpicById() {
        assertNotNull(taskManager.getEpicById(epicTM1.getId()), "Эпик 1 не получен.");
        assertNull(taskManager.getEpicById(100), "Получен Эпик с несуществующим Id.");
    }

    @Test
    void getSubtasksOfEpicById() {
        List<Subtask> subtasks = new ArrayList<>(taskManager.getSubtasksOfEpicById(epicTM1.getId()));
        assertEquals(2, subtasks.size(), "Вернулся массив подзадач неверного размера.");
        assertTrue(subtasks.contains(subtaskTM1),"В Эпике 1 отсутствует Подзадача 1.");
        assertTrue(subtasks.contains(subtaskTM2),"В Эпике 1 отсутствует Подзадача 2.");
        subtasks = new ArrayList<>(taskManager.getSubtasksOfEpicById(100));
        assertEquals(0, subtasks.size(), "Вернулся массив подзадач неверного размера.");
    }

    @Test
    void addSubtask() {
        savedSubtaskTM = taskManager.getSubtaskById(subtaskTM1.getId());
        assertNotNull(subtaskTM1.getId(), "Подзадача не добавилась.");
        assertEquals(subtaskTM1, savedSubtaskTM, "Подзадача 1 не совпадает с сохранённой.");
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
        savedSubtaskTM = taskManager.getSubtaskById(subtask3TM.getId());
        savedSubtaskTM.setName("Подзадача 3 (Эпика 2) обновленная");
        savedSubtaskTM.setDescription("Описание 3 обновленное");
        savedSubtaskTM.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtaskTM);
        assertEquals("Подзадача 3 (Эпика 2) обновленная", taskManager.getSubtaskById(subtask3TM.getId()).getName(),
                "Подзадача 2 не совпадает Имя с обновленным.");
        assertEquals("Описание 3 обновленное", taskManager.getSubtaskById(subtask3TM.getId()).getDescription(),
                "Подзадача 2 не совпадает Описание с обновленным.");
        assertEquals(Status.DONE, taskManager.getSubtaskById(subtask3TM.getId()).getStatus(),
                "Эпик 2 не изменил свой статус при обновлении подзадачи.");

        // Проверка на подмену Id
        savedSubtaskTM = taskManager.getSubtaskById(subtask3TM.getId());
        savedSubtaskTM.setId(subtask3TM.getEpicId());
        assertNull(taskManager.updateSubtask(savedSubtaskTM), "Удалось подменить Id.");

    }

    @Test
    void getSubtaskById() {
        assertNotNull(taskManager.getSubtaskById(subtaskTM1.getId()), "Подзадача 1 не получена.");
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
        assertTrue(epics.contains(epicTM1),"Эпик 1 отсутствует.");
        assertTrue(epics.contains(epicTM2),"Эпик 2 отсутствует.");
    }

    @Test
    void getSubtasks() {
        List<Subtask> subtasks = new ArrayList<>(taskManager.getSubtasks());
        assertEquals(3, subtasks.size(), "Вернулся массив Эпиков неверного размера.");
        assertTrue(subtasks.contains(subtaskTM1),"Подзадача 1 отсутствует.");
        assertTrue(subtasks.contains(subtaskTM2),"Подзадача 2 отсутствует.");
        assertTrue(subtasks.contains(subtask3TM),"Подзадача 3 отсутствует.");
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
    void delTaskById() {
        taskManager.delTaskById(taskTM1.getId());
        assertNull(taskManager.getTaskById(taskTM1.getId()), "Задача 1 не удалилась.");
    }

    @Test
    void delSubtaskById() {
        taskManager.delSubtaskById(subtaskTM1.getId());
        assertNull(taskManager.getSubtaskById(subtaskTM1.getId()), "Подзадача 1 не удалилась.");
        assertFalse(taskManager.getEpicById(subtaskTM1.getEpicId()).getSubtasksId().contains(subtaskTM1.getId()),
                "Подзадача 1 не удалилась из Эпика.");
    }

    @Test
    void delEpicById() {
        taskManager.delEpicById(epicTM1.getId());
        assertNull(taskManager.getEpicById(epicTM1.getId()), "Эпик 1 не удалился.");
    }

     @Test
     void calculateStatusEpicById() {

        // Проверка статуса Эпика с подзадачами в статусе NEW
         assertEquals(Status.NEW, taskManager.getEpicById(epicTM1.getId()).getStatus(),
                 "Проверка статуса Эпика с подзадачами в статусе NEW не пройдена");

         // Проверка статуса Эпика с подзадачами в статусе NEW и DONE
         savedSubtaskTM = taskManager.getSubtaskById(subtaskTM1.getId());
         savedSubtaskTM.setStatus(Status.DONE); // Меняем статус у единственной подзадачи Эпика 2
         taskManager.updateSubtask(savedSubtaskTM);
         assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicTM1.getId()).getStatus(),
                 "Проверка статуса Эпика с подзадачами в статусе NEW и DONE не пройдена");

         // Проверка статуса Эпика с подзадачами в статусе IN_PROGRESS
         savedSubtaskTM = taskManager.getSubtaskById(subtaskTM1.getId());
         savedSubtaskTM.setStatus(Status.IN_PROGRESS); // Меняем статус у единственной подзадачи Эпика 2
         taskManager.updateSubtask(savedSubtaskTM);
         savedSubtaskTM = taskManager.getSubtaskById(subtaskTM2.getId());
         savedSubtaskTM.setStatus(Status.IN_PROGRESS); // Меняем статус у единственной подзадачи Эпика 2
         taskManager.updateSubtask(savedSubtaskTM);
         assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicTM1.getId()).getStatus(),
                 "Проверка статуса Эпика с подзадачами в статусе IN_PROGRESS не пройдена");

         savedSubtaskTM = taskManager.getSubtaskById(subtask3TM.getId());
         savedSubtaskTM.setStatus(Status.DONE); // Меняем статус у единственной подзадачи Эпика 2
         taskManager.updateSubtask(savedSubtaskTM);
         assertEquals(Status.DONE, taskManager.getEpicById(subtask3TM.getEpicId()).getStatus(),
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
         assertEquals("2025-11-09T01:10", taskManager.getEpicById(epicTM1.getId()).getStartTime().get().toString(),
                 "Рассчитанное время начало Эпика1 неверно");
         assertEquals(40, taskManager.getEpicById(epicTM1.getId()).getDuration().get().toMinutes(),
                 "Рассчитанная продолжительность Эпика1 неверно");
         assertEquals("2025-11-10T02:30", taskManager.getEpicById(epicTM1.getId()).getEndTime().get().toString(),
                 "Рассчитанное время окончание Эпика1 неверно");

         assertFalse(taskManager.getEpicById(epicTM2.getId()).getStartTime().isPresent(),
                 "Рассчитанное время начало Эпика2 неверно");
         assertFalse(taskManager.getEpicById(epicTM2.getId()).getDuration().isPresent(),
                 "Рассчитанная продолжительность Эпика2 неверно");
         assertFalse(taskManager.getEpicById(epicTM2.getId()).getEndTime().isPresent(),
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

         //Попытка добавить задачу пересекающуюся по времени выполнения справа с другой
          Task task2 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW,
                 LocalDateTime.parse("2025-11-10 01:50", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
         assertThrows(InMemoryTaskManagerException.class, () -> {
             taskManager.addTask(task2);
         }, "Удалось добавить задачу пересекающуюся по времени выполнения справа с другой");

         //Попытка добавить задачу пересекающуюся по времени выполнения слева с другой
         Task task3 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW,
                 LocalDateTime.parse("2025-11-10 00:01", formatterForDataTaskAndCSV), Duration.ofMinutes(60));
         assertThrows(InMemoryTaskManagerException.class, () -> {
             taskManager.addTask(task3);
         }, "Удалось добавить задачу пересекающуюся по времени выполнения слева с другой");
     }

/*
     @Test
     void shouldCalculateEpicTime() {
         LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
         Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));

         Subtask subtask1 = taskManager.createSubtask(
                 new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                         Duration.ofHours(1), baseTime));

         Subtask subtask2 = taskManager.createSubtask(
                 new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),
                         Duration.ofHours(2), baseTime.plusHours(1)));

         assertEquals(baseTime, epic.getStartTime());
         assertEquals(baseTime.plusHours(3), epic.getEndTime());
     }
*/

}
