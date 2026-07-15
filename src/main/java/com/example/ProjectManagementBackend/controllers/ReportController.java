package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.reports.WorkspaceReportDto;
import com.example.ProjectManagementBackend.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<WorkspaceReportDto>
    getWorkspaceReport(@PathVariable UUID workspaceId)
    {
        return ResponseEntity.ok(
                reportService.generateReport(
                        workspaceId
                )
        );
    }
}
