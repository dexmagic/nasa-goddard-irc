package java.beans;

public class PropertyVetoException extends Exception{
    public PropertyVetoException() {
    }

    public PropertyVetoException(String message) {
        super(message);
    }

    public PropertyVetoException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyVetoException(Throwable cause) {
        super(cause);
    }

    public PropertyVetoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
