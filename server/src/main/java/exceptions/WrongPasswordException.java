package exceptions;

public class WrongPasswordException extends Exception {
    public WrongPasswordException(String message) {
        super(message);
    }
    public WrongPasswordException(String message, Throwable ex) {
        super(message, ex);
    }

}
