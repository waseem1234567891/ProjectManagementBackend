package com.example.ProjectManagementBackend.controllers;


import com.example.ProjectManagementBackend.dto.comments.CommentDTO;
import com.example.ProjectManagementBackend.dto.comments.CommentRequest;
import com.example.ProjectManagementBackend.models.CommentAttachment;
import com.example.ProjectManagementBackend.models.Comments;
import com.example.ProjectManagementBackend.respositories.CommentAttachmentRepository;
import com.example.ProjectManagementBackend.services.CommentsService;
import jakarta.annotation.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/issues/{issueId}/comments")
public class CommentsController {
    private final CommentsService commentsService;
    private final CommentAttachmentRepository attachmentRepo;

    public CommentsController(CommentsService commentsService, CommentAttachmentRepository attachmentRepo) {
        this.commentsService = commentsService;
        this.attachmentRepo = attachmentRepo;
    }

    @GetMapping
    public List<CommentDTO> getAllCommentsByIssueId(@PathVariable UUID issueId) {
        return commentsService.getCommentsByIssue(issueId);
    }



    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable UUID commentId,
            @RequestBody CommentRequest request
    ) {
        Comments updated = commentsService.updateComment(commentId, request.getContent());
        CommentDTO dto = commentsService.mapToDTO(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId) {
        commentsService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCommentWithFiles(
            @PathVariable UUID issueId,
            @RequestParam UUID authorId,
            @RequestPart(value = "dto",required = false) CommentRequest dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Comments saved = commentsService.addCommentWithFiles(issueId, authorId, dto,files);

        CommentDTO commentDTO=new CommentDTO(saved);
        return ResponseEntity.ok(commentDTO);
    }

    @GetMapping("/attachments/{id}")
    public ResponseEntity<UrlResource> downloadFile(@PathVariable UUID id) {

        CommentAttachment attachment = attachmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        File file = new File(attachment.getFilePath());

        UrlResource resource;
        try {
            resource = new UrlResource(file.toURI());
        } catch (Exception e) {
            throw new RuntimeException("File not found");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .body(resource);
    }
}
