package javakanban.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId; // Внешний ключ для Epic

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.id, subtask.name, subtask.description, subtask.status, subtask.startTime, subtask.duration);
        this.epicId = subtask.epicId;
    }

    public Subtask( String name, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super( name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTime +
                ", duration=" + (duration != null ? duration.toMinutes() + "мин." : "null") +
                '}';
    }
}
