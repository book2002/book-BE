package com.team2002.capstone.service;

import com.team2002.capstone.domain.*;
import com.team2002.capstone.dto.GroupCommentRequestDTO;
import com.team2002.capstone.dto.GroupCommentResponseDTO;
import com.team2002.capstone.dto.GroupPostRequestDTO;
import com.team2002.capstone.dto.GroupPostResponseDTO;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.*;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupPostService {
    private final GroupPostRepository groupPostRepository;
    private final GroupCommentRepository groupCommentRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final ReadingGroupRepository readingGroupRepository;
    private final GroupService groupService;

    @Transactional
    public GroupPostResponseDTO createGroupPost(Long groupId, GroupPostRequestDTO requestDTO) {
        Profile author = getCurrentProfile();
        ReadingGroup readingGroup = readingGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading group not found"));
        validateGroupMembership(author, readingGroup);

        GroupPost post = GroupPost.builder()
                .group(readingGroup)
                .author(author)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .build();
        groupPostRepository.save(post);

        return GroupPostResponseDTO.builder()
                .postId(post.getId())
                .groupId(post.getGroup().getId())
                .authorNickname(post.getAuthor().getNickname())
                .title(post.getTitle())
                .content(requestDTO.getContent())
                .createdAt(post.getCreatedAt())
                .commentCount(0)
                .isMyPost(true)
                .canModify(true)
                .build();
    }

    public Page<GroupPostResponseDTO> getGroupPosts(Long groupId, Pageable pageable) {
        Profile profile = getCurrentProfile();
        ReadingGroup group = readingGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading group not found"));
        validateGroupMembership(profile, group);

        Page<GroupPost> posts = groupPostRepository.findAllByGroupOrderByCreatedAtDesc(group, pageable);

        return posts.map(post -> {
            long commentCount = groupCommentRepository.countByPost(post);
            boolean isMyPost = post.getAuthor().getId() == profile.getId();
            boolean isOwner = post.getGroup().getOwner().getId() == profile.getId();
            return GroupPostResponseDTO.builder()
                    .postId(post.getId())
                    .groupId(group.getId())
                    .authorNickname(post.getAuthor().getNickname())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .commentCount(commentCount)
                    .isMyPost(isMyPost)
                    .canModify(isMyPost || isOwner)
                    .build();
        });
    }

    public GroupPostResponseDTO getPostDetail(Long postId) {
        Profile profile = getCurrentProfile();
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        validatePostMembership(post, getCurrentProfile());

        long commentCount = groupCommentRepository.countByPost(post);
        boolean isMyPost = post.getAuthor().getId() == profile.getId();
        boolean isOwner = post.getGroup().getOwner().getId() == profile.getId();

        return GroupPostResponseDTO.builder()
                .postId(post.getId())
                .groupId(post.getGroup().getId())
                .authorNickname(post.getAuthor().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .commentCount(commentCount)
                .isMyPost(isMyPost)
                .canModify(isMyPost || isOwner)
                .build();
    }

    @Transactional
    public GroupPostResponseDTO updatePost(Long postId, GroupPostRequestDTO requestDTO) {
        Profile author = getCurrentProfile();
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        validatePostPermission(author, post);

        post.setTitle(requestDTO.getTitle());
        post.setContent(requestDTO.getContent());

        long commentCount = groupCommentRepository.countByPost(post);
        boolean isMyPost = post.getAuthor().getId() == author.getId();
        boolean isOwner = post.getGroup().getOwner().getId() == author.getId();
        return GroupPostResponseDTO.builder()
                .postId(post.getId())
                .groupId(post.getGroup().getId())
                .authorNickname(post.getAuthor().getNickname())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .commentCount(commentCount)
                .isMyPost(isMyPost)
                .canModify(isMyPost || isOwner)
                .build();
    }

    @Transactional
    public void deletePost(Long postId) {
        Profile author = getCurrentProfile();
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        validatePostPermission(author, post);
        groupPostRepository.delete(post);
    }

    /*------댓글-----*/
    @Transactional
    public GroupCommentResponseDTO createComment(Long postId, GroupCommentRequestDTO requestDTO) {
        Profile author = getCurrentProfile();
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        validatePostMembership(post, author);

        GroupComment comment = GroupComment.builder()
                .post(post)
                .author(author)
                .content(requestDTO.getContent())
                .build();
        groupCommentRepository.save(comment);

        return GroupCommentResponseDTO.builder()
                .commentId(comment.getId())
                .postId(postId)
                .authorNickname(author.getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .isMyComment(true)
                .canModify(true)
                .build();
    }

    public List<GroupCommentResponseDTO> getComments(Long postId) {
        Profile profile = getCurrentProfile();
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        validatePostMembership(post, profile);

        List<GroupComment> comments = groupCommentRepository.findAllByPostOrderByCreatedAtDesc(post);
        return comments.stream()
                .map(comment -> {
                    boolean isMyComment = comment.getAuthor().getId() == profile.getId();
                    boolean isOwner = comment.getPost().getGroup().getOwner().getId() == profile.getId();
                    return GroupCommentResponseDTO.builder()
                            .commentId(comment.getId())
                            .postId(postId)
                            .authorNickname(comment.getAuthor().getNickname())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .isMyComment(isMyComment)
                            .canModify(isMyComment || isOwner)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupCommentResponseDTO updateComment(Long commentId, GroupCommentRequestDTO requestDTO) {
        Profile author = getCurrentProfile();
        GroupComment comment = groupCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        validateCommentPermission(author, comment);

        comment.setContent(requestDTO.getContent());
        groupCommentRepository.save(comment);

        boolean isMyComment = comment.getAuthor().getId() == author.getId();
        boolean isOwner = comment.getPost().getGroup().getOwner().getId() == author.getId();
        return GroupCommentResponseDTO.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .authorNickname(comment.getAuthor().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .isMyComment(isMyComment)
                .canModify(isMyComment || isOwner)
                .build();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Profile author = getCurrentProfile();
        GroupComment comment = groupCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        validateCommentPermission(author, comment);
        groupCommentRepository.delete(comment);
    }

    private void validateCommentPermission(Profile currentProfile, GroupComment comment) {
        ReadingGroup group = comment.getPost().getGroup();
        boolean isAuthor = (comment.getAuthor().getId() == currentProfile.getId());
        boolean isGroupOwner = (group.getOwner().getId() == currentProfile.getId());
        if (!isAuthor && !isGroupOwner) {
            throw new IllegalStateException("댓글 수정/삭제 권한이 없습니다."); // 권한은 작성자 또는 모임장에게만
        }
    }

    private void validatePostPermission(Profile currentProfile, GroupPost post) {
        ReadingGroup group = post.getGroup();
        boolean isAuthor = (post.getAuthor().getId() == currentProfile.getId());
        boolean isGroupOwner = (group.getOwner().getId() == currentProfile.getId());
        if (!isAuthor && !isGroupOwner) {
            throw new IllegalStateException("게시글 수정/삭제 권한이 없습니다."); // 권한은 작성자 또는 모임장에게만
        }
    }

    private void validateGroupMembership(Profile profile, ReadingGroup group) {
        Long groupId = group.getId();
        if (!groupService.isMemberOfGroup(groupId, profile)) {
            throw new IllegalStateException("해당 모임의 구성원만 접근할 수 있습니다.");
        }
    }

    private void validatePostMembership(GroupPost post, Profile currentProfile) {
        Long groupId = post.getGroup().getId();
        if (!groupService.isMemberOfGroup(groupId, currentProfile)) {
            throw new IllegalStateException("해당 모임의 구성원만 접근할 수 있습니다.");
        }
    }

    private Profile getCurrentProfile() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 사용자의 프로필을 찾을 수 없습니다."));
    }
}
