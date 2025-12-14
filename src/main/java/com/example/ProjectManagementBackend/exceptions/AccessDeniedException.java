package com.example.ProjectManagementBackend.exceptions;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message)
    {
        super(message);
    }
}
