package javakanban.manager;
import javakanban.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node<T> {
        private Node<T> next;
        private Node<T> previous;
        private final T task;

         Node(T task) {
            this.task = task;
            next = null;
            previous = null;
        }
    }

    private Node<Task> first;
    private Node<Task> last;
    private final Map<Integer, Node<Task>> viewingHistory = new HashMap<>();


    @Override
    public void add(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (this.viewingHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
        if (this.viewingHistory.isEmpty()) {
            this.first = newNode;
            this.last = newNode;
            viewingHistory.put(task.getId(), newNode);
            return;
        }
            newNode.previous = this.last;
            newNode.previous.next = newNode;
            this.last = newNode;
            viewingHistory.put(task.getId(), newNode);
        }

    @Override
    public void remove(int id) {
        Node<Task> node = viewingHistory.get(id);
        if (node == null) {
            return; // Задача не найдена
        }
        if (node == this.first) {
            this.first = node.next;
            if (this.first != null) {
                this.first.previous = null;
            } else {
                this.last = null;
            }
        } else if (node == this.last) {
            this.last = node.previous;
            this.last.next = null;
        } else {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
        viewingHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {

        List<Task> tasksList = new ArrayList<>();
        Node<Task> currentNode = first;
        while (currentNode != null) {
            tasksList.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasksList;
    }

}