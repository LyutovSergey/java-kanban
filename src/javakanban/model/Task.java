package javakanban.model;

import java.util.Objects;

public class Task {
    private int id; // Уникальный идентификационный номер
    private String name;
    private String description;
    private Status status;

    public Task(int id, String name, String description) {
        this.description = description;
        this.name = name;
        this.id =id;
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
}
