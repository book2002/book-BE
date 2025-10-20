package com.team2002.capstone.repository;

import com.team2002.capstone.domain.GroupMember;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.domain.ReadingGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByProfileAndGroup(Profile profile, ReadingGroup readingGroup);
    int countByGroup(ReadingGroup readingGroup);
    List<GroupMember> findAllByProfile(Profile profile);
}
