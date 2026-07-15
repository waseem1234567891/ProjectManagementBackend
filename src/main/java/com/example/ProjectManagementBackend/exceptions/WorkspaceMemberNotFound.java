package com.example.ProjectManagementBackend.exceptions;

public class WorkspaceMemberNotFound extends RuntimeException{
    public WorkspaceMemberNotFound(String message)
    {
        super(message);
    }
}
