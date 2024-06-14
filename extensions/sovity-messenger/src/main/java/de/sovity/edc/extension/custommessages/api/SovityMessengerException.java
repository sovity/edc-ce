package de.sovity.edc.extension.custommessages.api;

public class SovityMessengerException extends RuntimeException {
    public SovityMessengerException(String message) {
        super(message);
    }

    public SovityMessengerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SovityMessengerException(Throwable cause) {
        super(cause);
    }

    public SovityMessengerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
