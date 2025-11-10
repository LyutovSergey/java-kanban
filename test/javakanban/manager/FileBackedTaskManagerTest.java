package javakanban.manager;

import javakanban.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    final static String FILE_NAME = "dataTaskManagerTest.csv";
    FileBackedTaskManager taskManagerFB2, taskManagerFB3;
    Task taskFB1, taskFB2;
    Epic epicFB1, epicFB2;
    Subtask subtaskFB1, subtaskFB2, subtaskFB3;
    List<String> LinesCSVStandartFB = Arrays.asList("id,type,name,status,description,epic",
            "2,TASK,Задача 1 отдельная,NEW,Описание задачи 1,,,",
            "3,TASK,Задача 2 отдельная,NEW,Описание задачи 2,,,",
            "5,EPIC,Эпик 1 c тремя подзадачами,IN_PROGRESS,Описание Эпика 1,,,",
            "6,EPIC,Эпик 2 без подзадач ,NEW,Описание Эпика 2,,,",
            "8,SUBTASK,Подзадача 1 (Эпика 1),NEW,Описание 1,5,,",
            "9,SUBTASK,Подзадача 2 (Эпика 1),NEW,Описание 2,5,,",
            "10,SUBTASK,Подзадача 3 (Эпика 1),DONE,Описание 3,5,,");

    protected FileBackedTaskManager getTaskManager() {
        return  new FileBackedTaskManager(FILE_NAME,false);
    }

    @BeforeEach
    void genDataBeforeTestFB() {
        taskManagerFB2 = getTaskManager();// new FileBackedTaskManager(FILE_NAME,false);
        taskManagerFB3 = getTaskManager();  // new FileBackedTaskManager(FILE_NAME,false);

        taskFB1 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW);
        taskFB2 = new Task("Задача 2 отдельная", "Описание задачи 2", Status.NEW);
        taskManagerFB2.addTask(taskFB1);
        // Удаляем первую задачу для смещения счетчика id
        // и дальнейшей проверки неизменности id при сохранении и восстановлении задач
        taskManagerFB2.delTaskById(taskFB1.getId());
        taskManagerFB2.addTask(taskFB1);
        taskManagerFB2.addTask(taskFB2);

        epicFB1 = new Epic("Эпик 1 c тремя подзадачами", "Описание Эпика 1");
        epicFB2 = new Epic("Эпик 2 без подзадач ", "Описание Эпика 2");
        taskManagerFB2.addEpic(epicFB1);
        // Удаляем Эпик 1 для смещения счетчика id и проверки дальнейшей сохранности id
        taskManagerFB2.delEpicById(epicFB1.getId());
        taskManagerFB2.addEpic(epicFB1);
        taskManagerFB2.addEpic(epicFB2);

        subtaskFB1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epicFB1.getId());
        subtaskFB2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epicFB1.getId());
        subtaskFB3 = new Subtask("Подзадача 3 (Эпика 1)", "Описание 3", Status.DONE, epicFB1.getId());
        taskManagerFB2.addSubtask(subtaskFB1);
        // Удаляем Подзадачу 1 для смещения счетчика id и проверки дальнейшей сохранности id
        taskManagerFB2.delSubtaskById(subtaskFB1.getId());
        taskManagerFB2.addSubtask(subtaskFB1);
        taskManagerFB2.addSubtask(subtaskFB2);
        taskManagerFB2.addSubtask(subtaskFB3);

        File file = new File(FILE_NAME);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void fromTaskManagerToCSV() {
        List<String> LinesCSV = taskManagerFB2.fromTaskManagerToCSV();
      //  taskManagerFB2.save();
      //  taskManagerFB2.loadFromFile();
        assertEquals(LinesCSV, LinesCSVStandartFB, "Метод taskManagerToCSV вернул некорректные данные.");
    }

    @Test
    void fromCSVToTaskManager() {
        taskManagerFB3.fromCSVToTaskManager(LinesCSVStandartFB);
        assertEquals(taskManagerFB2.getTasks(), taskManagerFB3.getTasks(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
        assertEquals(taskManagerFB2.getEpics(), taskManagerFB3.getEpics(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
        assertEquals(taskManagerFB2.getSubtasks(), taskManagerFB3.getSubtasks(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
    }

    @Test
    void SaveAndLoadTaskManager() {
        // Тест загрузки пустого файла
        taskManagerFB3.loadFromFile();
        assertEquals(0, taskManagerFB3.getSubtasks().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");
        assertEquals(0, taskManagerFB3.getTasks().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");
        assertEquals(0, taskManagerFB3.getEpics().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");

        taskManagerFB2.save();
        taskManagerFB3.loadFromFile();
        assertEquals(taskManagerFB2.getTasks(), taskManagerFB3.getTasks(),
                "Загруженные задачи не равны сохранённым.");
        assertEquals(taskManagerFB2.getEpics(), taskManagerFB3.getEpics(),
                "Загруженные задачи не равны сохранённым.");
        assertEquals(taskManagerFB2.getSubtasks(), taskManagerFB3.getSubtasks(),
                "Загруженные задачи не равны сохранённым.");


    }

}