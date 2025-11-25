package javakanban.manager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javakanban.exceptions.InMemoryTaskManagerException;
import javakanban.model.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyOfTaskManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(
            task -> task.getStartTime().get()
    ));
    private int currentId = 0;

    protected void putTask(Task task) {
        if (isOverlapOfTimeTasks(task)) {
            throw new InMemoryTaskManagerException("Добавляемая задача пересекается по времени с другими");
        }
        Task newTask = new Task(task);
        tasks.put(newTask.getId(), newTask);
        updateId(task);
        addToPrioritizedTasks(task);
    }

    protected void putEpic(Epic epic) {
        Epic newEpic = new Epic(epic);
        epics.put(newEpic.getId(), newEpic);
        updateId(epic);
    }

    protected void putSubtask(Subtask subtask) { //
        if (isOverlapOfTimeTasks(subtask)) {
            throw new InMemoryTaskManagerException("Добавляемая задача пересекается по времени с другими");
        }
        Subtask newSubtask = new Subtask(subtask);
        subtasks.put(newSubtask.getId(), newSubtask);
        if (epics.containsKey(newSubtask.getEpicId())) {
            epics.get(newSubtask.getEpicId()).addSubtaskId(newSubtask.getId());
            calculateTimeEpicById(newSubtask.getEpicId());
        }
        updateId(subtask);

        addToPrioritizedTasks(newSubtask);
    }

    private void updateId(Task task) {
        if (currentId < task.getId()) {
            currentId = task.getId();
        }
    }

    private int getNextId() {
        currentId++;
        return currentId;
    }

    @Override
    public Task addTask(Task task) {
        if (isOverlapOfTimeTasks(task)) {
                 throw new InMemoryTaskManagerException("Добавляемая задача пересекается по времени с другими");
              }
        task.setId(getNextId());
        Task newTask = new Task(task); // на теории сообщили, что нужно помещать и возвращать копии объектов
        tasks.put(newTask.getId(), newTask);
        addToPrioritizedTasks(newTask);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }
        if (isOverlapOfTimeTasks(task)) {
            throw new InMemoryTaskManagerException("Добавляемая задача пересекается по времени с другими");
        }
        Task newTask = new Task(task);
        delFromPrioritizedTasks(tasks.get(task.getId()));
        tasks.replace(newTask.getId(), newTask);
        addToPrioritizedTasks(newTask);
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
        epic.setStatus(Status.NEW);
        Epic newEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), Status.NEW);
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
            subtasksOfEpic = epics.get(id).getSubtasksId().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());
            return subtasksOfEpic;
        }
        return null;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }
        if (isOverlapOfTimeTasks(subtask)) {
            throw new InMemoryTaskManagerException("Добавляемая задача пересекается по времени с другими");
        }
        subtask.setId(getNextId());
        Epic linkToEpic = epics.get(subtask.getEpicId());
        linkToEpic.addSubtaskId(subtask.getId());
        Subtask newSubtask = new Subtask(subtask);
        subtasks.put(newSubtask.getId(), newSubtask);
        calculateStatusEpicById(subtask.getEpicId());
        calculateTimeEpicById(subtask.getEpicId());
        addToPrioritizedTasks(newSubtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())
                || subtask.getEpicId() != subtasks.get(subtask.getId()).getEpicId()) {
            return null;
        }
        if (isOverlapOfTimeTasks(subtask)) {
            throw new InMemoryTaskManagerException("Добавляемая задача пересекается по времени с другими");
        }
        Subtask newSubtask = new Subtask(subtask);
        delFromPrioritizedTasks(subtasks.get(subtask.getId()));
        subtasks.replace(newSubtask.getId(), newSubtask);
        calculateStatusEpicById(newSubtask.getEpicId());
        calculateTimeEpicById(newSubtask.getEpicId());
        addToPrioritizedTasks(newSubtask);
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
        tasks.keySet().stream()
                .peek(historyOfTaskManager::remove)
                .forEach(key -> delFromPrioritizedTasks(tasks.get(key)));

        tasks.clear();
    }

    @Override
    public void delEpics() {
        subtasks.values().stream() // subtasks без epics существовать не может и по нему проще пройтись в потоке
                .peek(subtask -> historyOfTaskManager.remove(subtask.getEpicId()))
                .peek(subtask -> historyOfTaskManager.remove(subtask.getId()))
                .forEach(this::delFromPrioritizedTasks);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void delSubtasks() {
        subtasks.values().stream()
                .peek(subtask -> historyOfTaskManager.remove(subtask.getId()))
                .forEach(this::delFromPrioritizedTasks);

        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.removeAllSubtaskId();
            calculateStatusEpicById(epic.getId());
            calculateTimeEpicById(epic.getId());
        });
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
        if (!tasks.containsKey(id)) {
            return;
        }
        delFromPrioritizedTasks(tasks.get(id));
        historyOfTaskManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void delSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            historyOfTaskManager.remove(id);
            delFromPrioritizedTasks(subtasks.get(id));
            subtasks.remove(id);
            epics.get(epicId).removeSubtaskId(id);
            calculateStatusEpicById(epicId);
            calculateTimeEpicById(epicId);

        }
    }

    @Override
    public void delEpicById(int id) {
        if (epics.containsKey(id)) {

            epics.get(id).getSubtasksId().stream()
                    .peek(historyOfTaskManager::remove)
                    .peek(subtaskId -> delFromPrioritizedTasks(subtasks.get(subtaskId)))
                    .forEach(subtasks::remove);
            historyOfTaskManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyOfTaskManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isOverlapOfTimeTasks(Task task) {
        if (task.getStartTime().isEmpty() || task.getEndTime().isEmpty()) {
            return false;
        }
        if (prioritizedTasks.isEmpty()) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(prioritizedTask -> !Objects.equals(prioritizedTask.getId(), task.getId()))
                .anyMatch(prioritizedTask -> (
                        prioritizedTask.getStartTime().get().isBefore(task.getEndTime().get())
                                && prioritizedTask.getEndTime().get().isAfter(task.getStartTime().get())
                ));
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime().isEmpty() || task.getEndTime().isEmpty()) {
            return;
        }
        prioritizedTasks.add(task);
    }

    private void delFromPrioritizedTasks(Task task) {
        if (task != null && task.getStartTime().isPresent()) {
            prioritizedTasks.remove(task);
        }
    }

    protected void calculateTimeEpicById(int id) {

        Epic epic = epics.get(id);
        Optional<LocalDateTime> startTime = epic.getSubtasksId().stream()
                .map(idSubtask -> subtasks.get(idSubtask).getStartTime())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(Comparator.naturalOrder());
        startTime.ifPresent(epic::setStartTime);

        Optional<LocalDateTime> endTime = epic.getSubtasksId().stream()
                .map(idSubtask -> subtasks.get(idSubtask).getEndTime())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.naturalOrder());
        endTime.ifPresent(epic::setEndTime);

        Duration duration = epic.getSubtasksId().stream()
                .map(idSubtask -> subtasks.get(idSubtask).getDuration())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(Duration.ZERO, Duration::plus);

        if (!duration.isZero()) {
            epic.setDuration(duration);
        }
    }
}

