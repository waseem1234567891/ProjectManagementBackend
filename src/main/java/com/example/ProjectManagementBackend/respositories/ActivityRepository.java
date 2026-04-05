package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
    List<Activity> findTop5ByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);

}
