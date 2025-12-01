package com.team2002.capstone.service;

import com.team2002.capstone.domain.*;
import com.team2002.capstone.dto.*;
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
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final ReadingGroupRepository readingGroupRepository;
    private final GroupService groupService;

    @Transactional
    public DiscussionResponseDTO createDiscussion(Long groupId, DiscussionRequestDTO requestDTO) {
        Profile author = getCurrentProfile();
        ReadingGroup readingGroup = readingGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading group not found"));
        validateGroupMembership(author, readingGroup);

        Discussion discussion = Discussion.builder()
                .group(readingGroup)
                .author(author)
                .topicTitle(requestDTO.getTopicTitle())
                .topicContent(requestDTO.getTopicContent())
                .isClosed(false)
                .build();
        discussionRepository.save(discussion);

        return DiscussionResponseDTO.builder()
                .discussionId(discussion.getId())
                .groupId(discussion.getGroup().getId())
                .authorNickname(discussion.getAuthor().getNickname())
                .topicTitle(discussion.getTopicTitle())
                .topicContent(discussion.getTopicContent())
                .isClosed(discussion.isClosed())
                .createdAt(discussion.getCreatedAt())
                .commentCount(0)
                .isMyDiscussion(true)
                .canModify(true)
                .build();
    }

    public Page<DiscussionResponseDTO> getDiscussions(Long groupId, Pageable pageable) {
        Profile author = getCurrentProfile();
        ReadingGroup readingGroup = readingGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading group not found"));
        validateGroupMembership(author, readingGroup);

        Page<Discussion> discussions = discussionRepository.findAllByGroupOrderByCreatedAtDesc(readingGroup, pageable);
        return discussions.map(discussion -> {
            long commentCount = discussionCommentRepository.countByDiscussion(discussion);
            boolean isMyDiscussion = discussion.getAuthor().getId() == author.getId();
            boolean isOwner = discussion.getGroup().getOwner().getId() == author.getId();
            return DiscussionResponseDTO.builder()
                    .discussionId(discussion.getId())
                    .groupId(discussion.getGroup().getId())
                    .authorNickname(discussion.getAuthor().getNickname())
                    .topicTitle(discussion.getTopicTitle())
                    .topicContent(discussion.getTopicContent())
                    .isClosed(discussion.isClosed())
                    .createdAt(discussion.getCreatedAt())
                    .commentCount(commentCount)
                    .isMyDiscussion(isMyDiscussion)
                    .canModify(isMyDiscussion || isOwner)
                    .build();
        });
    }

    public DiscussionResponseDTO getDiscussionDetail(Long discussionId) {
        Profile profile = getCurrentProfile();
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        validateDiscussionAccess(profile, discussion);

        long commentCount = discussionCommentRepository.countByDiscussion(discussion);
        boolean isMyDiscussion = discussion.getAuthor().getId() == profile.getId();
        boolean isOwner = discussion.getGroup().getOwner().getId() == profile.getId();
        return DiscussionResponseDTO.builder()
                .discussionId(discussion.getId())
                .groupId(discussion.getGroup().getId())
                .authorNickname(discussion.getAuthor().getNickname())
                .topicTitle(discussion.getTopicTitle())
                .topicContent(discussion.getTopicContent())
                .isClosed(discussion.isClosed())
                .createdAt(discussion.getCreatedAt())
                .commentCount(commentCount)
                .isMyDiscussion(isMyDiscussion)
                .canModify(isMyDiscussion || isOwner)
                .build();
    }

    @Transactional
    public DiscussionResponseDTO updateDiscussion(Long discussionId, DiscussionRequestDTO requestDTO) {
        Profile profile = getCurrentProfile();
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        validateDiscussionAccess(profile, discussion);
        validateDiscussionPermission(profile, discussion);

        discussion.setTopicTitle(requestDTO.getTopicTitle());
        discussion.setTopicContent(requestDTO.getTopicContent());

        Long commentCount = discussionCommentRepository.countByDiscussion(discussion);
        boolean isMyDiscussion = discussion.getAuthor().getId() == profile.getId();
        boolean isOwner = discussion.getGroup().getOwner().getId() == profile.getId();
        return DiscussionResponseDTO.builder()
                .discussionId(discussion.getId())
                .groupId(discussion.getGroup().getId())
                .authorNickname(discussion.getAuthor().getNickname())
                .topicTitle(discussion.getTopicTitle())
                .topicContent(discussion.getTopicContent())
                .isClosed(discussion.isClosed())
                .createdAt(discussion.getCreatedAt())
                .commentCount(commentCount)
                .isMyDiscussion(isMyDiscussion)
                .canModify(isMyDiscussion || isOwner)
                .build();
    }

    @Transactional
    public void deleteDiscussion(Long discussionId) {
        Profile profile = getCurrentProfile();
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        validateDiscussionAccess(profile, discussion);
        validateDiscussionPermission(profile, discussion);
        discussionRepository.delete(discussion);
    }

    @Transactional
    public DiscussionResponseDTO updateDiscussionStatus(Long discussionId, DiscussionStatusUpdateRequestDTO requestDTO) {
        Profile profile = getCurrentProfile();
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        validateDiscussionPermission(profile, discussion);

        discussion.setClosed(requestDTO.getIsClosed());

        Long commentCount = discussionCommentRepository.countByDiscussion(discussion);
        boolean isMyDiscussion = discussion.getAuthor().getId() == profile.getId();
        boolean isOwner = discussion.getGroup().getOwner().getId() == profile.getId();
        return DiscussionResponseDTO.builder()
                .discussionId(discussion.getId())
                .groupId(discussion.getGroup().getId())
                .authorNickname(discussion.getAuthor().getNickname())
                .topicTitle(discussion.getTopicTitle())
                .topicContent(discussion.getTopicContent())
                .isClosed(discussion.isClosed())
                .createdAt(discussion.getCreatedAt())
                .commentCount(commentCount)
                .isMyDiscussion(isMyDiscussion)
                .canModify(isMyDiscussion || isOwner)
                .build();
    }

    /*------댓글-----*/
    @Transactional
    public DiscussionCommentResponseDTO createComment(Long discussionId, DiscussionCommentRequestDTO requestDTO) {
        Profile author = getCurrentProfile();
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        validateDiscussionAccess(author, discussion);

        if (discussion.isClosed()) {
            throw new IllegalStateException("Discussion is closed");
        }

        DiscussionComment comment = DiscussionComment.builder()
                .discussion(discussion)
                .author(author)
                .content(requestDTO.getContent())
                .build();
        discussionCommentRepository.save(comment);

        return DiscussionCommentResponseDTO.builder()
                .commentId(comment.getId())
                .discussionId(comment.getDiscussion().getId())
                .authorNickname(author.getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .isMyComment(true)
                .canModify(true)
                .build();
    }

    public List<DiscussionCommentResponseDTO> getComments(Long discussionId) {
        Profile profile = getCurrentProfile();
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        validateDiscussionAccess(profile, discussion);

        List<DiscussionComment> comments = discussionCommentRepository.findAllByDiscussionOrderByCreatedAtDesc(discussion);

        return comments.stream()
                .map(comment -> {
                    boolean isMyComment = comment.getAuthor().getId() == profile.getId();
                    boolean isOwner = comment.getDiscussion().getGroup().getOwner().getId() == profile.getId();
                    return DiscussionCommentResponseDTO.builder()
                            .commentId(comment.getId())
                            .discussionId(comment.getDiscussion().getId())
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
    public DiscussionCommentResponseDTO updateComment(Long commentId, DiscussionCommentRequestDTO requestDTO) {
        Profile author = getCurrentProfile();
        DiscussionComment comment = discussionCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        validateCommentPermission(author, comment);

        comment.setContent(requestDTO.getContent());
        discussionCommentRepository.save(comment);

        boolean isMyComment = comment.getAuthor().getId() == author.getId();
        boolean isOwner = comment.getDiscussion().getGroup().getOwner().getId() == author.getId();
        return DiscussionCommentResponseDTO.builder()
                .commentId(comment.getId())
                .discussionId(comment.getDiscussion().getId())
                .authorNickname(author.getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .isMyComment(isMyComment)
                .canModify(isMyComment || isOwner)
                .build();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Profile author = getCurrentProfile();
        DiscussionComment comment = discussionCommentRepository.findById(commentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        validateCommentPermission(author, comment);
        discussionCommentRepository.delete(comment);
    }

    private void validateDiscussionPermission(Profile profile, Discussion discussion) {
        ReadingGroup group = discussion.getGroup();
        boolean isAuthor = discussion.getAuthor().getId() == profile.getId();
        boolean isGroupOwner = group.getOwner().getId() == profile.getId();
        if (!isAuthor && !isGroupOwner) {
            throw new IllegalStateException("토론 수정/삭제/상태변경 권한이 없습니다.");
        }
    }

    private void validateCommentPermission(Profile currentProfile, DiscussionComment comment) {
        ReadingGroup group = comment.getDiscussion().getGroup();
        boolean isAuthor = (comment.getAuthor().getId() == currentProfile.getId());
        boolean isGroupOwner = (group.getOwner().getId() == currentProfile.getId());
        if (!isAuthor && !isGroupOwner) {
            throw new IllegalStateException("댓글 수정/삭제 권한이 없습니다."); // 권한은 작성자 또는 모임장에게만
        }
    }

    private void validateGroupMembership(Profile profile, ReadingGroup group) {
        if (!groupService.isMemberOfGroup(group.getId(), profile)) {
            throw new IllegalStateException("해당 모임의 구성원만 접근할 수 있습니다.");
        }
    }

    private void validateDiscussionAccess(Profile profile, Discussion discussion) {
        ReadingGroup group = discussion.getGroup();
        validateGroupMembership(profile, group);
    }

    private Profile getCurrentProfile() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 사용자의 프로필을 찾을 수 없습니다."));
    }
}
