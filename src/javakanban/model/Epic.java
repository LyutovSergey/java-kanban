package javakanban.model;
import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> subtasksId;


    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
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
