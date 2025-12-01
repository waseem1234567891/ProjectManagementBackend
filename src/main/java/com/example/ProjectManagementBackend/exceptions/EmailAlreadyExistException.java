package com.example.ProjectManagementBackend.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message)
    {
        super(message);
    }
}
