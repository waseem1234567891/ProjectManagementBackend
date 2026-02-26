package com.example.ProjectManagementBackend.exceptions;

public class WorkspaceNameAlreadyExistException extends RuntimeException{
    public WorkspaceNameAlreadyExistException(String message)
    {
        super(message);
    }
}
