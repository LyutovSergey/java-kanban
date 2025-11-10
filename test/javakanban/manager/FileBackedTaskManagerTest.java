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
    FileBackedTaskManager taskManager2;
    List<String> LinesCSVStandart = Arrays.asList("id,type,name,status,description,epic,startTime,duration",
            "2,TASK,Задача 2,NEW,Описание задачи 2,,2025-11-10 01:00,60",
            "8,TASK,Задача 1,NEW,Описание задачи 1,,,",
            "3,EPIC,Эпик 1,IN_PROGRESS,Описание Эпика 1,,,",
            "4,EPIC,Эпик 2,DONE,Описание Эпика 2,,,",
            "5,SUBTASK,Подзадача 1 (Эпика 1),IN_PROGRESS,Описание 1,3,2025-11-10 02:00,30",
            "6,SUBTASK,Подзадача 2 (Эпика 1),NEW,Описание 2,3,2025-11-09 01:10,10",
            "7,SUBTASK,Подзадача 3 (Эпика 2),DONE,Описание 3,4,,");

    protected FileBackedTaskManager getTaskManager() {
        return  new FileBackedTaskManager(FILE_NAME,false);
    }

    void updateDataBeforeTestFB() {
        taskManager2 = new FileBackedTaskManager(FILE_NAME,false);
        // Удаляем первую задачу для смещения счетчика id
        // и дальнейшей проверки неизменности id при сохранении и восстановлении задач
        taskManager.delTaskById(task1.getId());
        taskManager.addTask(task1);

        // Меняем статус у единственной подзадачи Эпика 2
        savedSubtask = taskManager.getSubtaskById(subtask3.getId());
        savedSubtask.setStatus(Status.DONE); // Меняем статус у единственной подзадачи Эпика 2
        taskManager.updateSubtask(savedSubtask);

        // Меняем статус у подзадачи Эпика1
        savedSubtask = taskManager.getSubtaskById(subtask1.getId());
        savedSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubtask);



        // Очистка или создание файла для данных
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
        updateDataBeforeTestFB();
        List<String> LinesCSV = taskManager.fromTaskManagerToCSV();
        assertEquals(LinesCSV, LinesCSVStandart, "Метод taskManagerToCSV вернул некорректные данные.");
    }

    @Test
    void fromCSVToTaskManager() {
        updateDataBeforeTestFB();
        taskManager2.fromCSVToTaskManager(LinesCSVStandart);
        assertEquals(taskManager.getTasks().toString(), taskManager2.getTasks().toString(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
        assertEquals(taskManager.getEpics().toString(), taskManager2.getEpics().toString(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
        assertEquals(taskManager.getSubtasks().toString(), taskManager2.getSubtasks().toString(),
                "Метод CSVToTaskManager загрузил некорректные данные.");
    }

    @Test
    void SaveAndLoadTaskManager() {
        updateDataBeforeTestFB();
        // Тест загрузки пустого файла
        taskManager2.loadFromFile();
        assertEquals(0, taskManager2.getSubtasks().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");
        assertEquals(0, taskManager2.getTasks().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");
        assertEquals(0, taskManager2.getEpics().size(),
                "Метод loadFromFile загрузил некорректные данные из пустого файла.");

        taskManager.save();
        taskManager2.loadFromFile();
        assertEquals(taskManager.getTasks().toString(), taskManager2.getTasks().toString(),
                "Загруженные задачи не равны сохранённым.");
        assertEquals(taskManager.getEpics().toString(), taskManager2.getEpics().toString(),
                "Загруженные задачи не равны сохранённым.");
        assertEquals(taskManager.getSubtasks().toString(), taskManager2.getSubtasks().toString(),
                "Загруженные задачи не равны сохранённым.");


    }

}