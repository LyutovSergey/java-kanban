package javakanban.exceptions;

public class ManagerFileSaveException extends RuntimeException {
    public ManagerFileSaveException(String message, Exception e) {
        super(message, e);
    }
}
