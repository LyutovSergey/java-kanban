package javakanban.model;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }
    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.id, epic.name, epic.description, epic.status);
        this.subtasksId = new ArrayList<>(epic.subtasksId);
    }

    public ArrayList<Integer> getSubtasksId() {
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
    public String toString() {
        return "\nEpic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtasksId=" + subtasksId +
                '}';
    }
}
