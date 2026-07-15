package com.example.ProjectManagementBackend.dto.reports;

public class SummaryDto {
    private long totalIssues;
    private long completed;
    private long inProgress;
    private long todo;
    private double completionRate;

    public SummaryDto() {
    }

    public SummaryDto(long totalIssues, long completed, long inProgress, long todo, double completionRate) {
        this.totalIssues = totalIssues;
        this.completed = completed;
        this.inProgress = inProgress;
        this.todo = todo;
        this.completionRate = completionRate;
    }

    public long getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(long totalIssues) {
        this.totalIssues = totalIssues;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getTodo() {
        return todo;
    }

    public void setTodo(long todo) {
        this.todo = todo;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }
}
