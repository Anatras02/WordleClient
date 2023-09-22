package it.unipi.lab3.abalderi1.api.exceptions;

public class ApiException extends Exception {
    String status;

    public ApiException(String status, String message) {
        super(message);

        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
