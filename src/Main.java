import javakanban.manager.*;
import javakanban.model.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 ,task2;
        Epic epic1, epic2;
        Subtask subtask1, subtask2, subtask3;

        task1 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2 отдельная", "Описание задачи 2", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        epic1 = new Epic("Эпик 1 c тремя подзадачами", "Описание Эпика 1");
        epic2 = new Epic("Эпик 2 без подзадач ", "Описание Эпика 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId());
        subtask3 = new Subtask("Подзадача 3 (Эпика 1)", "Описание 3", Status.DONE, epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getEpicById(epic2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getSubtaskById(subtask3.getId());

        printAllTasks(manager);

        System.out.println("\n--------Удаляем задачу 2----------------");
        manager.delTaskById(task2.getId());
        printHistory(manager);

        System.out.println("\n--------Удаляем Эпик 1 с тремя подзадачами----------------");
        manager.delEpicById(epic1.getId());
        printHistory(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksOfEpicById(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
