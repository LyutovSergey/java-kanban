package javakanban.model;

public class Subtask extends Task {
    private final int epicId; // Внешний ключ для Epic

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.id, subtask.name, subtask.description, subtask.status);
        this.epicId = subtask.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "\nSubtask{" +
                "epicId=" + epicId +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
