package com.team2002.capstone.repository;

import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookShelfItem_ItemId(Long itemId);
    List<Review> findByProfile(Profile profile);
}
