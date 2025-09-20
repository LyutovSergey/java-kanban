package javakanban.manager;
import javakanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewingHistory = new ArrayList<>(10);
    protected final static int MAX_SIZE_OF_HISTORY = 10;

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (viewingHistory.size() < MAX_SIZE_OF_HISTORY) {
            viewingHistory.addLast(new Task(task));
        } else {
            viewingHistory.removeFirst();
            viewingHistory.addLast(new Task(task));
        }
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(viewingHistory);
    }

}
