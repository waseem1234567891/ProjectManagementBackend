package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.models.Activity;
import com.example.ProjectManagementBackend.services.ActivityService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{workspaceId}/activity")
    public List<Activity> getActivity(@PathVariable UUID workspaceId) {
        return activityService.getWorkspaceActivity(workspaceId);
    }
}
