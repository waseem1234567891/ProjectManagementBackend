package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentsRepository extends JpaRepository<Comments, UUID> {
    List<Comments> findByIssueIdOrderByCreatedAtAsc(UUID issueId);
    @Query("SELECT c FROM Comments c LEFT JOIN FETCH c.mentionedUsers WHERE c.issue.id = :issueId ORDER BY c.createdAt ASC")
    List<Comments> findByIssueIdWithMentions(UUID issueId);
}
