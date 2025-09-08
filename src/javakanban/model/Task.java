package javakanban.model;
import java.util.Objects;

public class Task {
    private int id; // Уникальный идентификационный номер
    private String name;
    private String description;
    private Status status;

    public Task( String name, String description) {
        this.description = description;
        this.name = name;
        this.status =Status.NEW;
    }

    public Task(int id, String name, String description,Status status) {
        this.id = id;
        this.status = status;
        this.description = description;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "\nTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                "}";
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
}
