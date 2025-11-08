package com.team2002.capstone.repository;

import com.team2002.capstone.domain.FavoriteLibrary;
import com.team2002.capstone.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteLibraryRepository extends JpaRepository<FavoriteLibrary, Long> {

    List<FavoriteLibrary> findByProfile(Profile profile);

    Optional<FavoriteLibrary> findByProfileAndLibName(Profile profile, String libName);
}
