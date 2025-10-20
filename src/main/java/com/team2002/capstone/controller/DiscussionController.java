package com.team2002.capstone.controller;

import com.team2002.capstone.dto.*;
import com.team2002.capstone.service.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiscussionController {
    private final DiscussionService discussionService;

    @Operation(summary = "토론 주제 생성")
    @PostMapping("/groups/{groupId}/discussions")
    public ResponseEntity<DiscussionResponseDTO> createDiscussion(@PathVariable Long groupId,
                                                                  @RequestBody @Validated DiscussionRequestDTO discussionRequestDTO) {
        DiscussionResponseDTO discussionResponseDTO = discussionService.createDiscussion(groupId, discussionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(discussionResponseDTO);
    }

    @Operation(summary = "토론 목록 조회")
    @GetMapping("/groups/{groupId}/discussions")
    public ResponseEntity<Page<DiscussionResponseDTO>> getDiscussions(@PathVariable Long groupId, Pageable pageable) {
        Page<DiscussionResponseDTO> discussionPage = discussionService.getDiscussions(groupId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(discussionPage);
    }

    @Operation(summary = "토론 상세 조회")
    @GetMapping("/discussions/{discussionId}")
    public ResponseEntity<DiscussionResponseDTO> getDiscussionDetail(@PathVariable Long discussionId) {
        DiscussionResponseDTO discussionResponseDTO = discussionService.getDiscussionDetail(discussionId);
        return ResponseEntity.status(HttpStatus.OK).body(discussionResponseDTO);
    }

    @Operation(summary = "토론 수정")
    @PutMapping("/discussions/{discussionId}")
    public ResponseEntity<DiscussionResponseDTO> updateDiscussion(@PathVariable Long discussionId,
                                                                  @RequestBody @Validated DiscussionRequestDTO discussionRequestDTO) {
        DiscussionResponseDTO discussionResponseDTO = discussionService.updateDiscussion(discussionId, discussionRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(discussionResponseDTO);
    }

    @Operation(summary = "토론 삭제")
    @DeleteMapping("/discussions/{discussionId}")
    public ResponseEntity<Void> deleteDiscussion(@PathVariable Long discussionId) {
        discussionService.deleteDiscussion(discussionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토론 상태 변경")
    @PutMapping("/discussions/{discussionId}/status")
    public ResponseEntity<DiscussionResponseDTO> updateDiscussionStatus(@PathVariable Long discussionId,
                                                                        @RequestBody @Validated DiscussionStatusUpdateRequestDTO discussionStatusUpdateRequestDTO) {
        DiscussionResponseDTO discussionResponseDTO = discussionService.updateDiscussionStatus(discussionId, discussionStatusUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(discussionResponseDTO);
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/discussions/{discussionId}/comments")
    public ResponseEntity<DiscussionCommentResponseDTO> createComment(@PathVariable Long discussionId,
                                                                      @RequestBody @Validated DiscussionCommentRequestDTO discussionCommentRequestDTO) {
        DiscussionCommentResponseDTO discussionCommentResponseDTO = discussionService.createComment(discussionId, discussionCommentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(discussionCommentResponseDTO);
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/discussions/{discussionId}/comments")
    public ResponseEntity<List<DiscussionCommentResponseDTO>> getComments(@PathVariable Long discussionId) {
        List<DiscussionCommentResponseDTO> comments = discussionService.getComments(discussionId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/discussions/comments/{commentId}")
    public ResponseEntity<DiscussionCommentResponseDTO> updateComment(@PathVariable Long commentId,
                                                                      @RequestBody @Validated DiscussionCommentRequestDTO discussionCommentRequestDTO) {
        DiscussionCommentResponseDTO discussionCommentResponseDTO = discussionService.updateComment(commentId, discussionCommentRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(discussionCommentResponseDTO);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/discussions/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        discussionService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
