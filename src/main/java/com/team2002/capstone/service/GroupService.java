package com.team2002.capstone.service;

import com.team2002.capstone.domain.GroupMember;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.domain.ReadingGroup;
import com.team2002.capstone.dto.GroupCreateRequestDTO;
import com.team2002.capstone.dto.GroupResponseDTO;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.GroupMemberRepository;
import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.repository.ProfileRepository;
import com.team2002.capstone.repository.ReadingGroupRepository;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

    private final ReadingGroupRepository readingGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public GroupResponseDTO createGroup(GroupCreateRequestDTO requestDTO) {
        Profile owner = getCurrentProfile();

        ReadingGroup readingGroup = ReadingGroup.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .goal(requestDTO.getGoal())
                .maxMembers(requestDTO.getMaxMembers())
                .owner(owner)
                .build();
        readingGroupRepository.save(readingGroup);

        GroupMember groupMember = GroupMember.builder()
                .group(readingGroup)
                .profile(owner)
                .build();
        groupMemberRepository.save(groupMember);

        return GroupResponseDTO.builder()
                .groupId(readingGroup.getId())
                .name(readingGroup.getName())
                .description(readingGroup.getDescription())
                .goal(readingGroup.getGoal())
                .ownerName(owner.getNickname())
                .maxMembers(readingGroup.getMaxMembers())
                .currentMembers(1)
                .build();
    }

    @Transactional
    public GroupResponseDTO joinGroup(Long groupId) {
        Profile profile = getCurrentProfile();
        ReadingGroup readingGroup = readingGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        if (groupMemberRepository.findByProfileAndGroup(profile, readingGroup).isEmpty()) {
            throw new IllegalStateException("이미 모임에 가입되어 있습니다.");
        }

        int currentCount = groupMemberRepository.countByGroup(readingGroup);
        if (currentCount >= readingGroup.getMaxMembers()) {
            throw new IllegalStateException("모임의 정원이 가득 찼습니다.");
        }

        GroupMember groupMember = GroupMember.builder()
                .group(readingGroup)
                .profile(profile)
                .build();
        groupMemberRepository.save(groupMember);

        int newCount = groupMemberRepository.countByGroup(readingGroup);
        return GroupResponseDTO.builder()
                .groupId(readingGroup.getId())
                .name(readingGroup.getName())
                .description(readingGroup.getDescription())
                .goal(readingGroup.getGoal())
                .ownerName(readingGroup.getOwner().getNickname())
                .maxMembers(readingGroup.getMaxMembers())
                .currentMembers(newCount)
                .build();
    }

    @Transactional
    public void leaveGroup(Long groupId) {
        Profile profile = getCurrentProfile();
        ReadingGroup readingGroup = readingGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        if (readingGroup.getOwner() != null && readingGroup.getOwner().getId() == profile.getId()) {
            throw new IllegalStateException("모임장은 모임을 탈퇴할 수 없습니다.");
        }

        GroupMember groupMember = groupMemberRepository.findByProfileAndGroup(profile, readingGroup)
                .orElseThrow(() -> new IllegalStateException("해당 모임에 가입되어 있지 않습니다."));

        groupMemberRepository.delete(groupMember);
    }

    @Transactional
    public List<GroupResponseDTO> getMyGroups() {
        Profile profile = getCurrentProfile();
        List<GroupMember> groups = groupMemberRepository.findAllByProfile(profile);

        return groups.stream()
                .map(GroupMember::getGroup)
                .map(group -> {
                    int currentCount = groupMemberRepository.countByGroup(group);
                    return GroupResponseDTO.builder()
                            .groupId(group.getId())
                            .name(group.getName())
                            .description(group.getDescription())
                            .goal(group.getGoal())
                            .ownerName(group.getOwner().getNickname())
                            .maxMembers(currentCount)
                            .currentMembers(currentCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GroupResponseDTO> getGroups() {
        List<ReadingGroup> groups = readingGroupRepository.findAll();
        Profile profile = getCurrentProfile();

        return groups.stream()
                .map(group -> {
                    int currentCount = groupMemberRepository.countByGroup(group);
                    boolean isJoined = groupMemberRepository.findByProfileAndGroup(profile, group).isPresent();
                    return GroupResponseDTO.builder()
                            .groupId(group.getId())
                            .name(group.getName())
                            .description(group.getDescription())
                            .goal(group.getGoal())
                            .ownerName(group.getOwner().getNickname())
                            .maxMembers(group.getMaxMembers())
                            .currentMembers(currentCount)
                            .isJoined(isJoined)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GroupResponseDTO> getPopularGroups() {
        List<ReadingGroup> groups = readingGroupRepository.findAll();
        Profile profile = getCurrentProfile();

        return groups.stream()
                .map(group -> {
                    int currentCount = groupMemberRepository.countByGroup(group);
                    boolean isJoined = groupMemberRepository.findByProfileAndGroup(profile, group).isPresent();
                    return GroupResponseDTO.builder()
                            .groupId(group.getId())
                            .name(group.getName())
                            .description(group.getDescription())
                            .goal(group.getGoal())
                            .ownerName(group.getOwner().getNickname())
                            .maxMembers(group.getMaxMembers())
                            .currentMembers(currentCount)
                            .isJoined(isJoined)
                            .build();
                })
                .filter(dto -> dto.getCurrentMembers() < dto.getMaxMembers())
                .sorted((dto1, dto2) -> Integer.compare(dto2.getCurrentMembers(), dto1.getCurrentMembers()))
                // 정원이 다 찬 모임 제외, 현원이 많은 순으로 정렬
                .collect(Collectors.toList());
    }

    public boolean isMemberOfGroup(Long groupId, Profile profile) {
        ReadingGroup group = readingGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("모임을 찾을 수 없습니다."));
        return groupMemberRepository.findByProfileAndGroup(profile, group).isPresent();
    }

    private Profile getCurrentProfile() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 사용자의 프로필을 찾을 수 없습니다."));
    }
}
