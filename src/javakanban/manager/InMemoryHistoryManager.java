package javakanban.manager;
import javakanban.model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected final static int MAX_SIZE_OF_HISTORY = 10;
    private final List<Task> viewingHistory = new ArrayList<>(MAX_SIZE_OF_HISTORY);

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (viewingHistory.size() == MAX_SIZE_OF_HISTORY) {
            viewingHistory.removeFirst();
        }
        viewingHistory.addLast(new Task(task));
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(viewingHistory);
    }
}
