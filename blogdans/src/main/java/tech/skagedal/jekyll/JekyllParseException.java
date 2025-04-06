package tech.skagedal.jekyll;

public class JekyllParseException extends RuntimeException {
    public JekyllParseException(String message) {
        super(message);
    }

    public JekyllParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JekyllParseException(Throwable cause) {
        super(cause);
    }
}
