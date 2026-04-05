package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.models.Activity;
import com.example.ProjectManagementBackend.respositories.ActivityRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    private final ActivityRepository activityRepo;

    public ActivityService(ActivityRepository activityRepo) {
        this.activityRepo = activityRepo;
    }

    // ✅ Create activity
    public void logActivity(String type, String message, UUID workspaceId, UUID issueId) {
        Activity activity = new Activity();
        activity.setType(type);
        activity.setMessage(message);
        activity.setWorkspaceId(workspaceId);
        activity.setIssueId(issueId);

        activityRepo.save(activity);
    }

    // ✅ Fetch activity
    public List<Activity> getWorkspaceActivity(UUID workspaceId) {
        return activityRepo.findTop5ByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
    }
}
