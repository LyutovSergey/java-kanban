package javakanban.data;

import java.util.Objects;

public class Task {
    private int idTask; // Уникальный идентификационный номер задачи
    private String name;
    private String description;
    private Status status;
    private static int counterTask=0; // Счетчик и уникальный идентификатор


    public Task(String name, String description) {
        this.description = description;
        this.name = name;
        counterTask++; // Счетчик и уникальный идентификатор
        idTask=counterTask; // Назначаем уникальный идентификатор
        status=Status.NEW;
    }

    public Task(String description) {
        this.description = description;
        counterTask++;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idTask == task.idTask;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idTask);
    }
}
