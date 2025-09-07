package javakanban.model;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private List<Integer> subtasksId;



    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtaskId() {
        return subtasksId;
    }

    public  void addSubtaskId(int id) {
        subtasksId.add(id);
    }
}
