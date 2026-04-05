package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.comments.CommentAttachmentDTO;
import com.example.ProjectManagementBackend.dto.comments.CommentDTO;
import com.example.ProjectManagementBackend.dto.comments.CommentRequest;
import com.example.ProjectManagementBackend.exceptions.ResourceNotFoundException;
import com.example.ProjectManagementBackend.models.CommentAttachment;
import com.example.ProjectManagementBackend.models.Comments;
import com.example.ProjectManagementBackend.models.Issue;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.respositories.CommentAttachmentRepository;
import com.example.ProjectManagementBackend.respositories.CommentsRepository;
import com.example.ProjectManagementBackend.respositories.IssueRepo;
import com.example.ProjectManagementBackend.respositories.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final IssueRepo issueRepo;
    private final UserRepo userRepo;
    private final CommentAttachmentRepository attachmentRepo;

    public CommentsService(CommentsRepository commentsRepository, IssueRepo issueRepo, UserRepo userRepo, CommentAttachmentRepository attachmentRepo) {
        this.commentsRepository = commentsRepository;
        this.issueRepo = issueRepo;
        this.userRepo = userRepo;
        this.attachmentRepo = attachmentRepo;
    }

    public List<CommentDTO> getCommentsByIssue(UUID issueId) {
        List<Comments> comments = commentsRepository
                .findByIssueIdWithMentions(issueId);
        return comments.stream()
                .map(this::mapToDTO) // ✅ map each comment
                .toList();
    }

    public Comments addComment(UUID issueId, UUID authorId, String content) {
        Comments comment = new Comments();
        Optional<Issue> byId = issueRepo.findById(issueId);
        if (byId.isPresent()) {
            comment.setIssue(byId.get());
            Optional<User> optionalUser = userRepo.findById(authorId);
            if (optionalUser.isPresent()) {
                comment.setAuthor(optionalUser.get());
            } else {
                throw new UsernameNotFoundException("User not found");
            }

            comment.setContent(content);
            comment.setCreatedAt(Instant.now());
        }else {
            throw new ResourceNotFoundException("issue not found");
        }
        return commentsRepository.save(comment);

    }

    public Comments updateComment(UUID commentId, String content) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        comment.setContent(content);
        comment.setUpdatedAt(Instant.now());
        return commentsRepository.save(comment);
    }

    public void deleteComment(UUID commentId) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        commentsRepository.delete(comment);
    }

    public Comments addCommentWithFiles(UUID issueId, UUID authorId, CommentRequest dto,List<MultipartFile> files) {

        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        User user = userRepo.findById(authorId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comments comment = new Comments();
        comment.setIssue(issue);
        comment.setAuthor(user);
        comment.setContent(dto.getContent());
        if (dto.getMentionedUserIds() != null && !dto.getMentionedUserIds().isEmpty()) {

            List<User> mentionedUsers = userRepo.findAllById(dto.getMentionedUserIds());

            if (mentionedUsers.size() != dto.getMentionedUserIds().size()) {
                throw new ResourceNotFoundException("One or more mentioned users not found");
            }

            comment.setMentionedUsers(mentionedUsers);
        }
        comment.setCreatedAt(Instant.now());

        Comments saved = commentsRepository.save(comment);

        // ✅ Define upload directory (absolute path is safer)
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // ✅ ensure folder exists
        }

        // ✅ Handle files safely
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {

                if (file.isEmpty()) continue; // skip empty files

                String originalName = file.getOriginalFilename();
                String fileName = UUID.randomUUID() + "_" + originalName;
                String filePath = uploadDir + fileName;

                try {
                    file.transferTo(new File(filePath));
                } catch (Exception e) {
                    e.printStackTrace(); // ✅ log real issue
                    throw new RuntimeException("File upload failed: " + e.getMessage());
                }

                CommentAttachment attachment = new CommentAttachment();
                attachment.setFileName(originalName);
                attachment.setFileType(file.getContentType());
                attachment.setFilePath(filePath);
                attachment.setComment(saved);

                attachmentRepo.save(attachment);
            }
        }

        // ✅ reload or fetch attachments to include in returned object
        List<CommentAttachment> savedAttachments = attachmentRepo.findByCommentId(saved.getId());
        saved.setAttachments(savedAttachments);

        return saved;
    }

    public CommentDTO mapToDTO(Comments comment) {

        List<CommentAttachmentDTO> attachments = comment.getAttachments()
                .stream()
                .map(att -> new CommentAttachmentDTO(
                        att.getId(),
                        att.getFileName(),
                        att.getFileType()
                ))
                .toList();
        List<UUID> mensionUserlist = comment.getMentionedUsers().stream().map(user -> user.getId()).toList();

        return new CommentDTO(
                comment.getId(),
                comment.getIssue().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getFirstName(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                attachments ,mensionUserlist
        );
    }
}
