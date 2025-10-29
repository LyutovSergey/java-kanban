package javakanban.manager;

import javakanban.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest {
    final static String FILE_NAME = "dataTaskManagerTest.csv";
    FileBackedTaskManager taskManager1, taskManager2;
    Task task1, task2;
    Epic epic1, epic2;
    Subtask subtask1, subtask2, subtask3;
    List<String> LinesCSVStandart = Arrays.asList("id,type,name,status,description,epic",
            "1,TASK,Задача 1 отдельная,NEW,Описание задачи 1,",
            "2,TASK,Задача 2 отдельная,NEW,Описание задачи 2,",
            "3,EPIC,Эпик 1 c тремя подзадачами,IN_PROGRESS,Описание Эпика 1,",
            "4,EPIC,Эпик 2 без подзадач ,NEW,Описание Эпика 2,",
            "5,SUBTASK,Подзадача 1 (Эпика 1),NEW,Описание 1,3",
            "6,SUBTASK,Подзадача 2 (Эпика 1),NEW,Описание 2,3",
            "7,SUBTASK,Подзадача 3 (Эпика 1),DONE,Описание 3,3");
    ;

    @BeforeEach
    void genDataBeforeTest() {
        taskManager1 = new FileBackedTaskManager(FILE_NAME,false);
        taskManager2 = new FileBackedTaskManager(FILE_NAME,false);

        task1 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2 отдельная", "Описание задачи 2", Status.NEW);
        taskManager1.addTask(task1);
        taskManager1.addTask(task2);

        epic1 = new Epic("Эпик 1 c тремя подзадачами", "Описание Эпика 1");
        epic2 = new Epic("Эпик 2 без подзадач ", "Описание Эпика 2");
        taskManager1.addEpic(epic1);
        taskManager1.addEpic(epic2);

        subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId());
        subtask3 = new Subtask("Подзадача 3 (Эпика 1)", "Описание 3", Status.DONE, epic1.getId());
        taskManager1.addSubtask(subtask1);
        taskManager1.addSubtask(subtask2);
        taskManager1.addSubtask(subtask3);

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
    void taskManagerToCSV() {
        List<String> LinesCSV = taskManager1.TaskManagerToCSV();
        assertEquals(LinesCSV, LinesCSVStandart, "Метод taskManagerToCSV вернул некорректные данные.");
    }

    @Test
    void CSVToTaskManager() {
        taskManager2.CSVToTaskManager(LinesCSVStandart);
        assertEquals(taskManager1.getTasks(), taskManager2.getTasks(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
        assertEquals(taskManager1.getEpics(), taskManager2.getEpics(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
        assertEquals(taskManager1.getSubtasks(), taskManager2.getSubtasks(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
    }

    @Test
    void SaveAndLoadTaskManager() {
        // Тест загрузки пустого файла
        taskManager2.loadFromFile();
        assertEquals(0, taskManager2.getSubtasks().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");
        assertEquals(0, taskManager2.getTasks().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");
        assertEquals(0, taskManager2.getEpics().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");

        taskManager1.save();
        taskManager2.loadFromFile();
        assertEquals(taskManager1.getTasks(), taskManager2.getTasks(),
                "Загруженные задачи не равны сохранённым.");
        assertEquals(taskManager1.getEpics(), taskManager2.getEpics(),
                "Загруженные задачи не равны сохранённым.");
        assertEquals(taskManager1.getSubtasks(), taskManager2.getSubtasks(),
                "Загруженные задачи не равны сохранённым.");


    }
}