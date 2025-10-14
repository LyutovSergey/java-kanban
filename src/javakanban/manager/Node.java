package javakanban.manager;

public class Node <T>{
     Node<T> next;
     Node<T> previous;
     T task;

    public Node(T task) {
        this.task=task;
        next=null;
        previous=null;
    }
}
