package de.sovity.edc.extension.custommessages.api;

public class SovityMessageException extends RuntimeException {
    public SovityMessageException(String message) {
        super(message);
    }

    public SovityMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public SovityMessageException(Throwable cause) {
        super(cause);
    }

    public SovityMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
