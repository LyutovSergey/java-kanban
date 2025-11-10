package javakanban.manager;

import javakanban.model.Epic;
import javakanban.model.Subtask;
import javakanban.model.Task;
import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Task updateTask(Task task);

    Task getTaskById(int id);

    Epic addEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic getEpicById(int id);

    List<Subtask> getSubtasksOfEpicById(int id);

    Subtask addSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    Subtask getSubtaskById(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getHistory();

    void delTasks();

    void delEpics();

    void delSubtasks();

    void delTaskById(int id);

    void delSubtaskById(int id);

    void delEpicById(int id);

    List<Task> getPrioritizedTasks();
}
