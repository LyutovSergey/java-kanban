import javakanban.manager.*;
import javakanban.model.*;

public class Main {

    public static void main(String[] args) {

        Task task1, task2;
        Epic epic1, epic2;
        Subtask subtask1, subtask2, subtask3;

        TaskManager taskManager1 = new FileBackedTaskManager(false);

        task1 = new Task("Задача 1 отдельная", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2 отдельная", "Описание задачи 2", Status.NEW);
        taskManager1.addTask(task1);
        // Удаляем первую задачу для смещения счетчика id
        // и дальнейшей проверки неизменности id при сохранении и восстановлении задач
        taskManager1.delTaskById(task1.getId());
        taskManager1.addTask(task1);
        taskManager1.addTask(task2);

        epic1 = new Epic("Эпик 1 c тремя подзадачами", "Описание Эпика 1");
        epic2 = new Epic("Эпик 2 без подзадач ", "Описание Эпика 2");
        taskManager1.addEpic(epic1);
        // Удаляем Эпик 1 для смещения счетчика id и проверки дальнейшей сохранности id
        taskManager1.delEpicById(epic1.getId());
        taskManager1.addEpic(epic1);
        taskManager1.addEpic(epic2);

        subtask1 = new Subtask("Подзадача 1 (Эпика 1)", "Описание 1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("Подзадача 2 (Эпика 1)", "Описание 2", Status.NEW, epic1.getId());
        subtask3 = new Subtask("Подзадача 3 (Эпика 1)", "Описание 3", Status.DONE, epic1.getId());
        taskManager1.addSubtask(subtask1);
        // Удаляем Подзадачу 1 для смещения счетчика id и проверки дальнейшей сохранности id
        taskManager1.delSubtaskById(subtask1.getId());
        taskManager1.addSubtask(subtask1);
        taskManager1.addSubtask(subtask2);
        taskManager1.addSubtask(subtask3);

        TaskManager taskManager2 = Managers.getTaskManager(TypeTaskManager.FILE_BACKED); // Инициализация с загрузкой

        System.out.println("-----------Данные taskManager1-----------------");
        printAllTasks(taskManager1);
        System.out.println("\n\n-----------Данные taskManager2-----------------");
        printAllTasks(taskManager2);
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
        //printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
