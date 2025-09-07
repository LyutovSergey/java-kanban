package javakanban.manager;
import java.util.HashMap;
import javakanban.model.*;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int currentId=0;

    public int getNextId() {
        currentId++;
        return currentId;
    }

    public Task addTask(Task task){
        task.setId(getNextId());
        tasks.put(getNextId(), task);
        return task;
    }

    public Epic addEpic(Epic epic){
        epic.setId(getNextId());
        epics.put(getNextId(), epic);
        return epic;
    }
    public Subtask addSubtask(Subtask subtask){
        subtask.setId(getNextId());
        subtasks.put(getNextId(), subtask);
        return subtask;
    }

    public Subtask addSubtask(Subtask subtask){
        subtask.setId(getNextId());
        subtasks.put(getNextId(), subtask);
        return subtask;
    }




}
