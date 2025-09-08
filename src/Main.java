import javakanban.manager.*;
import javakanban.model.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        System.out.println("================ Тест 1 ==================");
        System.out.println("Добавляем две Задачи");
        Task task1 = new Task("Сдать спринт 4", "Необходимо сдать 4 спринт для продолжения обучения");
        Task task2 = new Task("Пройти теорию 5 спринта", "Необходимо сдать 5 спринт для продолжения обучения");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        System.out.println("Результат добавления:\n" + taskManager.getTasks());

        System.out.println("\n================ Тест 2 ==================");
        System.out.println("Обновляет Задачу id=2");
        Task newTask2 = new Task(2, "Пройти теорию 5 спринта, обновленная задача",
                "Необходимо сдать 5 спринт для продолжения обучения", Status.IN_PROGRESS);
        taskManager.updateTask(newTask2);
        System.out.println("Содержимое tasks:\n" + taskManager.getTasks());

        System.out.println("\n================ Тест 3 ==================");
        System.out.println("Содержимое task id=2:\n" + taskManager.getTaskById(2));

        System.out.println("\n================ Тест 4 ==================");
        System.out.println("Удаляем task id=2");
        taskManager.delTaskById(2);
        System.out.println("Содержимое tasks:\n" + taskManager.getTasks());

        System.out.println("\n================ Тест 5 ==================");
        System.out.println("Очищаем tasks");
        taskManager.delTasks();
        System.out.println("Содержимое tasks:\n" + taskManager.getTasks());

        System.out.println("\n================ Тест 6 ==================");
        System.out.println("Добавляем два эпика");
        Epic epic1 = new Epic("Эпик 1: Сдать спринт 4", "Описание Эпика 1");
        Epic epic2 = new Epic("Эпик 2: Сдать спринт 5", "Описание Эпика 2");
        Epic addedEpic1 = taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());

        System.out.println("\n================ Тест 7 ==================");
        System.out.println("\nДобавляем в эпики по две подзадачи");
        Subtask subtask1 = new Subtask("Подзадача 1 Эпика 1: Реализовать ТЗ", "Описание Подзадача 1");
        Subtask subtask2 = new Subtask("Подзадача 2 Эпика 1: Отправить ТЗ на проверку", "Описание Подзадача 2");
        Subtask subtask3 = new Subtask("Подзадача 1 Эпика 2: Пройти теорию Спринта 5", "Описание Подзадача 1");
        Subtask subtask4 = new Subtask("Подзадача 2 Эпика 2: Реализовать ТЗ", "Описание Подзадача 2");
        subtask1.setEpicId(addedEpic1.getId());
        taskManager.addSubtask(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.addSubtask(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.addSubtask(subtask3);
        subtask4.setEpicId(epic2.getId());
        taskManager.addSubtask(subtask4);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());

        System.out.println("\n================ Тест 8 ==================");
        System.out.println("Обновляем статус у Подзадача 2 Эпика 1");
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());

        System.out.println("\n================ Тест 9 ==================");
        System.out.println("Удаляем подзадачу у Эпика 1");
        taskManager.delSubtaskById(6);
        System.out.println("Содержимое Epics (статус изменился):\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());

        System.out.println("\n================ Тест 10 ==================");
        System.out.println("Обновляем статус у Подзадача 1 Эпика 1");
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());

        System.out.println("\n================ Тест 11 ==================");
        System.out.println("Очищаем подзадачи");
        taskManager.delSubtasks();
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());

        System.out.println("\n================ Тест 12 ==================");
        System.out.println("Добавляем Эпик");
        Subtask subtask5 = new Subtask("Подзадача 1 Эпика 2: Реализовать ТЗ", "Описание Подзадача 1");
        subtask5.setEpicId(epic2.getId());
        taskManager.addSubtask(subtask5);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());
        System.out.println("Удаляем Эпик");
        taskManager.delEpicById(4);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());

        System.out.println("\n================ Тест 13 ==================");
        System.out.println("Обновление Эпика");
        Epic epic3 = new Epic("Обновленный Эпик 2: Сдать спринт 5", "Описание Эпика 2");
        epic3.setId(3);
        taskManager.updateEpic(epic3);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());

        System.out.println("\n================ Тест 14 ==================");
        System.out.println("Добавляем Эпик");
        Subtask subtask6 = new Subtask("Подзадача 1 Эпика 1: Реализовать ТЗ", "Описание Подзадача 1");
        subtask6.setEpicId(epic1.getId());
        taskManager.addSubtask(subtask6);
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());
        System.out.println("Удаляем все Эпики");
        taskManager.delEpics();
        System.out.println("Содержимое Epics:\n" + taskManager.getEpics());
        System.out.println("Содержимое Subtasks:\n" + taskManager.getSubtasks());
    }
}
