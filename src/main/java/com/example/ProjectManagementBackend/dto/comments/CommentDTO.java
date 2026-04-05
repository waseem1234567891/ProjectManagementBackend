package com.example.ProjectManagementBackend.dto.comments;

import com.example.ProjectManagementBackend.models.CommentAttachment;
import com.example.ProjectManagementBackend.models.Comments;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CommentDTO {
   private UUID id;
   private  UUID issueId;
   private UUID authorId;
   private String authorName;
   private String content;
   private Instant createdAt;
   private Instant updatedAt;
    private List<UUID> mentionedUserIds;

    private List<CommentAttachmentDTO> attachments;

    public CommentDTO(UUID id, UUID issueId, UUID authorId, String authorName, String content, Instant createdAt,Instant updatedAt,List<CommentAttachmentDTO> attachments,List<UUID> mentionedUserIds) {
        this.id=id;
        this.issueId=issueId;
        this.authorId=authorId;
        this.authorName=authorName;
        this.content=content;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
        this.attachments = attachments;
        this.mentionedUserIds=mentionedUserIds;
    }

    public CommentDTO() {

    }

    public CommentDTO(Comments saved) {
        this.id=saved.getId();
        this.content=saved.getContent();
        this.authorName=saved.getAuthor().getFirstName();
        this.issueId=saved.getIssue().getId();
        this.createdAt=saved.getCreatedAt();
        this.updatedAt=saved.getUpdatedAt();
        this.authorId=saved.getAuthor().getId();
        List<CommentAttachment> attachments1 = saved.getAttachments();
        if (attachments1!=null)
        {
            this.attachments=attachments1.stream().map(c->new CommentAttachmentDTO(c.getId(),c.getFileName(),c.getFileType())).toList();
        }
        List<UUID> mentionedUserIds=saved.getMentionedUsers().stream().map(user -> user.getId()).toList();
        if (mentionedUserIds!=null)
        {
            this.mentionedUserIds=mentionedUserIds;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CommentAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<CommentAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public List<UUID> getMentionedUserIds() {
        return mentionedUserIds;
    }

    public void setMentionedUserIds(List<UUID> mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }
}
