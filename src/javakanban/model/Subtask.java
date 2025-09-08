package javakanban.model;

public class Subtask extends Task{
    private int epicId; // Внешний ключ для Epic

    public Subtask(String name, String description) {
        super( name, description);

}

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicID) {
        this.epicId = epicID;
    }

    @Override
    public String toString() {
        return "\nSubtask{" +
                "epicId=" + epicId  +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
