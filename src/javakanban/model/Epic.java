package javakanban.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.id, epic.name, epic.description, epic.status, epic.startTime, epic.duration);
        if (epic.subtasksId == null) {
            this.subtasksId = new ArrayList<>();
        } else {
            this.subtasksId = new ArrayList<>(epic.subtasksId);
        }
        this.endTime = epic.endTime;
    }

    public ArrayList<Integer> getSubtasksId() {
        if (this.subtasksId == null) {
           return new ArrayList<>();
        }
        return new ArrayList<>(this.subtasksId);
    }

    public void addSubtaskId(int id) {
        subtasksId.add(id);
    }

    public void removeSubtaskId(int id) {
        subtasksId.remove((Object) id);
    }

    public void removeAllSubtaskId() {
        subtasksId.clear();
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        if (endTime != null) {
            return Optional.of(endTime);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", startTime=" + (startTime != null ? startTime : "отсутствует") +
                ", endTime=" + (endTime != null ? endTime : "отсутствует") +
                ", duration=" + (duration != null ? duration.toMinutes() + "мин." : "отсутствует") +
                ", subtasksId=" + subtasksId +
                '}';
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
