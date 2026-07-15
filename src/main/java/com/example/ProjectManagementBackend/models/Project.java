package com.example.ProjectManagementBackend.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    private String name;

    private String key; // WEB, API, CRM

    private String description;

    private UUID createdBy;

    @CreationTimestamp
    private Instant createdAt;
}
