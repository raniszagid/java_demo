package ru.t1.java.demo.exception;

import org.springframework.http.HttpStatus;

public class ClientException extends RuntimeException {
    public ClientException() {
        super("There is no entity with current id");
    }

    public ClientException(Long id) {
        super(String.format("There is no entity with id %d", id));
    }

    public ClientException(String accountType) {
        super("Wrong value of account type: " + accountType);
    }
}
