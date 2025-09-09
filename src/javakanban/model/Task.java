package javakanban.model;
import java.util.Objects;

public class Task {
    protected int id; // Уникальный идентификационный номер
    protected String name;
    protected String description;
    protected Status status;

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

    public Task(Task task) { // на теории сообщили, что нужно помещать и возвращать копии объектов
        this.id = task.id;
        this.status = task.status;
        this.description = task.description;
        this.name = task.name;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
