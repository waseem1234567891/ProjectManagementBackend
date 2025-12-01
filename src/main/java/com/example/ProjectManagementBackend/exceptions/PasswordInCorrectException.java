package com.example.ProjectManagementBackend.exceptions;

public class PasswordInCorrectException extends RuntimeException{

    public PasswordInCorrectException(String message)
    {
        super(message);
    }
}
