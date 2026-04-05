package com.example.ProjectManagementBackend.dto.comments;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public class CommentRequest {

    private String content;

    private List<UUID> mentionedUserIds;



    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<UUID> getMentionedUserIds() {
        return mentionedUserIds;
    }

    public void setMentionedUserIds(List<UUID> mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }


}
