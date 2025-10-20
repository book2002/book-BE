package com.team2002.capstone.controller;

import com.team2002.capstone.dto.GroupCommentRequestDTO;
import com.team2002.capstone.dto.GroupCommentResponseDTO;
import com.team2002.capstone.dto.GroupPostRequestDTO;
import com.team2002.capstone.dto.GroupPostResponseDTO;
import com.team2002.capstone.service.GroupPostService;
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
public class GroupPostController {
    private final GroupPostService groupPostService;

    @Operation(summary = "모임 내 게시글 작성")
    @PostMapping("/groups/{groupId}/posts")
    public ResponseEntity<GroupPostResponseDTO> createPost(@PathVariable Long groupId, @RequestBody @Validated GroupPostRequestDTO groupPostRequestDTO) {
        GroupPostResponseDTO groupPostResponseDTO = groupPostService.createGroupPost(groupId, groupPostRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupPostResponseDTO);
    }

    @Operation(summary = "모임 내 게시글 목록 조회")
    @GetMapping("/groups/{groupId}/posts")
    public ResponseEntity<Page<GroupPostResponseDTO>> getPosts(@PathVariable Long groupId, Pageable pageable) {
        Page<GroupPostResponseDTO> postPage = groupPostService.getGroupPosts(groupId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(postPage);
    }

    @Operation(summary = "모임 내 게시글 상세 조회")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<GroupPostResponseDTO> getPostDetail(@PathVariable Long postId) {
        GroupPostResponseDTO groupPostResponseDTO = groupPostService.getPostDetail(postId);
        return ResponseEntity.status(HttpStatus.OK).body(groupPostResponseDTO);
    }

    @Operation(summary = "모임 내 게시글 수정")
    @PutMapping("/posts/{postId}")
    public ResponseEntity<GroupPostResponseDTO> updatePost(@PathVariable Long postId, @RequestBody @Validated GroupPostRequestDTO groupPostRequestDTO) {
        GroupPostResponseDTO groupPostResponseDTO = groupPostService.updatePost(postId, groupPostRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(groupPostResponseDTO);
    }

    @Operation(summary = "모임 내 게시글 삭제")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        groupPostService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<GroupCommentResponseDTO> createComment(@PathVariable Long postId, @RequestBody @Validated GroupCommentRequestDTO commentRequestDTO) {
        GroupCommentResponseDTO groupCommentResponseDTO = groupPostService.createComment(postId, commentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupCommentResponseDTO);
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<GroupCommentResponseDTO>> getComments(@PathVariable Long postId) {
        List<GroupCommentResponseDTO> comments = groupPostService.getComments(postId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<GroupCommentResponseDTO> updateComment(@PathVariable Long commentId, @RequestBody @Validated GroupCommentRequestDTO commentRequestDTO) {
        GroupCommentResponseDTO groupCommentResponseDTO = groupPostService.updateComment(commentId, commentRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(groupCommentResponseDTO);
    }

    @Operation(summary = "댓글 수정")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        groupPostService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
