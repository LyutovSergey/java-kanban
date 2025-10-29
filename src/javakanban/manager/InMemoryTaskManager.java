package javakanban.manager;
import java.util.HashMap;
import javakanban.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyOfTaskManager =  Managers.getDefaultHistory();
    private int currentId = 0;

    protected void putTask(Task task) {
        Task newTask = new Task(task);
        tasks.put(newTask.getId(), newTask);
        updateId(task);
    }

    protected void putEpic(Epic epic) {
        Epic newEpic = new Epic(epic);
        epics.put(newEpic.getId(), newEpic);
        updateId(epic);
    }

    protected void putSubtask(Subtask subtask) { //
        Subtask newSubtask = new Subtask(subtask);
        subtasks.put(newSubtask.getId(), newSubtask);
        if (epics.containsKey(newSubtask.getEpicId())) {
            epics.get(newSubtask.getEpicId()).addSubtaskId(newSubtask.getId());
        }
        updateId(subtask);
    }

    private void updateId(Task task) {
        if (currentId<task.getId()) {
            currentId=task.getId();
        }
    }

    private int getNextId() {
        currentId++;
        return currentId;
    }

    @Override
    public Task addTask(Task task) {
        task.setId(getNextId());
        Task newTask = new Task(task); // на теории сообщили, что нужно помещать и возвращать копии объектов
        tasks.put(newTask.getId(), newTask);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }
        Task newTask = new Task(task);
        tasks.replace(newTask.getId(), newTask);
        return task;
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) return null;
        historyOfTaskManager.add(tasks.get(id));
        return new Task(tasks.get(id));
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(getNextId());
        Epic newEpic = new Epic(epic);
        epics.put(newEpic.getId(), newEpic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return null;
        }
        Epic linkToEpic = epics.get(epic.getId());
        linkToEpic.setName(epic.getName());
        linkToEpic.setDescription(epic.getDescription());
        return epic;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        historyOfTaskManager.add(epics.get(id));
        return new Epic(epics.get(id));
    }

    @Override
    public List<Subtask> getSubtasksOfEpicById(int id) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        if (epics.containsKey(id)) {
            for (int idSubtask : epics.get(id).getSubtasksId()) {
                subtasksOfEpic.add(subtasks.get(idSubtask));
            }
        }
        return subtasksOfEpic;
    }

    @Override
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

    @Override
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

    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return null;
        }
        historyOfTaskManager.add(subtasks.get(id));
        return new Subtask(subtasks.get(id));
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void delTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyOfTaskManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void delEpics() {
        for (Integer epicId : epics.keySet()) {
            historyOfTaskManager.remove(epicId);
        }
        epics.clear();

        for (Integer subtaskId : subtasks.keySet()) {
            historyOfTaskManager.remove(subtaskId);
        }
        subtasks.clear(); // subtasks без epics существовать не может
    }

    @Override
    public void delSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyOfTaskManager.remove(subtaskId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtaskId(); // Удаление привязанных subtask
            calculateStatusEpicById(epic.getId());
        }

    }

    protected void calculateStatusEpicById(int id) {
        int countStatusNEW = 0;
        int countStatusINPROGRESS = 0;
        int countStatusDONE = 0;
        Epic epic = epics.get(id);
        for (int findId : epic.getSubtasksId()) {
            switch (subtasks.get(findId).getStatus()) {
                case Status.NEW:
                    countStatusNEW++;
                    break;
                case Status.IN_PROGRESS:
                    countStatusINPROGRESS++;
                    break;
                case Status.DONE:
                    countStatusDONE++;
            }
        }
        if (countStatusINPROGRESS == 0 && countStatusDONE == 0) {
            epic.setStatus(Status.NEW);
        } else if (countStatusNEW == 0 && countStatusINPROGRESS == 0 && countStatusDONE > 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void delTaskById(int id) {
        historyOfTaskManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void delSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            historyOfTaskManager.remove(id);
            subtasks.remove(id);
            epics.get(epicId).removeSubtaskId(id);
            calculateStatusEpicById(epicId);
        }
    }

    @Override
    public void delEpicById(int id) {
        if (epics.containsKey(id)) {
            for (int idSubtask : epics.get(id).getSubtasksId()) {
                historyOfTaskManager.remove(idSubtask);
                subtasks.remove(idSubtask);
            }
            historyOfTaskManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyOfTaskManager.getHistory();
    }



}
