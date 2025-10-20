package com.team2002.capstone.repository;

import com.team2002.capstone.domain.ReadingGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingGroupRepository extends JpaRepository<ReadingGroup, Long> {
    Optional<ReadingGroup> findReadingGroupByName(String name);
}
