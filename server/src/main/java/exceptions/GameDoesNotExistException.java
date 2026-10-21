package exceptions;

public class GameDoesNotExistException extends Exception {
    public GameDoesNotExistException(String message) {
        super(message);
    }
    public GameDoesNotExistException(String message, Throwable ex) {
        super(message, ex);
    }

}

