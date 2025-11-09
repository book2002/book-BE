package com.team2002.capstone.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BookShelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shelfId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference("profile-bookshelf")
    private Profile profile;


    @Column(nullable = false)
    private String shelfName;

    public BookShelf(String shelfName, Profile profile) {
        this.profile = profile;
        this.shelfName = shelfName;
    }
}
