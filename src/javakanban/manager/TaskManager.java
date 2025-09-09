package javakanban.manager;
import java.util.HashMap;
import javakanban.model.*;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int currentId = 0;

    protected int getNextId() {
        currentId++;
        return currentId;
    }

    public Task addTask(Task task) {
        task.setId(getNextId());
        Task newTask = new Task(task); // на теории сообщили, что нужно помещать и возвращать копии объектов
        tasks.put(newTask.getId(), newTask);
        return task;
    }

    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }
        Task newTask = new Task(task);
        tasks.replace(newTask.getId(), newTask);
        return task;
    }

    public Task getTaskById(int id) {
        return new Task(tasks.get(id));
    }

    public Epic addEpic(Epic epic) {
        epic.setId(getNextId());
        Epic newEpic = new Epic(epic);
        epics.put(newEpic.getId(), newEpic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return null;
        }
        Epic linkToEpic = epics.get(epic.getId());
        linkToEpic.setName(epic.getName());
        linkToEpic.setDescription(epic.getDescription());
        return epic;
    }

    public Epic getEpicById(int id) {
        return new Epic(epics.get(id));
    }

    public ArrayList<Subtask> getSubtasksOfEpicById(int id ) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        if (epics.containsKey(id)) {
            for (int idSubtask : epics.get(id).getSubtasksId()) {
                subtasksOfEpic.add(subtasks.get(idSubtask));
            }
        }
        return subtasksOfEpic;
    }

    public Subtask addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }
        subtask.setId(getNextId());
        Epic linkToEpic = epics.get(subtask.getEpicId());
        linkToEpic.addSubtaskId(subtask.getId());
        Subtask newSubtask = new Subtask(subtask);
        subtasks.put(newSubtask.getId(), newSubtask);
        calculateStatusEpicById(subtask.getEpicId());
        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())
                || subtask.getEpicId() != subtasks.get(subtask.getId()).getEpicId()) {
            return null;
        }
        Subtask newSubtask = new Subtask(subtask);
        subtasks.replace(newSubtask.getId(), newSubtask);
        calculateStatusEpicById(newSubtask.getEpicId());
        return subtask;
    }

    public Subtask getSubtaskById(int id) {
        return new Subtask(subtasks.get(id));
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
        for (Epic epic : epics.values()) {
            epic.removeAllSubtaskId(); // Удаление привязанных subtask
            calculateStatusEpicById(epic.getId());
        }

    }

    protected void calculateStatusEpicById(int id) {
        int countStatusNEW = 0;
        int countStatusIN_PROGRESS = 0;
        int countStatusDONE = 0;
        Epic epic = epics.get(id);
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
        if (countStatusIN_PROGRESS == 0 && countStatusDONE == 0) {
            epic.setStatus(Status.NEW);
        } else if (countStatusNEW == 0 && countStatusIN_PROGRESS == 0 && countStatusDONE > 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void delTaskById(int id) {
        tasks.remove(id);
    }

    public void delSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            epics.get(epicId).removeSubtaskId(id);
            calculateStatusEpicById(epicId);
        }
    }

    public void delEpicById(int id) {
        if (epics.containsKey(id)) {
            for (int idSubtask : epics.get(id).getSubtasksId()) {
                subtasks.remove(idSubtask);
            }
            epics.remove(id);
        }
    }
}
