package com.example.ProjectManagementBackend.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class IssueDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "depends_on_issue_id")
    private Issue dependsOnIssue;
}
