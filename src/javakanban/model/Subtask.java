package javakanban.model;

public class Subtask extends Task{
    private int epicID; // Внешний ключ для Epic

    public Subtask(int id,String name, String description, int epicID) {
        super(id, name, description);
        this.epicID = epicID;
}
}
