package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.CommentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentAttachmentRepository extends JpaRepository<CommentAttachment, UUID> {
    List<CommentAttachment> findByCommentId(UUID id);
}
