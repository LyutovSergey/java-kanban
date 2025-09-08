package javakanban.manager;
import java.util.HashMap;
import javakanban.model.*;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int currentId = 0;

    public int getNextId() {
        currentId++;
        return currentId;
    }

    public Task addTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }
        tasks.replace(task.getId(), task);
        return task;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }



    public Epic addEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return null;
        }
        epics.replace(epic.getId(), epic);
        calculateEpicByIDStatus(epic.getId());
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }
        int id = getNextId();
        Epic epic = epics.get(subtask.getEpicId());

        epic.addSubtaskId(id);
        subtask.setId(id);
        epics.replace(subtask.getEpicId(), epic);

        subtasks.put(id, subtask);
        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return null;
        }
        subtasks.replace(subtask.getId(), subtask);
        calculateEpicByIDStatus(subtask.getEpicId());
        return subtask;
    }


    public ArrayList<Task> getTasks() {

        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void delTasks() {
        tasks.clear();
    }

    public void delEpics() {
        epics.clear();
        subtasks.clear(); // subtasks без epics существовать не может
    }

    public void delSubtasks() {
        subtasks.clear();
        for (int id : epics.keySet()) {
            epics.get(id).removeAllSubtaskId(); // Удаление привязанных subtask
            calculateEpicByIDStatus(id);
        }

    }

    public void calculateEpicByIDStatus(int id) {
        int countStatusNEW = 0;
        int countStatusIN_PROGRESS = 0;
        int countStatusDONE = 0;
        Epic epic=epics.get(id);
        for (int findId : epic.getSubtasksId()) {
            switch (subtasks.get(findId).getStatus()) {
                case Status.NEW:
                    countStatusNEW++;
                    break;
                case Status.IN_PROGRESS:
                    countStatusIN_PROGRESS++;
                    break;
                case Status.DONE:
                    countStatusDONE++;
            }
        }
        if (countStatusIN_PROGRESS==0 && countStatusDONE==0) {
            epic.setStatus(Status.NEW);
            return;
        }
        else if (countStatusNEW==0 && countStatusIN_PROGRESS==0 && countStatusDONE>0) {
            epic.setStatus(Status.DONE);
            return;
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }


    public void delTaskById(int id) {
        tasks.remove(id);
    }

    public void delSubtaskById(int id) {
        int epicId=subtasks.get(id).getEpicId();
        subtasks.remove(id);
        epics.get(epicId).removeSubtaskId(id);
        calculateEpicByIDStatus(epicId);
    }

    public void delEpicById(int id) {
        if (epics.containsKey(id)) {
            epics.remove(id);
            for (int findId : subtasks.keySet()) { // Удаление привязанных subtask
                if (subtasks.get(findId).getEpicId() == id) {
                    subtasks.remove(findId);
                }
            }
        }
    }


}
