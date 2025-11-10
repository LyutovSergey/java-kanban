package javakanban.model;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected Integer id; // Уникальный идентификационный номер
    protected String name;
    protected String description;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, Status status) {
        this.status = status;
        this.description = description;
        this.name = name;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.status = status;
        this.description = description;
        this.name = name;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.status = status;
        this.description = description;
        this.name = name;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.status = status;
        this.description = description;
        this.name = name;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Task task) { // на теории сообщили, что нужно помещать и возвращать копии объектов
        this.id = task.id;
        this.status = task.status;
        this.description = task.description;
        this.name = task.name;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (duration != null && startTime != null) {
            return Optional.of(startTime.plus(duration));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + (duration != null ? duration.toMinutes() + " мин." : "null") +
                '}';
    }

    public Optional<LocalDateTime> getStartTime() {
        if (startTime != null) {
            return Optional.of(startTime);
        } else {
            return Optional.empty();
        }

    }

    public Optional<Duration> getDuration() {
        if (duration != null) {
            return Optional.of(duration);
        } else {
            return Optional.empty();
        }
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
