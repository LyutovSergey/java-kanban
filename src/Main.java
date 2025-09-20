import javakanban.manager.*;
import javakanban.model.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 ,task2;
        Epic epic1, epic2;
        Subtask subtask1, subtask2, subtask3;

        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        epic2 = new Epic("Эпик 2", "Описание Эпика 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId());
        subtask3 = new Subtask("Подзадача 3 (Эпика 2)", "Описание 3", Status.DONE, epic2.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getSubtaskById(subtask3.getId());
        manager.getEpicById(epic2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.delTaskById(task2.getId());

        printAllTasks(manager);
    }


    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksOfEpicById(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
