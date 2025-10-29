package javakanban.exceptions;

public class ManagerFileLoadException extends RuntimeException {

    public ManagerFileLoadException(String message, Exception e) {
        super(message, e);
    }
}
