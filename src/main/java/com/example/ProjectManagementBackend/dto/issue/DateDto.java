package com.example.ProjectManagementBackend.dto.issue;

import java.time.LocalDate;

public class DateDto {
    private LocalDate dueDate;

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
