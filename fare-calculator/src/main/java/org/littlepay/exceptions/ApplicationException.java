package org.littlepay.exceptions;

public class ApplicationException extends Exception {
    public ApplicationException() {
        super(); // Calls the default constructor of the parent Exception class
    }

    public ApplicationException(String message) {
        super(message); // Calls the constructor of the parent Exception class with a message
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause); // Calls the constructor with a message and a cause
    }

    public ApplicationException(Throwable cause) {
        super(cause); // Calls the constructor with a cause
    }
}
