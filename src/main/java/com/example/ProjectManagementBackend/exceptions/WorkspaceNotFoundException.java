package com.example.ProjectManagementBackend.exceptions;

public class WorkspaceNotFoundException extends RuntimeException{
    public WorkspaceNotFoundException(String message)
    {
        super(message);
    }
}
