package com.example.ProjectManagementBackend.dto.reports;

import java.util.UUID;

public class MemberPerformanceDto {
    private UUID memberId;
    private String memberName;
    private Long completedTasks;

    public MemberPerformanceDto() {
    }

    public MemberPerformanceDto(UUID memberId, String memberName, Long completedTasks) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.completedTasks = completedTasks;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Long completedTasks) {
        this.completedTasks = completedTasks;
    }
}
